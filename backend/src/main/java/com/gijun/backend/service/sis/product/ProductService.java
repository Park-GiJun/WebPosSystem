package com.gijun.backend.service.sis.product;

import com.gijun.backend.domain.sis.product.Category;
import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.sis.product.ProductStatus;
import com.gijun.backend.dto.kafka.ProductEvent;
import com.gijun.backend.dto.product.ProductDTO;
import com.gijun.backend.repository.sis.product.CategoryRepository;
import com.gijun.backend.repository.sis.product.ProductRepository;
import com.gijun.backend.service.KafkaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final KafkaService kafkaService;

    @Transactional
    public ProductDTO.ProductResponse createProduct(ProductDTO.ProductCreateRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        if (productRepository.existsByCodeOrName(request.getCode(), request.getName())) {
            throw new DuplicateKeyException("Product code or name already exists");
        }

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
                .unit(request.getUnit())
                .build();

        Product savedProduct = productRepository.save(product);

        // 이벤트 데이터 생성 및 전송
        ProductEvent event = new ProductEvent("product.created", savedProduct);
//        kafkaService.sendMessage("product-events", event);

        return ProductDTO.ProductResponse.from(savedProduct);
    }
    public ProductDTO.ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return ProductDTO.ProductResponse.from(product);
    }

    public Page<ProductDTO.ProductResponse> getProducts(ProductDTO.ProductSearchRequest request, Pageable pageable) {
        Page<Product> products;

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            products = productRepository.findByCategoryAndStatusOrderByNameAsc(
                    category, ProductStatus.ON_SALE, pageable);
        } else if (StringUtils.hasText(request.getKeyword())) {
            products = productRepository.findByNameContainingAndStatusOrderByNameAsc(
                    request.getKeyword(), ProductStatus.ON_SALE, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(ProductDTO.ProductResponse::from);
    }

    @Transactional
    public ProductDTO.ProductResponse updateProduct(Long id, ProductDTO.ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        }

        product.update(
                request.getName(),
                request.getDescription(),
                category,
                request.getPrice(),
                request.getCostPrice(),
                request.getMinStock(),
                request.getMaxStock(),
                request.getUnit()
        );

        kafkaService.sendMessage("product-events", "product.updated", product);
        return ProductDTO.ProductResponse.from(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.delete();
        kafkaService.sendMessage("product-events", "product.deleted", id);
    }

    @Transactional
    public ProductDTO.ProductResponse updateStock(Long id, ProductDTO.StockUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        product.updateStock(request.getQuantity());

        Map<String, Object> stockEvent = Map.of(
                "productId", id,
                "quantity", request.getQuantity(),
                "note", request.getNote()
        );
        kafkaService.sendMessage("stock-events", "stock.updated", stockEvent);

        return ProductDTO.ProductResponse.from(product);
    }
}