package com.bonita.recipe.infrastructure.repository;

import com.bonita.recipe.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Optional<Keyword> findByNameIgnoreCase(String name);
}
