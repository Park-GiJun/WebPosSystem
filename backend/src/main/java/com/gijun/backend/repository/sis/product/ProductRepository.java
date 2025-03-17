package com.gijun.backend.repository.sis.product;

import com.gijun.backend.domain.sis.category.Category;
import com.gijun.backend.domain.sis.category.ProductStatus;
import com.gijun.backend.domain.sis.category.ProductType;
import com.gijun.backend.domain.sis.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT p FROM Product p WHERE p.productType = :productType")
    Page<Product> findByProductType(@Param("productType") ProductType productType, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.productType = :productType")
    Page<Product> findByCategoryAndProductType(
            @Param("category") Category category,
            @Param("productType") ProductType productType,
            Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (:status IS NULL OR p.status = :status) " +
            "AND (:productType IS NULL OR p.productType = :productType) " +
            "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.code) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(
            @Param("categoryId") Long categoryId,
            @Param("status") ProductStatus status,
            @Param("productType") ProductType productType,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.recipeComponents rc LEFT JOIN FETCH rc.ingredient WHERE p.id = :id")
    Optional<Product> findByIdWithRecipeComponents(@Param("id") Long id);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.setProductItems si LEFT JOIN FETCH si.item WHERE p.id = :id")
    Optional<Product> findByIdWithSetProductItems(@Param("id") Long id);
}