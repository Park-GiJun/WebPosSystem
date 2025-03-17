package com.gijun.backend.service.sis.category;

import com.gijun.backend.domain.sis.category.Category;
import com.gijun.backend.dto.category.CategoryDTO;
import com.gijun.backend.repository.sis.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public CategoryDTO.CategoryResponse createCategory(CategoryDTO.CategoryCreateRequest request) {
        // Check if category code or name already exists
        if (categoryRepository.existsByCodeOrName(request.getCode(), request.getName())) {
            throw new DuplicateResourceException("Category code or name already exists");
        }

        // Find parent category if parentId is provided
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
        }

        // Create new category
        Category category = Category.builder()
                .code(request.getCode())
                .name(request.getName())
                .parent(parent)
                .orderNum(request.getOrderNum())
                .description(request.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return CategoryDTO.CategoryResponse.from(savedCategory);
    }

    public Page<CategoryDTO.CategoryResponse> getCategories(Pageable pageable) {
        return categoryRepository.findByParentIsNullOrderByOrderNumAsc(pageable)
                .map(CategoryDTO.CategoryResponse::from);
    }

    public List<CategoryDTO.CategoryResponse> getSubcategories(Long parentId) {
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        return categoryRepository.findByParentOrderByOrderNumAsc(parent)
                .stream()
                .map(CategoryDTO.CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO.CategoryResponse updateCategory(Long id, CategoryDTO.CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found"));
        }

        category.updateInfo(
                request.getName(),
                parent,
                request.getOrderNum(),
                request.getDescription()
        );

        return CategoryDTO.CategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with products");
        }

        categoryRepository.delete(category);
    }

    public List<CategoryDTO.CategoryResponse> searchCategories(String keyword) {
        return categoryRepository.findByKeyword(keyword)
                .stream()
                .map(CategoryDTO.CategoryResponse::from)
                .collect(Collectors.toList());
    }
}