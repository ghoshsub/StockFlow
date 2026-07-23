package com.stockflow.backend.dto.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ChartDataResponse — Generic label-value pair for React UI charts (Pie, Bar, Line).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic label-value pair for React charts")
public class ChartDataResponse {

    @Schema(description = "Chart label (e.g. Month name, Category name, Warehouse name)", example = "Electronics")
    private String label;

    @Schema(description = "Primary numerical value", example = "15000.00")
    private BigDecimal value;

    @Schema(description = "Secondary numerical value (optional, e.g., count or cost)", example = "45")
    private BigDecimal secondaryValue;
}
