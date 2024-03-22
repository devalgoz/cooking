package com.bonita.recipe.service;

import com.bonita.recipe.infrastructure.repository.IngredientRespository;
import com.bonita.recipe.infrastructure.repository.KeywordRepository;
import com.bonita.recipe.infrastructure.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final IngredientRespository ingredientRespository;
    private final KeywordRepository keywordRepository;
    private final RecipeRepository recipeRepository;


    @Transactional
    public void clear() {
        recipeRepository.deleteAllInBatch();
        ingredientRespository.deleteAllInBatch();
        keywordRepository.deleteAllInBatch();
    }
}
