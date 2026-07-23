package com.stockflow.backend.service;

import com.stockflow.backend.dto.inventory.StockInRequest;
import com.stockflow.backend.dto.purchase.PurchaseItemRequest;
import com.stockflow.backend.dto.purchase.PurchaseRequest;
import com.stockflow.backend.dto.purchase.PurchaseResponse;
import com.stockflow.backend.entity.*;
import com.stockflow.backend.exception.*;
import com.stockflow.backend.mapper.PurchaseMapper;
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
 * PurchaseServiceImpl — Core implementation of PurchaseService.
 * Handles purchase creation, subtotal & total calculations, unique purchase number generation,
 * and automatic integration with InventoryService (STOCK_IN).
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PurchaseMapper purchaseMapper;
    private final InventoryService inventoryService;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SUFFIX_LENGTH = 6;

    private String generateUniquePurchaseNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        for (int attempt = 0; attempt < 10; attempt++) {
            StringBuilder suffix = new StringBuilder();
            for (int i = 0; i < SUFFIX_LENGTH; i++) {
                suffix.append(CHARACTERS.charAt(ThreadLocalRandom.current().nextInt(CHARACTERS.length())));
            }
            String number = "PO-" + date + "-" + suffix;
            if (!purchaseRepository.existsByPurchaseNumberIgnoreCase(number)) {
                return number;
            }
        }
        throw new IllegalStateException("Failed to generate unique purchase number after 10 attempts.");
    }

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request, String username) {
        log.info("Creating purchase order for supplierId={}, warehouseId={}, user={}",
                request.getSupplierId(), request.getWarehouseId(), username);

        // 1. Resolve Supplier, Warehouse, and User
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .filter(Supplier::getActive)
                .orElseThrow(() -> new SupplierNotFoundException("Supplier not found or inactive with id: " + request.getSupplierId()));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .filter(Warehouse::getActive)
                .orElseThrow(() -> new WarehouseNotFoundException("Warehouse not found or inactive with id: " + request.getWarehouseId()));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // 2. Validate Invoice Number Uniqueness if provided
        if (request.getInvoiceNumber() != null && !request.getInvoiceNumber().isBlank()) {
            if (purchaseRepository.existsByInvoiceNumberIgnoreCase(request.getInvoiceNumber().trim())) {
                throw new DuplicateResourceException("Purchase with invoice number '" + request.getInvoiceNumber().trim() + "' already exists");
            }
        }

        // 3. Auto-generate Purchase Number
        String purchaseNumber = generateUniquePurchaseNumber();
        LocalDateTime now = LocalDateTime.now();

        // 4. Build Purchase Entity
        Purchase purchase = Purchase.builder()
                .purchaseNumber(purchaseNumber)
                .purchaseDate(now)
                .invoiceNumber(request.getInvoiceNumber() != null ? request.getInvoiceNumber().trim() : null)
                .paymentStatus(request.getPaymentStatus())
                .paymentMethod(request.getPaymentMethod())
                .remarks(request.getRemarks())
                .supplier(supplier)
                .warehouse(warehouse)
                .createdBy(user)
                .totalAmount(BigDecimal.ZERO)
                .purchaseItems(new ArrayList<>())
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        // 5. Process Purchase Items & Calculate Subtotals
        for (PurchaseItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .filter(Product::getActive)
                    .orElseThrow(() -> new ProductNotFoundException(itemReq.getProductId()));

            BigDecimal subtotal = itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            PurchaseItem item = PurchaseItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .subtotal(subtotal)
                    .build();

            purchase.addPurchaseItem(item);
        }

        purchase.setTotalAmount(totalAmount);
        Purchase savedPurchase = purchaseRepository.save(purchase);

        // 6. Automatically update Inventory via InventoryService (STOCK_IN)
        for (PurchaseItem item : savedPurchase.getPurchaseItems()) {
            StockInRequest stockInRequest = new StockInRequest();
            stockInRequest.setProductId(item.getProduct().getId());
            stockInRequest.setWarehouseId(warehouse.getId());
            stockInRequest.setQuantity(item.getQuantity());
            stockInRequest.setRemarks("Auto Stock In from Purchase #" + purchaseNumber);

            inventoryService.stockIn(stockInRequest, username);
        }

        log.info("Purchase created successfully — ID: {}, PO Number: {}, Total: {}",
                savedPurchase.getId(), savedPurchase.getPurchaseNumber(), savedPurchase.getTotalAmount());

        return purchaseMapper.toPurchaseResponse(savedPurchase);
    }

    @Override
    public PurchaseResponse getPurchaseById(Long id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase not found with id: " + id));
        return purchaseMapper.toPurchaseResponse(purchase);
    }

    @Override
    public List<PurchaseResponse> getAllPurchases() {
        return purchaseMapper.toPurchaseResponseList(purchaseRepository.findAll());
    }

    @Override
    public Page<PurchaseResponse> getAllPurchasesPaginated(Pageable pageable) {
        return purchaseMapper.toPurchaseResponsePage(purchaseRepository.findAll(pageable));
    }

    @Override
    public Page<PurchaseResponse> searchPurchases(
            String purchaseNumber, String supplierName, String invoiceNumber,
            String productKeyword, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) {
        return purchaseMapper.toPurchaseResponsePage(
                purchaseRepository.searchPurchases(purchaseNumber, supplierName, invoiceNumber, productKeyword, dateFrom, dateTo, pageable));
    }

    @Override
    public Page<PurchaseResponse> filterPurchases(
            Long supplierId, Long warehouseId, String paymentStatus,
            LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) {
        return purchaseMapper.toPurchaseResponsePage(
                purchaseRepository.filterPurchases(supplierId, warehouseId, paymentStatus, dateFrom, dateTo, pageable));
    }
}
