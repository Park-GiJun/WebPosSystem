package com.gijun.backend.repository.sis.product;

import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.sis.product.SetProductItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SetProductItemRepository extends JpaRepository<SetProductItem, Long> {

    List<SetProductItem> findBySetProduct(Product setProduct);

    void deleteBySetProductAndItem(Product setProduct, Product item);

    void deleteBySetProduct(Product setProduct);
}