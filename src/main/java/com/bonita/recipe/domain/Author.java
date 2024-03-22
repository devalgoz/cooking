package com.bonita.recipe.domain;

import com.bonita.recipe.domain.recipe.Recipe;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Author {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany
    private Set<Recipe> recipeSet = new HashSet<>();

    public Author(String name) {
        this.name = name;
    }
}
