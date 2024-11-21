package com.alura.literalura.repository;

import com.alura.literalura.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByTitleIgnoreCase(String title);

    List<Book> findByLanguageIgnoreCaseOrderByTitleAsc(String language);

    List<Book> findAllByOrderByTitleAsc();

    List<Book> findTop10ByOrderByDownloadCountDesc();
}