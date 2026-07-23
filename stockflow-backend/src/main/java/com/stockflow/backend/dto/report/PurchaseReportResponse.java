package com.stockflow.backend.dto.report;

import com.stockflow.backend.dto.purchase.PurchaseResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * PurchaseReportResponse — DTO payload for Purchase Report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Purchase orders report")
public class PurchaseReportResponse {

    @Schema(description = "Total purchase orders in period", example = "25")
    private Long totalOrders;

    @Schema(description = "Total purchase spend in period", example = "18500.00")
    private BigDecimal totalSpend;

    @Schema(description = "List of matching purchase records")
    private List<PurchaseResponse> purchases;
}
