package com.gijun.backend.repository.sis.recipe;

import com.gijun.backend.domain.sis.recipe.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByNameContainingOrCodeContaining(String name, String code);
}