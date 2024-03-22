package com.bonita.recipe.domain.projection;

import java.util.List;

public interface RecipeProjection {

    String getName();
    List<String> getIngrediens();

    List<String> getKeywords();

    String getAuthor();
}
