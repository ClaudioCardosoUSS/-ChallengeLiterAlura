package com.alura.literalura.service;

import com.alura.literalura.entity.Author;
import com.alura.literalura.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public List<Author> listAllAuthors() {
        return authorRepository.findAllByOrderByNameAsc();
    }

    public List<Author> findAuthorsAliveInYear(Integer year) {
        if (year == null) {
            throw new IllegalArgumentException("Ano não pode ser nulo");
        }
        return authorRepository.findAuthorsAliveInYear(year);
    }

    @Transactional
    public Author saveAuthor(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Autor não pode ser nulo");
        }

        // Verifica se já existe um autor com o mesmo nome
        Optional<Author> existingAuthor = authorRepository.findByNameIgnoreCase(author.getName());
        if (existingAuthor.isPresent()) {
            return existingAuthor.get();
        }

        return authorRepository.save(author);
    }

    public Optional<Author> findAuthorByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do autor não pode ser nulo ou vazio");
        }
        return authorRepository.findByNameIgnoreCase(name.trim());
    }

    @Transactional(readOnly = true)
    public boolean authorExists(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return authorRepository.findByNameIgnoreCase(name.trim()).isPresent();
    }
}