package com.bonita.recipe.domain.recipe.input;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class RecipeInput {

    @Nonnull
    private String name;

    @Nonnull
    private List<String> ingredients;

    @Nonnull
    private List<String> keywords;
}
