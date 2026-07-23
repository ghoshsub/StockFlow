package com.stockflow.backend.service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.stockflow.backend.dto.product.ProductResponse;
import com.stockflow.backend.dto.purchase.PurchaseResponse;
import com.stockflow.backend.dto.report.FinancialSummaryReportResponse;
import com.stockflow.backend.dto.report.InventoryReportResponse;
import com.stockflow.backend.dto.report.PurchaseReportResponse;
import com.stockflow.backend.dto.report.SalesReportResponse;
import com.stockflow.backend.dto.sale.SaleResponse;
import com.stockflow.backend.mapper.ProductMapper;
import com.stockflow.backend.mapper.PurchaseMapper;
import com.stockflow.backend.mapper.SaleMapper;
import com.stockflow.backend.repository.ProductRepository;
import com.stockflow.backend.repository.PurchaseRepository;
import com.stockflow.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.awt.Color;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ReportServiceImpl — Generates JSON report data and compiles downloadable PDF, Excel (.xlsx), and CSV files.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final ProductRepository productRepository;
    private final PurchaseRepository purchaseRepository;
    private final SaleRepository saleRepository;
    private final ProductMapper productMapper;
    private final PurchaseMapper purchaseMapper;
    private final SaleMapper saleMapper;

    @Override
    public InventoryReportResponse getInventoryReport() {
        log.debug("Generating Inventory Report");
        List<ProductResponse> allProducts = productMapper.toResponseList(productRepository.findAllByActiveTrue());
        List<ProductResponse> lowStock = productMapper.toResponseList(productRepository.findLowStockProductsList());
        List<ProductResponse> outOfStock = productMapper.toResponseList(productRepository.findOutOfStockProductsList());

        return InventoryReportResponse.builder()
                .totalProducts(productRepository.countByActiveTrue())
                .totalInventoryValue(productRepository.calculateTotalInventoryValue())
                .lowStockCount(productRepository.countLowStockProducts())
                .outOfStockCount(productRepository.countOutOfStockProducts())
                .currentInventory(allProducts)
                .lowStockProducts(lowStock)
                .outOfStockProducts(outOfStock)
                .build();
    }

    @Override
    public PurchaseReportResponse getPurchaseReport(Long supplierId, Long warehouseId, LocalDateTime dateFrom, LocalDateTime dateTo) {
        log.debug("Generating Purchase Report: supplierId={}, warehouseId={}", supplierId, warehouseId);
        List<PurchaseResponse> purchases = purchaseMapper.toPurchaseResponseList(
                purchaseRepository.filterPurchases(supplierId, warehouseId, null, dateFrom, dateTo, Pageable.unpaged()).getContent());

        BigDecimal totalSpend = purchases.stream()
                .map(PurchaseResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return PurchaseReportResponse.builder()
                .totalOrders((long) purchases.size())
                .totalSpend(totalSpend)
                .purchases(purchases)
                .build();
    }

    @Override
    public SalesReportResponse getSalesReport(String customerName, String productKeyword, LocalDateTime dateFrom, LocalDateTime dateTo) {
        log.debug("Generating Sales Report: customerName={}, product={}", customerName, productKeyword);
        List<SaleResponse> sales = saleMapper.toSaleResponseList(
                saleRepository.searchSales(null, customerName, null, productKeyword, dateFrom, dateTo, Pageable.unpaged()).getContent());

        BigDecimal totalRevenue = sales.stream()
                .map(SaleResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SalesReportResponse.builder()
                .totalSales((long) sales.size())
                .totalRevenue(totalRevenue)
                .sales(sales)
                .build();
    }

    @Override
    public FinancialSummaryReportResponse getFinancialSummaryReport() {
        log.debug("Generating Financial Summary Report");
        BigDecimal totalRevenue = saleRepository.calculateTotalRevenue();
        BigDecimal totalPurchaseCost = purchaseRepository.calculateTotalPurchaseCost();
        BigDecimal grossProfit = totalRevenue.subtract(totalPurchaseCost);
        BigDecimal currentInventoryValue = productRepository.calculateTotalInventoryValue();

        return FinancialSummaryReportResponse.builder()
                .totalRevenue(totalRevenue)
                .totalPurchaseCost(totalPurchaseCost)
                .grossProfit(grossProfit)
                .currentInventoryValue(currentInventoryValue)
                .build();
    }

    // ── Export Handling ───────────────────────────────────────────────────────

    @Override
    public byte[] exportInventoryReport(String format) {
        InventoryReportResponse report = getInventoryReport();
        return switch (format.toLowerCase()) {
            case "pdf" -> generateInventoryPdf(report);
            case "xlsx", "excel" -> generateInventoryExcel(report);
            case "csv" -> generateInventoryCsv(report);
            default -> throw new IllegalArgumentException("Unsupported export format: " + format);
        };
    }

    @Override
    public byte[] exportPurchaseReport(Long supplierId, Long warehouseId, LocalDateTime dateFrom, LocalDateTime dateTo, String format) {
        PurchaseReportResponse report = getPurchaseReport(supplierId, warehouseId, dateFrom, dateTo);
        return switch (format.toLowerCase()) {
            case "pdf" -> generatePurchasePdf(report);
            case "xlsx", "excel" -> generatePurchaseExcel(report);
            case "csv" -> generatePurchaseCsv(report);
            default -> throw new IllegalArgumentException("Unsupported export format: " + format);
        };
    }

    @Override
    public byte[] exportSalesReport(String customerName, String productKeyword, LocalDateTime dateFrom, LocalDateTime dateTo, String format) {
        SalesReportResponse report = getSalesReport(customerName, productKeyword, dateFrom, dateTo);
        return switch (format.toLowerCase()) {
            case "pdf" -> generateSalesPdf(report);
            case "xlsx", "excel" -> generateSalesExcel(report);
            case "csv" -> generateSalesCsv(report);
            default -> throw new IllegalArgumentException("Unsupported export format: " + format);
        };
    }

    @Override
    public byte[] exportFinancialSummaryReport(String format) {
        FinancialSummaryReportResponse report = getFinancialSummaryReport();
        return switch (format.toLowerCase()) {
            case "pdf" -> generateFinancialPdf(report);
            case "xlsx", "excel" -> generateFinancialExcel(report);
            case "csv" -> generateFinancialCsv(report);
            default -> throw new IllegalArgumentException("Unsupported export format: " + format);
        };
    }

    // ── PDF Builders (OpenPDF) ────────────────────────────────────────────────

    private byte[] generateInventoryPdf(InventoryReportResponse report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
            document.add(new Paragraph("StockFlow — Inventory Status Report", headerFont));
            document.add(new Paragraph("Generated: " + LocalDateTime.now()));
            document.add(new Paragraph("Total Products: " + report.getTotalProducts() + " | Valuation: $" + report.getTotalInventoryValue()));
            document.add(new Paragraph("Low Stock Count: " + report.getLowStockCount() + " | Out of Stock Count: " + report.getOutOfStockCount()));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            addTableHeader(table, new String[]{"SKU", "Name", "Category", "Quantity", "Buying Price", "Selling Price"});

            for (ProductResponse p : report.getCurrentInventory()) {
                table.addCell(p.getSku());
                table.addCell(p.getName());
                table.addCell(p.getCategory() != null ? p.getCategory().getName() : "-");
                table.addCell(String.valueOf(p.getQuantity()));
                table.addCell("$" + p.getBuyingPrice());
                table.addCell("$" + p.getSellingPrice());
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Inventory PDF", e);
        }
    }

    private byte[] generatePurchasePdf(PurchaseReportResponse report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            document.add(new Paragraph("StockFlow — Purchase Orders Report", headerFont));
            document.add(new Paragraph("Total Orders: " + report.getTotalOrders() + " | Total Spend: $" + report.getTotalSpend()));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTableHeader(table, new String[]{"PO Number", "Supplier", "Warehouse", "Date", "Total Amount"});

            for (PurchaseResponse p : report.getPurchases()) {
                table.addCell(p.getPurchaseNumber());
                table.addCell(p.getSupplierName());
                table.addCell(p.getWarehouseName());
                table.addCell(p.getPurchaseDate() != null ? p.getPurchaseDate().toString() : "-");
                table.addCell("$" + p.getTotalAmount());
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Purchase PDF", e);
        }
    }

    private byte[] generateSalesPdf(SalesReportResponse report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.GREEN);
            document.add(new Paragraph("StockFlow — Sales Revenue Report", headerFont));
            document.add(new Paragraph("Total Sales: " + report.getTotalSales() + " | Total Revenue: $" + report.getTotalRevenue()));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            addTableHeader(table, new String[]{"SO Number", "Customer", "Payment Method", "Status", "Total Amount"});

            for (SaleResponse s : report.getSales()) {
                table.addCell(s.getSaleNumber());
                table.addCell(s.getCustomerName() != null ? s.getCustomerName() : "Walk-in");
                table.addCell(s.getPaymentMethod());
                table.addCell(s.getPaymentStatus());
                table.addCell("$" + s.getTotalAmount());
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Sales PDF", e);
        }
    }

    private byte[] generateFinancialPdf(FinancialSummaryReportResponse report) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.MAGENTA);
            document.add(new Paragraph("StockFlow — Financial Summary Report", headerFont));
            document.add(new Paragraph("Generated: " + LocalDateTime.now()));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(80);
            addTableHeader(table, new String[]{"Financial Metric", "Amount ($)"});

            table.addCell("Total Revenue");
            table.addCell("$" + report.getTotalRevenue());

            table.addCell("Total Purchase Cost");
            table.addCell("$" + report.getTotalPurchaseCost());

            table.addCell("Gross Profit");
            table.addCell("$" + report.getGrossProfit());

            table.addCell("Current Inventory Valuation");
            table.addCell("$" + report.getCurrentInventoryValue());

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Financial PDF", e);
        }
    }

    private void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    // ── Excel Builders (Apache POI) ──────────────────────────────────────────

    private byte[] generateInventoryExcel(InventoryReportResponse report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Inventory Report");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"SKU", "Name", "Category", "Quantity", "Buying Price", "Selling Price"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (ProductResponse p : report.getCurrentInventory()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getSku());
                row.createCell(1).setCellValue(p.getName());
                row.createCell(2).setCellValue(p.getCategory() != null ? p.getCategory().getName() : "-");
                row.createCell(3).setCellValue(p.getQuantity());
                row.createCell(4).setCellValue(p.getBuyingPrice().doubleValue());
                row.createCell(5).setCellValue(p.getSellingPrice().doubleValue());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Inventory Excel file", e);
        }
    }

    private byte[] generatePurchaseExcel(PurchaseReportResponse report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Purchase Orders");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"PO Number", "Supplier", "Warehouse", "Date", "Total Amount"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (PurchaseResponse p : report.getPurchases()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getPurchaseNumber());
                row.createCell(1).setCellValue(p.getSupplierName());
                row.createCell(2).setCellValue(p.getWarehouseName());
                row.createCell(3).setCellValue(p.getPurchaseDate() != null ? p.getPurchaseDate().toString() : "-");
                row.createCell(4).setCellValue(p.getTotalAmount().doubleValue());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Purchase Excel file", e);
        }
    }

    private byte[] generateSalesExcel(SalesReportResponse report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Sales Orders");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"SO Number", "Customer", "Payment Method", "Status", "Total Amount"};
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (SaleResponse s : report.getSales()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getSaleNumber());
                row.createCell(1).setCellValue(s.getCustomerName() != null ? s.getCustomerName() : "Walk-in");
                row.createCell(2).setCellValue(s.getPaymentMethod());
                row.createCell(3).setCellValue(s.getPaymentStatus());
                row.createCell(4).setCellValue(s.getTotalAmount().doubleValue());
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Sales Excel file", e);
        }
    }

    private byte[] generateFinancialExcel(FinancialSummaryReportResponse report) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Financial Summary");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Financial Metric");
            headerRow.createCell(1).setCellValue("Amount");

            Object[][] data = {
                {"Total Revenue", report.getTotalRevenue().doubleValue()},
                {"Total Purchase Cost", report.getTotalPurchaseCost().doubleValue()},
                {"Gross Profit", report.getGrossProfit().doubleValue()},
                {"Current Inventory Valuation", report.getCurrentInventoryValue().doubleValue()}
            };

            int rowIdx = 1;
            for (Object[] rowData : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(rowData[0].toString());
                row.createCell(1).setCellValue((Double) rowData[1]);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Financial Excel file", e);
        }
    }

    // ── CSV Builders ──────────────────────────────────────────────────────────

    private byte[] generateInventoryCsv(InventoryReportResponse report) {
        StringBuilder csv = new StringBuilder("SKU,Name,Category,Quantity,BuyingPrice,SellingPrice\n");
        for (ProductResponse p : report.getCurrentInventory()) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",%d,%.2f,%.2f\n",
                    p.getSku(), p.getName(), p.getCategory() != null ? p.getCategory().getName() : "",
                    p.getQuantity(), p.getBuyingPrice(), p.getSellingPrice()));
        }
        return csv.toString().getBytes();
    }

    private byte[] generatePurchaseCsv(PurchaseReportResponse report) {
        StringBuilder csv = new StringBuilder("PurchaseNumber,Supplier,Warehouse,Date,TotalAmount\n");
        for (PurchaseResponse p : report.getPurchases()) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%.2f\n",
                    p.getPurchaseNumber(), p.getSupplierName(), p.getWarehouseName(), p.getPurchaseDate(), p.getTotalAmount()));
        }
        return csv.toString().getBytes();
    }

    private byte[] generateSalesCsv(SalesReportResponse report) {
        StringBuilder csv = new StringBuilder("SaleNumber,Customer,PaymentMethod,PaymentStatus,TotalAmount\n");
        for (SaleResponse s : report.getSales()) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%.2f\n",
                    s.getSaleNumber(), s.getCustomerName() != null ? s.getCustomerName() : "", s.getPaymentMethod(), s.getPaymentStatus(), s.getTotalAmount()));
        }
        return csv.toString().getBytes();
    }

    private byte[] generateFinancialCsv(FinancialSummaryReportResponse report) {
        StringBuilder csv = new StringBuilder("Metric,Amount\n");
        csv.append(String.format("Total Revenue,%.2f\n", report.getTotalRevenue()));
        csv.append(String.format("Total Purchase Cost,%.2f\n", report.getTotalPurchaseCost()));
        csv.append(String.format("Gross Profit,%.2f\n", report.getGrossProfit()));
        csv.append(String.format("Current Inventory Valuation,%.2f\n", report.getCurrentInventoryValue()));
        return csv.toString().getBytes();
    }
}
