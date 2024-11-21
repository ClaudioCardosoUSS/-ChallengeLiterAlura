package com.alura.literalura.repository;

import com.alura.literalura.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findByNameIgnoreCase(String name);

    @Query("SELECT DISTINCT a FROM Author a WHERE :year BETWEEN a.birthYear AND COALESCE(a.deathYear, 2024)")
    List<Author> findAuthorsAliveInYear(@Param("year") Integer year);

    List<Author> findAllByOrderByNameAsc();
}