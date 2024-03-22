package com.bonita.recipe.data;

import com.bonita.recipe.domain.recipe.input.RecipeInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

public class DataGenerator {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static RecipeInput[] RECIPES;

    static {
        try {
            RECIPES = MAPPER.readValue(
                    new ClassPathResource("data.json").getInputStream(),
                    RecipeInput[].class
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
