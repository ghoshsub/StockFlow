package com.stockflow.backend.service;

import com.stockflow.backend.dto.product.ProductRequest;
import com.stockflow.backend.dto.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * ProductService — Service interface defining the Product module contract.
 */
public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(Long id);

    List<ProductResponse> getAllProducts();

    Page<ProductResponse> getAllProductsPaginated(Pageable pageable);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

    Page<ProductResponse> filterProducts(Long categoryId, Long brandId, Long supplierId, Long warehouseId, Boolean active, Pageable pageable);

    Page<ProductResponse> getLowStockProducts(Pageable pageable);
}
