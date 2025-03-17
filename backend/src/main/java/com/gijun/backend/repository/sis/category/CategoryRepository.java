package com.gijun.backend.repository.sis.category;

import com.gijun.backend.domain.sis.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentOrderByOrderNumAsc(Category parent);

    Page<Category> findByParentIsNullOrderByOrderNumAsc(Pageable pageable);

    Optional<Category> findByCode(String code);

    boolean existsByCodeOrName(String code, String name);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.code) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Category> findByKeyword(String keyword);

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.parent WHERE c.id = :id")
    Optional<Category> findByIdWithParent(Long id);
}