package com.gijun.backend.service.sis.product;

import com.gijun.backend.domain.sis.category.Category;
import com.gijun.backend.domain.sis.category.ProductStatus;
import com.gijun.backend.domain.sis.category.ProductType;
import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.sis.product.SetProductItem;
import com.gijun.backend.domain.sis.recipe.RecipeComponent;
import com.gijun.backend.dto.product.ProductDTO;
import com.gijun.backend.repository.sis.category.CategoryRepository;
import com.gijun.backend.repository.sis.product.ProductRepository;
import com.gijun.backend.repository.sis.product.SetProductItemRepository;
import com.gijun.backend.repository.sis.recipe.RecipeComponentRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RecipeComponentRepository recipeComponentRepository;
    private final SetProductItemRepository setProductItemRepository;

    @Transactional
    public ProductDTO.ProductResponse createProduct(ProductDTO.ProductCreateRequest request) {
        if (productRepository.existsByCodeOrName(request.getCode(), request.getName())) {
            throw new DuplicateResourceException("Product code or name already exists");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .stock(request.getStock())
                .minStock(request.getMinStock())
                .maxStock(request.getMaxStock())
                .status(ProductStatus.ON_SALE)
                .isTaxable(request.getIsTaxable() != null ? request.getIsTaxable() : true)
                .unit(request.getUnit() != null ? request.getUnit() : request.getProductType().equals(ProductType.RAW_MATERIAL) ? null : null)
                .productType(request.getProductType())
                .barcode(request.getBarcode())
                .imageUrl(request.getImageUrl())
                .build();

        Product savedProduct = productRepository.save(product);

        if (ProductType.RECIPE_PRODUCT.equals(request.getProductType()) && request.getRecipeComponents() != null) {
            for (ProductDTO.RecipeComponentRequest componentRequest : request.getRecipeComponents()) {
                Product ingredient = productRepository.findById(componentRequest.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + componentRequest.getIngredientId()));

                RecipeComponent component = new RecipeComponent(
                        savedProduct,
                        ingredient,
                        componentRequest.getQuantity(),
                        componentRequest.getUnit() != null ? componentRequest.getUnit() : ingredient.getUnit()
                );

                recipeComponentRepository.save(component);
            }
        }

        if (ProductType.SET_PRODUCT.equals(request.getProductType()) && request.getSetProductItems() != null) {
            for (ProductDTO.SetProductItemRequest itemRequest : request.getSetProductItems()) {
                Product item = productRepository.findById(itemRequest.getItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product item not found: " + itemRequest.getItemId()));

                SetProductItem setItem = new SetProductItem(
                        savedProduct,
                        item,
                        itemRequest.getQuantity()
                );

                setProductItemRepository.save(setItem);
            }
        }

        return getProductResponseById(savedProduct.getId());
    }

    public ProductDTO.ProductResponse getProduct(Long id) {
        return getProductResponseById(id);
    }

    public Page<ProductDTO.ProductResponse> getProducts(ProductDTO.ProductSearchRequest request, Pageable pageable) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        Page<Product> products = productRepository.searchProducts(
                request.getCategoryId(),
                request.getStatus(),
                request.getProductType(),
                request.getKeyword(),
                pageable
        );

        return products.map(ProductDTO.ProductResponse::from);
    }

    @Transactional
    public ProductDTO.ProductResponse updateProduct(Long id, ProductDTO.ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        product.update(
                request.getName(),
                request.getDescription(),
                category,
                request.getPrice(),
                request.getCostPrice(),
                request.getMinStock(),
                request.getMaxStock(),
                request.getUnit(),
                request.getStatus(),
                null,
                request.getBarcode(),
                request.getImageUrl()
        );

        if (ProductType.RECIPE_PRODUCT.equals(product.getProductType()) && request.getRecipeComponents() != null) {
            // Clear existing components
            recipeComponentRepository.deleteByProduct(product);

            for (ProductDTO.RecipeComponentRequest componentRequest : request.getRecipeComponents()) {
                Product ingredient = productRepository.findById(componentRequest.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + componentRequest.getIngredientId()));

                RecipeComponent component = new RecipeComponent(
                        product,
                        ingredient,
                        componentRequest.getQuantity(),
                        componentRequest.getUnit() != null ? componentRequest.getUnit() : ingredient.getUnit()
                );

                recipeComponentRepository.save(component);
            }
        }

        if (ProductType.SET_PRODUCT.equals(product.getProductType()) && request.getSetProductItems() != null) {
            setProductItemRepository.deleteBySetProduct(product);

            for (ProductDTO.SetProductItemRequest itemRequest : request.getSetProductItems()) {
                Product item = productRepository.findById(itemRequest.getItemId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product item not found: " + itemRequest.getItemId()));

                SetProductItem setItem = new SetProductItem(
                        product,
                        item,
                        itemRequest.getQuantity()
                );

                setProductItemRepository.save(setItem);
            }
        }

        return getProductResponseById(id);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productRepository.delete(product);
    }

    @Transactional
    public ProductDTO.ProductResponse updateStock(Long id, ProductDTO.StockUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.updateStock(request.getQuantity());
        return getProductResponseById(id);
    }

    public List<ProductDTO.ProductResponse> getProductsByType(ProductType productType) {
        List<Product> products = productRepository.findByProductType(productType, Pageable.unpaged()).getContent();
        List<ProductDTO.ProductResponse> responses = new ArrayList<>();

        for (Product product : products) {
            responses.add(ProductDTO.ProductResponse.from(product));
        }

        return responses;
    }

    private ProductDTO.ProductResponse getProductResponseById(Long id) {
        Product product;

        if (ProductType.RECIPE_PRODUCT.equals(productRepository.findById(id).get().getProductType())) {
            product = productRepository.findByIdWithRecipeComponents(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        } else if (ProductType.SET_PRODUCT.equals(productRepository.findById(id).get().getProductType())) {
            product = productRepository.findByIdWithSetProductItems(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        } else {
            product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        }

        return ProductDTO.ProductResponse.from(product);
    }
}