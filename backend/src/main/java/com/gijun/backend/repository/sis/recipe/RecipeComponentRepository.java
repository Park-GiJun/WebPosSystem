package com.gijun.backend.repository.sis.recipe;

import com.gijun.backend.domain.sis.product.Product;
import com.gijun.backend.domain.sis.recipe.RecipeComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeComponentRepository extends JpaRepository<RecipeComponent, Long> {

    List<RecipeComponent> findByProduct(Product product);

    void deleteByProductAndIngredient(Product product, Product ingredient);

    void deleteByProduct(Product product);
}