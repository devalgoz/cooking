package com.bonita.recipe.infrastructure.repository;

import com.bonita.recipe.domain.recipe.Recipe;
import com.bonita.recipe.infrastructure.predicate.PredicateBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, QuerydslPredicateExecutor<Recipe> {

    Optional<Recipe> findByNameIgnoreCase(String name);


    default Iterable<Recipe> search(String author, List<String> keywords, List<String> ingredients) {
        return findAll(PredicateBuilder.buildPredicate(author, keywords, ingredients));
    }

}
