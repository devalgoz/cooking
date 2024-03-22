package com.bonita.recipe.domain.projection;

import com.bonita.recipe.domain.Ingredient;
import com.bonita.recipe.domain.Keyword;
import com.bonita.recipe.domain.recipe.Recipe;

import java.util.Comparator;
import java.util.List;

public class RecipeProjectionFactory {

    public static RecipeProjection createRecipeProjection(Recipe recipe) {
        return new RecipeProjection() {
            @Override
            public String getName() {
                return recipe.getName();
            }

            @Override
            public List<String> getIngrediens() {
                return recipe.getIngredients()
                        .stream()
                        .sorted(Comparator.comparing(Ingredient::getOrder))
                        .map(Ingredient::getName)
                        .toList();
            }

            @Override
            public List<String> getKeywords() {
                return recipe.getKeywords()
                        .stream()
                        .sorted(Comparator.comparing(Keyword::getOrder))
                        .map(Keyword::getName)
                        .toList();
            }

            @Override
            public String getAuthor() {
                return recipe.getAuthor().getName();
            }
        };
    }
}
