package com.bonita.recipe.domain.recipe.service;

import com.bonita.recipe.domain.Author;
import com.bonita.recipe.domain.Ingredient;
import com.bonita.recipe.domain.Keyword;
import com.bonita.recipe.domain.projection.RecipeProjection;
import com.bonita.recipe.domain.projection.RecipeProjectionFactory;
import com.bonita.recipe.domain.recipe.Recipe;
import com.bonita.recipe.domain.recipe.input.RecipeInput;
import com.bonita.recipe.infrastructure.exception.InvalidInputException;
import com.bonita.recipe.infrastructure.exception.ResourceNotFoundException;
import com.bonita.recipe.infrastructure.exception.UnauthorizedException;
import com.bonita.recipe.infrastructure.helper.AuthenticationHelper;
import com.bonita.recipe.infrastructure.repository.AuthorRepository;
import com.bonita.recipe.infrastructure.repository.IngredientRespository;
import com.bonita.recipe.infrastructure.repository.KeywordRepository;
import com.bonita.recipe.infrastructure.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final AuthorRepository authorRepository;
    private final KeywordRepository keywordRepository;
    private final IngredientRespository ingredientRespository;
    private final AuthenticationHelper authenticationHelper;

    public List<RecipeProjection> findAll() {
        return recipeRepository.findAll()
                .stream()
                .map(RecipeProjectionFactory::createRecipeProjection)
                .toList();
    }

    public RecipeProjection findById(final Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        return RecipeProjectionFactory.createRecipeProjection(recipe);
    }

    public List<RecipeProjection> search(String author, List<String> keywords, List<String> ingredients) {
        return StreamSupport
                .stream(recipeRepository.search(author, keywords, ingredients).spliterator(), false)
                .map(RecipeProjectionFactory::createRecipeProjection)
                .toList();
    }

    @Transactional
    public Long create(final RecipeInput input) {
        recipeRepository
                .findByNameIgnoreCase(input.getName())
                .ifPresent(recipe -> {
                            throw new InvalidInputException("A recipe within the same name already exists");
                        }
                );

        String authorName = Optional
                .ofNullable(authenticationHelper.getCurrentUser())
                .orElseThrow(UnauthorizedException::new);
        Author author = authorRepository.findByNameIgnoreCase(authorName).orElse(new Author(authorName));

        Recipe recipe = Recipe.builder()
                .name(input.getName())
                .author(author)
                .ingredients(getOrCreateIngredients(input.getIngredients()))
                .keywords(getOrCreateKeywords(input.getKeywords()))
                .build();

        return recipeRepository.save(recipe).getId();
    }

    @Transactional
    public void update(Long id, final RecipeInput input) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);

        recipe.setName(input.getName());
        recipe.setIngredients(getOrCreateIngredients(input.getIngredients()));
        recipe.setKeywords(getOrCreateKeywords(input.getKeywords()));
        recipeRepository.save(recipe);
    }

    @Transactional
    public void delete(Long id) {
        recipeRepository.delete(recipeRepository.findById(id).orElseThrow(ResourceNotFoundException::new));
    }

    private Set<Ingredient> getOrCreateIngredients(List<String> ingredients) {
        return ingredients
                .stream()
                .map(ingredient -> ingredientRespository
                        .findByNameIgnoreCase(ingredient)
                        .orElse(new Ingredient(ingredient, ingredients.indexOf(ingredient)))
                )
                .collect(Collectors.toSet());
    }

    private Set<Keyword> getOrCreateKeywords(List<String> keywords) {
        return keywords
                .stream()
                .map(keyword -> keywordRepository
                        .findByNameIgnoreCase(keyword)
                        .orElse(new Keyword(keyword, keywords.indexOf(keyword)))
                )
                .collect(Collectors.toSet());
    }

}
