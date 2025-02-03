package com.gijun.backend.repository.sis.product;

import com.gijun.backend.domain.sis.product.Category;
import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.sis.product.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryAndStatusOrderByNameAsc(Category category, ProductStatus status);
    Optional<Product> findByCode(String code);
    boolean existsByCodeOrName(String code, String name);
    Page<Product> findByNameContainingAndStatusOrderByNameAsc(String name, ProductStatus status, Pageable pageable);
    Page<Product> findByCategoryAndStatusOrderByNameAsc(Category category, ProductStatus status, Pageable pageable);
}