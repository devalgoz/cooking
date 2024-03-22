package com.bonita.recipe.infrastructure.controller;

import com.bonita.recipe.domain.projection.RecipeProjection;
import com.bonita.recipe.domain.recipe.input.RecipeInput;
import com.bonita.recipe.domain.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/rest/recipes")
@RequiredArgsConstructor
@OpenAPIDefinition(info = @Info(title = "Recipes API", version = "v1"))
@SecurityRequirement(name = "basicAuth")
public class RecipeRestController {

    private final RecipeService recipeService;

    @GetMapping
    public List<RecipeProjection> findAll() {
        return recipeService.findAll();
    }

    @GetMapping("/{id}")
    public RecipeProjection findById(@PathVariable("id") Long id) {
        return recipeService.findById(id);
    }

    @GetMapping("/search")
    public List<RecipeProjection> search(String author, String[] keywords, String[] ingredients) {
        return recipeService.search(author, Arrays.stream(keywords).toList(), Arrays.stream(ingredients).toList());
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody RecipeInput recipeInput) {
        Long id = recipeService.create(recipeInput);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id, @RequestBody RecipeInput recipeInput) {
        recipeService.update(id, recipeInput);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        recipeService.delete(id);
    }
}
