package com.stockflow.backend.service;

import com.stockflow.backend.dto.category.CategoryRequest;
import com.stockflow.backend.dto.category.CategoryResponse;
import com.stockflow.backend.entity.Category;
import com.stockflow.backend.exception.CategoryNotFoundException;
import com.stockflow.backend.exception.DuplicateResourceException;
import com.stockflow.backend.mapper.CategoryMapper;
import com.stockflow.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CategoryServiceImpl — Concrete implementation of CategoryService.
 *
 * Follows the Single Responsibility Principle:
 *  - All business rules for categories live here.
 *  - Data access is delegated to CategoryRepository.
 *  - Mapping is delegated to CategoryMapper.
 *  - No HTTP / controller concerns belong here.
 *
 * Transaction strategy:
 *  - Class-level @Transactional(readOnly = true) → all read operations are optimised.
 *  - Write methods override with @Transactional (readOnly = false) as needed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // ── CREATE ────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * Business rules:
     *  1. Name uniqueness is enforced (case-insensitive).
     *  2. `active` is always set to true on creation.
     *  3. Name and description are trimmed of leading/trailing whitespace.
     */
    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating category with name: '{}'", request.getName());

        // Uniqueness guard — case-insensitive comparison
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new DuplicateResourceException(
                    "Category with name '" + request.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);

        log.info("Category created successfully — id: {}, name: '{}'", saved.getId(), saved.getName());
        return categoryMapper.toResponse(saved);
    }

    // ── READ (single) ─────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * Uses findById() which searches ALL categories (including soft-deleted ones).
     * This allows admins to view and restore deleted categories by ID.
     */
    @Override
    public CategoryResponse getCategoryById(Long id) {
        log.debug("Fetching category by id: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        return categoryMapper.toResponse(category);
    }

    // ── READ (all) ────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * Returns all active categories — useful for populating dropdowns/selects.
     */
    @Override
    public List<CategoryResponse> getAllCategories() {
        log.debug("Fetching all active categories");
        List<Category> categories = categoryRepository.findAllByActiveTrue();
        return categoryMapper.toResponseList(categories);
    }

    /**
     * {@inheritDoc}
     *
     * Returns a Page of active categories with full pagination metadata.
     * Pageable carries: page index (0-based), size, sort field, sort direction.
     */
    @Override
    public Page<CategoryResponse> getAllCategoriesPaginated(Pageable pageable) {
        log.debug("Fetching categories — page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Category> page = categoryRepository.findAllByActiveTrue(pageable);
        return categoryMapper.toResponsePage(page);
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * Business rules:
     *  1. Category must exist (active OR inactive — admin can update soft-deleted ones).
     *  2. New name must not conflict with any OTHER category (case-insensitive).
     *  3. If request.active is provided, it can reactivate a soft-deleted category.
     *  4. The entity is modified in-place → JPA's dirty-checking issues the UPDATE.
     */
    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        log.info("Updating category id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        // Name uniqueness check — exclude the current category's own name
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new DuplicateResourceException(
                    "Category with name '" + request.getName() + "' already exists");
        }

        categoryMapper.updateEntity(request, category);
        // No explicit save() needed — @Transactional + JPA dirty-checking handles it.
        // But calling save() explicitly is fine and makes intent clear:
        Category updated = categoryRepository.save(category);

        log.info("Category updated — id: {}, name: '{}'", updated.getId(), updated.getName());
        return categoryMapper.toResponse(updated);
    }

    // ── DELETE (soft) ─────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * Soft delete strategy:
     *  - Sets active = false; the row stays in the DB.
     *  - All queries that show "visible" categories filter on active = true,
     *    so the deleted category disappears from normal views.
     *  - The category can be restored by calling update() with active = true.
     *  - This preserves FK integrity: if products reference this category,
     *    they are not orphaned.
     */
    @Override
    @Transactional
    public void deleteCategory(Long id) {
        log.info("Soft-deleting category id: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getActive()) {
            log.warn("Category id: {} is already inactive (soft-deleted)", id);
            // Idempotent — no exception, just log and return
            return;
        }

        category.setActive(false);
        categoryRepository.save(category);

        log.info("Category id: {} soft-deleted successfully", id);
    }

    // ── SEARCH ────────────────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * Performs a case-insensitive partial match on name AND description.
     * Empty keyword falls back to returning all active categories.
     */
    @Override
    public Page<CategoryResponse> searchCategories(String keyword, Pageable pageable) {
        log.debug("Searching categories with keyword: '{}' — page: {}, size: {}",
                keyword, pageable.getPageNumber(), pageable.getPageSize());

        // Null/blank keyword → return all active (treat as empty search)
        if (keyword == null || keyword.isBlank()) {
            return getAllCategoriesPaginated(pageable);
        }

        Page<Category> results = categoryRepository.searchByKeyword(keyword.trim(), pageable);
        return categoryMapper.toResponsePage(results);
    }
}
