package com.bonita.recipe.infrastructure.predicate;

import com.bonita.recipe.domain.recipe.QRecipe;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringPath;

import java.util.List;

public class PredicateBuilder {

    public static Predicate buildPredicate(final String author, final List<String> keywords, final List<String> ingredients) {
        Predicate authorPredicate = buildPredicateForAuthor(author);
        Predicate keywordsPredicate = buildPredicateForValuesAndPath(keywords, QRecipe.recipe.keywords.any().name);
        Predicate ingredientsPredicate = buildPredicateForValuesAndPath(ingredients, QRecipe.recipe.ingredients.any().name);
        return ExpressionUtils.allOf(authorPredicate, keywordsPredicate, ingredientsPredicate);
    }

    private static Predicate buildPredicateForAuthor(String author) {
        if (author == null || author.isEmpty()) {
            return null;
        }
        return QRecipe.recipe.author.name.containsIgnoreCase(author);
    }

    private static Predicate buildPredicateForValuesAndPath(final List<String> values, final StringPath path) {
        if (values == null || values.isEmpty()) {
            return null;
        }

        Predicate predicate = null;
        for (String value : values) {
            predicate = ExpressionUtils.or(predicate, path.containsIgnoreCase(value));
        }
        return predicate;
    }
}


