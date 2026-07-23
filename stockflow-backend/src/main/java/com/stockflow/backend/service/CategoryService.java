package com.stockflow.backend.service;

import com.stockflow.backend.dto.category.CategoryRequest;
import com.stockflow.backend.dto.category.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * CategoryService — Service interface defining the Category module's contract.
 *
 * Programming to an interface (not the implementation) allows:
 *  - Easy unit testing via mocking.
 *  - Clean dependency injection without coupling to the concrete class.
 *  - Future: multiple implementations (e.g. cached vs non-cached).
 *
 * All methods return CategoryResponse DTOs — the entity never escapes this layer.
 */
public interface CategoryService {

    /**
     * Create a new category.
     * Throws DuplicateResourceException if the name already exists.
     *
     * @param request validated request DTO
     * @return the persisted category as a response DTO
     */
    CategoryResponse createCategory(CategoryRequest request);

    /**
     * Retrieve a category by its primary key.
     * Throws CategoryNotFoundException if not found.
     *
     * @param id the category ID
     * @return the category response DTO
     */
    CategoryResponse getCategoryById(Long id);

    /**
     * Retrieve all active categories (non-paginated).
     * Suitable for dropdown/select lists in the frontend.
     *
     * @return list of all active categories
     */
    List<CategoryResponse> getAllCategories();

    /**
     * Retrieve a paginated and sorted list of all active categories.
     *
     * @param pageable page number, page size, and sort parameters
     * @return a Page containing CategoryResponse objects and metadata
     */
    Page<CategoryResponse> getAllCategoriesPaginated(Pageable pageable);

    /**
     * Update an existing category by its ID.
     * Throws CategoryNotFoundException if not found.
     * Throws DuplicateResourceException if the new name conflicts with another category.
     *
     * @param id      the category ID to update
     * @param request the validated update payload
     * @return the updated category response DTO
     */
    CategoryResponse updateCategory(Long id, CategoryRequest request);

    /**
     * Soft-delete a category by setting active = false.
     * The row is NOT physically removed from the database.
     * Throws CategoryNotFoundException if the category doesn't exist.
     *
     * @param id the category ID to soft-delete
     */
    void deleteCategory(Long id);

    /**
     * Search categories by keyword (partial match on name or description).
     * Only returns active categories.
     *
     * @param keyword  the search term
     * @param pageable pagination and sort parameters
     * @return a Page of matching CategoryResponse objects
     */
    Page<CategoryResponse> searchCategories(String keyword, Pageable pageable);
}
