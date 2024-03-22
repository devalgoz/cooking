package com.bonita.recipe.domain.recipe;

import com.bonita.recipe.domain.Author;
import com.bonita.recipe.domain.Ingredient;
import com.bonita.recipe.domain.Keyword;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(exclude = {"ingredients", "keywords"})
public class Recipe {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Author author;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Ingredient> ingredients = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Keyword> keywords = new HashSet<>();

    public void setIngredients(Set<Ingredient> ingredients) {
        this.ingredients.clear();
        this.ingredients.addAll(ingredients);
    }

    public void setKeywords(Set<Keyword> keywords) {
        this.keywords.clear();
        this.keywords.addAll(keywords);
    }

}
