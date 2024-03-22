package com.bonita.recipe.data;

import com.bonita.recipe.domain.projection.RecipeProjection;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class RecipeProjectionImpl implements RecipeProjection {

    private String name;
    private String author;
    private List<String> keywords;
    private List<String> ingrediens;

}
