package com.stockflow.backend.service;

import com.stockflow.backend.dto.inventory.StockOutRequest;
import com.stockflow.backend.dto.sale.SaleItemRequest;
import com.stockflow.backend.dto.sale.SaleRequest;
import com.stockflow.backend.dto.sale.SaleResponse;
import com.stockflow.backend.entity.*;
import com.stockflow.backend.exception.*;
import com.stockflow.backend.mapper.SaleMapper;
import com.stockflow.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * SaleServiceImpl — Core implementation of SaleService.
 * Enforces pre-validation of product active status & stock availability.
 * Computes subtotals & final total (sum(subtotal) - discount + tax).
 * Automatically updates Inventory via InventoryService (STOCK_OUT) inside a single transaction.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SaleMapper saleMapper;
    private final InventoryService inventoryService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SUFFIX_LENGTH = 6;

    private String generateUniqueSaleNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder suffix = new StringBuilder();
            for (int i = 0; i < SUFFIX_LENGTH; i++) {
                suffix.append(CHARACTERS.charAt(ThreadLocalRandom.current().nextInt(CHARACTERS.length())));
            }
            String number = "SO-" + date + "-" + suffix;
            if (!saleRepository.existsBySaleNumberIgnoreCase(number)) {
                return number;
            }
        }
        throw new IllegalStateException("Failed to generate unique sale number after 10 attempts.");
    }

    @Override
    @Transactional
    public SaleResponse createSale(SaleRequest request, String username) {
        log.info("Processing sale for customer='{}', warehouseId={}, user={}",
                request.getCustomerName(), request.getWarehouseId(), username);

        // 1. Resolve Warehouse & User
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .filter(Warehouse::getActive)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found or inactive with id: " + request.getWarehouseId()));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // 2. Pre-validate all products & stock availability BEFORE mutating state
        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(itemReq.getProductId()));

            if (!Boolean.TRUE.equals(product.getActive())) {
                throw new IllegalArgumentException("Product '" + product.getName() + "' (ID: " + product.getId() + ") is inactive and cannot be sold.");
            }

            if (product.getQuantity() < itemReq.getQuantity()) {
                throw new InsufficientStockException(product.getName(), product.getQuantity(), itemReq.getQuantity());
            }
        }

        // 3. Auto-generate Sale Number
        String saleNumber = generateUniqueSaleNumber();
        LocalDateTime now = LocalDateTime.now();

        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal tax = request.getTax() != null ? request.getTax() : BigDecimal.ZERO;

        // 4. Build Sale Entity
        Sale sale = Sale.builder()
                .saleNumber(saleNumber)
                .customerName(request.getCustomerName() != null ? request.getCustomerName().trim() : null)
                .customerEmail(request.getCustomerEmail() != null ? request.getCustomerEmail().trim() : null)
                .customerPhone(request.getCustomerPhone() != null ? request.getCustomerPhone().trim() : null)
                .saleDate(now)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(request.getPaymentStatus())
                .discount(discount)
                .tax(tax)
                .remarks(request.getRemarks())
                .warehouse(warehouse)
                .createdBy(user)
                .totalAmount(BigDecimal.ZERO)
                .saleItems(new ArrayList<>())
                .build();

        BigDecimal sumSubtotals = BigDecimal.ZERO;

        // 5. Process Sale Items & Subtotals
        for (SaleItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId()).get(); // Already pre-validated

            BigDecimal subtotal = itemReq.getSellingPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            sumSubtotals = sumSubtotals.add(subtotal);

            SaleItem item = SaleItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .sellingPrice(itemReq.getSellingPrice())
                    .subtotal(subtotal)
                    .build();

            sale.addSaleItem(item);
        }

        // Total = sum(subtotals) - discount + tax
        BigDecimal totalAmount = sumSubtotals.subtract(discount).add(tax);
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            totalAmount = BigDecimal.ZERO;
        }

        sale.setTotalAmount(totalAmount);
        Sale savedSale = saleRepository.save(sale);

        // 6. Update Inventory via InventoryService (STOCK_OUT)
        for (SaleItem item : savedSale.getSaleItems()) {
            StockOutRequest stockOutRequest = new StockOutRequest();
            stockOutRequest.setProductId(item.getProduct().getId());
            stockOutRequest.setWarehouseId(warehouse.getId());
            stockOutRequest.setQuantity(item.getQuantity());
            stockOutRequest.setRemarks("Auto Stock Out from Sales Order #" + saleNumber);

            inventoryService.stockOut(stockOutRequest, username);
        }

        log.info("Sale processed successfully — ID: {}, SO Number: {}, Total: {}",
                savedSale.getId(), savedSale.getSaleNumber(), savedSale.getTotalAmount());

        return saleMapper.toSaleResponse(savedSale);
    }

    @Override
    public SaleResponse getSaleById(Long id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
        return saleMapper.toSaleResponse(sale);
    }

    @Override
    public List<SaleResponse> getAllSales() {
        return saleMapper.toSaleResponseList(saleRepository.findAll());
    }

    @Override
    public Page<SaleResponse> getAllSalesPaginated(Pageable pageable) {
        return saleMapper.toSaleResponsePage(saleRepository.findAll(pageable));
    }

    @Override
    public Page<SaleResponse> searchSales(
            String saleNumber, String customerName, String customerEmail,
            String productKeyword, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) {
        return saleMapper.toSaleResponsePage(
                saleRepository.searchSales(saleNumber, customerName, customerEmail, productKeyword, dateFrom, dateTo, pageable));
    }

    @Override
    public Page<SaleResponse> filterSales(
            String paymentStatus, String paymentMethod, String customerName,
            LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) {
        return saleMapper.toSaleResponsePage(
                saleRepository.filterSales(paymentStatus, paymentMethod, customerName, dateFrom, dateTo, pageable));
    }
}
