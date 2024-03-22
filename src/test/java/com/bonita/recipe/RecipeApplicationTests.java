package com.bonita.recipe;

import com.bonita.recipe.data.RecipeProjectionImpl;
import com.bonita.recipe.domain.projection.RecipeProjection;
import com.bonita.recipe.domain.recipe.input.RecipeInput;
import com.bonita.recipe.service.DatabaseService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.bonita.recipe.data.DataGenerator.RECIPES;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RecipeApplicationTests {

    private static final String FAST_FOOD = "Fast food";
    private static final String ITALY = "Italy";
    private static final String TOMATE = "tomate";
    private static final String JAY = "Jay";
    private static final String RAY = "Ray";
    private static final String PASSWORD = "secret1234";
    private static final String ROOT = "";
    private static final Long ID = 12345L;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private DatabaseService databaseService;

    @LocalServerPort
    private int port;


    @BeforeEach
    @Transactional
    public void clearDatabase() {
        databaseService.clear();
        clearAuthentication();
    }

    @Test
    void findAll_SHOULD_THROW_HTTPCLIENTERROREXCEPTION_UNAUTHORIZED() {
        assertThrows(HttpClientErrorException.Unauthorized.class, () -> restTemplate.getForObject(buildUrl(ROOT), RecipeProjection[].class));
    }

    @Test
    void findAll_SHOULD_RETURN_EMPTY_NONNULL_RESULT() {
        setAuthentication(JAY, PASSWORD);
        RecipeProjection[] recipes = restTemplate
                .exchange(buildUrl(""), HttpMethod.GET, null, RecipeProjection[].class)
                .getBody();
        assertNotNull(recipes);
        assertEquals(0, recipes.length);
    }

    @Test
    void create_SHOULD_RETURN_OK() {
        setAuthentication(JAY, PASSWORD);
        Long id = createRecipe(RECIPES[0]);
        assertNotNull(id);
    }

    @Test
    void create_SHOULD_THROW_INVALID_INPUT_EXCEPTION() {
        setAuthentication(JAY, PASSWORD);
        createRecipe(RECIPES[0]);
        assertThrows(HttpClientErrorException.Conflict.class, () -> createRecipe(RECIPES[0]));
    }

    @Test
    void findById_SHOULD_THROW_NOT_FOUND_EXCEPTION() {
        setAuthentication(JAY, PASSWORD);
        assertThrows(HttpClientErrorException.NotFound.class, () -> findRecipe(ID));
    }

    @Test
    void findById_OK() {
        setAuthentication(JAY, PASSWORD);
        Long id = createRecipe(RECIPES[0]);
        RecipeProjection recipeProjection = findRecipe(id);
        assertNotNull(recipeProjection);
        verify(JAY, recipeProjection, RECIPES[0]);
    }

    @Test
    void update_SHOULD_THROW_NOT_FOUND_EXCEPTION() {
        setAuthentication(JAY, PASSWORD);
        assertThrows(HttpClientErrorException.NotFound.class, () -> updateRecipe(ID, RECIPES[0]));
    }

    @Test
    void update_OK() {
        setAuthentication(RAY, PASSWORD);
        Long id = createRecipe(RECIPES[0]);
        updateRecipe(id, RECIPES[1]);
        RecipeProjection recipeProjection = findRecipe(id);
        verify(RAY, recipeProjection, RECIPES[1]);
    }

    @Test
    void delete_SHOULD_THROW_NOT_FOUND_EXCEPTION() {
        setAuthentication(JAY, PASSWORD);
        assertThrows(HttpClientErrorException.NotFound.class, () -> deleteRecipe(ID));
    }

    @Test
    void delete_OK() {
        setAuthentication(JAY, PASSWORD);
        Long id = createRecipe(RECIPES[0]);
        RecipeProjection recipeProjection = findRecipe(id);
        assertNotNull(recipeProjection);
        deleteRecipe(id);
        assertThrows(HttpClientErrorException.NotFound.class, () -> findRecipe(ID));
    }

    @Test
    void search_OK() {
        setAuthentication(JAY, PASSWORD);
        createRecipe(RECIPES[0]);
        createRecipe(RECIPES[1]);
        createRecipe(RECIPES[2]);
        clearAuthentication();

        setAuthentication(RAY, PASSWORD);
        createRecipe(RECIPES[3]);

        RecipeProjection[] recipeProjections = search(null, List.of(FAST_FOOD), null);
        assertEquals(recipeProjections.length, 3);
        assertTrue(
                Arrays
                        .stream(recipeProjections)
                        .allMatch(recipeProjection ->
                                recipeProjection
                                        .getKeywords()
                                        .stream()
                                        .anyMatch(keyword -> keyword.contains(FAST_FOOD))
                        )
        );

        recipeProjections = search(JAY, List.of(ITALY), List.of(TOMATE));
        assertEquals(recipeProjections.length, 2);
        assertTrue(
                Arrays
                        .stream(recipeProjections)
                        .allMatch(recipeProjection ->
                                recipeProjection
                                        .getKeywords()
                                        .stream()
                                        .anyMatch(keyword -> keyword.contains(ITALY))
                                &&
                                recipeProjection
                                        .getIngrediens()
                                        .stream()
                                        .anyMatch(keyword -> keyword.contains(TOMATE))
                        )
        );

    }

    private RecipeProjection[] search(String author, List<String> keywords, List<String> ingredients) {
        return restTemplate.getForObject(
                buildUrl("/search?author={author}&keywords={keywords}&ingredients={ingredients}"),
                RecipeProjectionImpl[].class,
                author,
                join(keywords),
                join(ingredients)
        );
    }

    private String join(List<String> values) {
        return Optional
                .ofNullable(values)
                .map(strings -> String.join(",", strings))
                .orElse(null);
    }


    private Long createRecipe(RecipeInput recipeInput) {
        return restTemplate
                .exchange(buildUrl(""), HttpMethod.POST, new HttpEntity<>(recipeInput), Long.class)
                .getBody();
    }

    private void updateRecipe(Long id, RecipeInput recipeInput) {
        restTemplate.exchange(buildUrl("/%s".formatted(id)), HttpMethod.PUT, new HttpEntity<>(recipeInput), Void.class);
    }

    private RecipeProjection findRecipe(Long id) {
        return restTemplate
                .exchange(buildUrl("/%s".formatted(id)), HttpMethod.GET, null, RecipeProjectionImpl.class)
                .getBody();
    }

    private void deleteRecipe(Long id) {
        restTemplate.exchange(buildUrl("/%s".formatted(id)), HttpMethod.DELETE, null, Void.class);
    }

    private void verify(String authorName, RecipeProjection recipeProjection, RecipeInput recipeInput) {
        assertEquals(recipeInput.getName(), recipeProjection.getName());
        assertEquals(recipeInput.getKeywords(), recipeProjection.getKeywords());
        assertEquals(recipeInput.getIngredients(), recipeProjection.getIngrediens());
        assertEquals(authorName, recipeProjection.getAuthor());
    }

    private String buildUrl(String suffix) {
        return "http://localhost:%s/api/rest/recipes%s".formatted(port, suffix);
    }

    private void setAuthentication(String username, String password) {
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
    }

    private void clearAuthentication() {
        restTemplate.getInterceptors().clear();
    }

}
