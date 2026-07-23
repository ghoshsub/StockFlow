package com.stockflow.backend.mapper;

import com.stockflow.backend.dto.category.CategoryRequest;
import com.stockflow.backend.dto.category.CategoryResponse;
import com.stockflow.backend.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CategoryMapper — Manual mapper between Category entity and its DTOs.
 *
 * Why manual mapping instead of MapStruct?
 *  - No additional dependency required.
 *  - Full explicit control over what fields are mapped.
 *  - Easy to debug — no annotation-processor magic.
 *  - Future: can be replaced with MapStruct for large-scale projects.
 *
 * Responsibilities:
 *  toEntity()         — CategoryRequest  → Category (for create)
 *  updateEntity()     — CategoryRequest  → Category (for update, modifies in-place)
 *  toResponse()       — Category         → CategoryResponse
 *  toResponseList()   — List<Category>   → List<CategoryResponse>
 *  toResponsePage()   — Page<Category>   → Page<CategoryResponse>
 */
@Component
public class CategoryMapper {

    /**
     * Convert a CategoryRequest DTO into a new Category entity.
     * The `active` field is always set to true on creation regardless of request value.
     *
     * @param request the validated inbound DTO
     * @return a new (unpersisted) Category entity
     */
    public Category toEntity(CategoryRequest request) {
        return Category.builder()
                .name(request.getName().trim())
                .description(request.getDescription() != null
                        ? request.getDescription().trim() : null)
                .active(true)   // always active on creation
                .build();
    }

    /**
     * Apply changes from a CategoryRequest onto an existing Category entity.
     * Used by the update operation — we modify the existing managed entity
     * so JPA tracks the change and issues an UPDATE on flush.
     *
     * @param request the validated inbound update DTO
     * @param category the existing entity loaded from DB
     */
    public void updateEntity(CategoryRequest request, Category category) {
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription() != null
                ? request.getDescription().trim() : null);
        if (request.getActive() != null) {
            category.setActive(request.getActive());
        }
    }

    /**
     * Convert a Category entity to a CategoryResponse DTO.
     * This is the ONLY place where entity fields are read for outbound mapping.
     *
     * @param category the persisted entity
     * @return a fully-populated CategoryResponse
     */
    public CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    /**
     * Convert a list of Category entities to a list of CategoryResponse DTOs.
     *
     * @param categories the list of entities from the DB
     * @return list of response DTOs
     */
    public List<CategoryResponse> toResponseList(List<Category> categories) {
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convert a Page<Category> to Page<CategoryResponse>.
     * Preserves all pagination metadata (page number, size, total elements, total pages).
     *
     * @param page the Spring Data page of entities
     * @return a Page of response DTOs
     */
    public Page<CategoryResponse> toResponsePage(Page<Category> page) {
        return page.map(this::toResponse);
    }
}
