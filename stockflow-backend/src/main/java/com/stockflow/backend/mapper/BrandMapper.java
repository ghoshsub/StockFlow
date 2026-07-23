package com.stockflow.backend.mapper;

import com.stockflow.backend.dto.brand.BrandRequest;
import com.stockflow.backend.dto.brand.BrandResponse;
import com.stockflow.backend.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * BrandMapper — Manual mapper between Brand entity and its DTOs.
 *
 * Responsibilities:
 *  toEntity()         — BrandRequest  → Brand (for create)
 *  updateEntity()     — BrandRequest  → Brand (for update, modifies in-place)
 *  toResponse()       — Brand         → BrandResponse
 *  toResponseList()   — List<Brand>   → List<BrandResponse>
 *  toResponsePage()   — Page<Brand>   → Page<BrandResponse>
 */
@Component
public class BrandMapper {

    /**
     * Convert a BrandRequest DTO into a new Brand entity.
     * The `active` field is always set to true on creation.
     */
    public Brand toEntity(BrandRequest request) {
        return Brand.builder()
                .name(request.getName().trim())
                .description(request.getDescription() != null
                        ? request.getDescription().trim() : null)
                .active(true)
                .build();
    }

    /**
     * Apply changes from a BrandRequest onto an existing Brand entity.
     */
    public void updateEntity(BrandRequest request, Brand category) {
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription() != null
                ? request.getDescription().trim() : null);
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
    }

    /**
     * Convert a Brand entity to a BrandResponse DTO.
     */
    public BrandResponse toResponse(Brand brand) {
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .active(brand.getActive())
                .createdAt(brand.getCreatedAt())
                .updatedAt(brand.getUpdatedAt())
                .build();
    }

    /**
     * Convert a list of Brand entities to a list of BrandResponse DTOs.
     */
    public List<BrandResponse> toResponseList(List<Brand> brands) {
        return brands.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert a Page<Brand> to Page<BrandResponse>.
     */
    public Page<BrandResponse> toResponsePage(Page<Brand> page) {
        return page.map(this::toResponse);
    }
}
