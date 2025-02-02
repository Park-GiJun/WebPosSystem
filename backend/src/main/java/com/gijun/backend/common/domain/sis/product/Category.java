package com.gijun.backend.common.domain.sis.product;

import com.gijun.backend.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;  // 카테고리 이름

    @Column(length = 500)
    private String description;  // 카테고리 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parentCategory;  // 상위 카테고리

    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> subCategories = new HashSet<>();  // 하위 카테고리 목록

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();  // 해당 카테고리에 속한 제품 목록

    @Builder
    public Category(String name, String description, Category parentCategory) {
        this.name = name;
        this.description = description;
        this.parentCategory = parentCategory;
    }

    // 카테고리 정보 업데이트
    public void updateInfo(String name, String description, Category parentCategory) {
        this.name = name;
        this.description = description;
        this.parentCategory = parentCategory;
    }

    // 하위 카테고리 추가
    public void addSubCategory(Category subCategory) {
        subCategory.parentCategory = this;
        this.subCategories.add(subCategory);
    }

    // 하위 카테고리 제거
    public void removeSubCategory(Category subCategory) {
        this.subCategories.remove(subCategory);
        subCategory.parentCategory = null;
    }
}
