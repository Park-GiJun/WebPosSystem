package com.gijun.backend.repository.sis.product;

import com.gijun.backend.domain.sis.product.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentOrderByOrderNumAsc(Category parent);
    Optional<Category> findByCode(String code);
    boolean existsByCodeOrName(String code, String name);
    Page<Category> findByParentIsNullOrderByOrderNumAsc(Pageable pageable);
}