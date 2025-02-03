package com.gijun.backend.service.sis.product;

import com.gijun.backend.domain.sis.product.Category;
import com.gijun.backend.dto.product.CategoryDTO;
import com.gijun.backend.dto.product.CategoryHierarchyDto;
import com.gijun.backend.repository.sis.product.CategoryRepository;
import com.gijun.backend.service.KafkaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
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
    private final KafkaService kafkaService;

    @Transactional
    public CategoryDTO.CategoryResponse createCategory(CategoryDTO.CategoryCreateRequest request) {
        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
        }

        if (categoryRepository.existsByCodeOrName(request.getCode(), request.getName())) {
            throw new DuplicateKeyException("Category code or name already exists");
        }

        Category category = Category.builder()
                .code(request.getCode())
                .name(request.getName())
                .parent(parent)
                .orderNum(request.getOrderNum())
                .description(request.getDescription())
                .build();

        Category savedCategory = categoryRepository.save(category);
        kafkaService.sendMessage("category-events", "category.created", savedCategory);

        return CategoryDTO.CategoryResponse.from(savedCategory);
    }

    public Page<CategoryDTO.CategoryResponse> getCategories(Pageable pageable) {
        return categoryRepository.findByParentIsNullOrderByOrderNumAsc(pageable)
                .map(CategoryDTO.CategoryResponse::from);
    }

    public List<CategoryDTO.CategoryResponse> getSubcategories(Long parentId) {
        Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        return categoryRepository.findByParentOrderByOrderNumAsc(parent)
                .stream()
                .map(CategoryDTO.CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryDTO.CategoryResponse updateCategory(Long id, CategoryDTO.CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        Category parent = null;
        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category not found"));
        }

        category.updateInfo(
                request.getName(),
                parent,
                request.getOrderNum(),
                request.getDescription()
        );

        kafkaService.sendMessage("category-events", "category.updated", category);
        return CategoryDTO.CategoryResponse.from(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (!category.getProducts().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with products");
        }

        category.delete();
        kafkaService.sendMessage("category-events", "category.deleted", id);
    }
}