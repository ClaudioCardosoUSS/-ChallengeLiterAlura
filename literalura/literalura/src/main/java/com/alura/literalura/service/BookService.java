package com.alura.literalura.service;

import com.alura.literalura.dto.GutendexBookDTO;
import com.alura.literalura.entity.Author;
import com.alura.literalura.entity.Book;
import com.alura.literalura.repository.AuthorRepository;
import com.alura.literalura.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String GUTENDEX_API_URL = "https://gutendex.com/books/";

    @Transactional(readOnly = true)
    public Book searchBookInApi(String title) {
        String searchUrl = GUTENDEX_API_URL + "?search=" + title;
        GutendexBookDTO response = restTemplate.getForObject(searchUrl, GutendexBookDTO.class);

        if (response != null && response.getResults().length > 0) {
            GutendexBookDTO.Results bookResult = response.getResults()[0];

            // Verificar se o livro já existe no banco
            Optional<Book> existingBook = bookRepository.findByTitleIgnoreCase(bookResult.getTitle());
            if (existingBook.isPresent()) {
                entityManager.refresh(existingBook.get());
                System.out.println("\n📚 Livro encontrado no banco de dados!");
                return existingBook.get();
            } else {
                System.out.println("\n📚 Livro encontrado na API!");
                return createBookFromApiResult(bookResult);
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Book> searchBooksByAuthor(String authorName) {
        String searchUrl = GUTENDEX_API_URL + "?search=" + authorName;
        GutendexBookDTO response = restTemplate.getForObject(searchUrl, GutendexBookDTO.class);
        List<Book> newBooks = new ArrayList<>();

        if (response != null && response.getResults().length > 0) {
            for (GutendexBookDTO.Results bookResult : response.getResults()) {
                // Verifica se algum dos autores do livro corresponde à busca
                boolean authorMatch = bookResult.getAuthors().stream()
                        .anyMatch(author -> author.getName().toLowerCase()
                                .contains(authorName.toLowerCase()));

                if (authorMatch) {
                    // Verifica se o livro já existe no banco
                    Optional<Book> existingBook = bookRepository.findByTitleIgnoreCase(bookResult.getTitle());
                    if (existingBook.isEmpty()) {
                        Book newBook = createBookFromApiResult(bookResult);
                        newBooks.add(newBook);
                    }
                }
            }
        }
        return newBooks;
    }

    @Transactional
    public Book saveBook(Book book) {
        if (book == null) return null;

        // Verificar se o livro já existe
        Optional<Book> existingBook = bookRepository.findByTitleIgnoreCase(book.getTitle());
        if (existingBook.isPresent()) {
            return existingBook.get();
        }

        // Verificar se o autor já existe
        Author bookAuthor = book.getAuthor();
        if (bookAuthor != null) {
            Optional<Author> existingAuthor = authorRepository.findByNameIgnoreCase(bookAuthor.getName());
            if (existingAuthor.isPresent()) {
                book.setAuthor(existingAuthor.get());
            } else {
                authorRepository.save(bookAuthor);
            }
        }

        return bookRepository.save(book);
    }

    @Transactional
    public List<Book> saveAllBooks(List<Book> books) {
        List<Book> savedBooks = new ArrayList<>();
        for (Book book : books) {
            Optional<Book> existingBook = bookRepository.findByTitleIgnoreCase(book.getTitle());
            if (existingBook.isEmpty()) {
                // Verificar se o autor já existe
                Author bookAuthor = book.getAuthor();
                if (bookAuthor != null) {
                    Optional<Author> existingAuthor = authorRepository.findByNameIgnoreCase(bookAuthor.getName());
                    if (existingAuthor.isPresent()) {
                        book.setAuthor(existingAuthor.get());
                    } else {
                        authorRepository.save(bookAuthor);
                    }
                }
                savedBooks.add(bookRepository.save(book));
            }
        }
        return savedBooks;
    }

    private Book createBookFromApiResult(GutendexBookDTO.Results bookResult) {
        GutendexBookDTO.Results.Author authorDto = bookResult.getAuthors().get(0);

        Author author = new Author(
                authorDto.getName(),
                authorDto.getBirth_year(),
                authorDto.getDeath_year()
        );

        Book book = new Book(
                bookResult.getTitle(),
                bookResult.getLanguages().get(0),
                bookResult.getDownload_count()
        );
        book.setAuthor(author);

        return book;
    }

    @Transactional(readOnly = true)
    public List<Book> listAllBooks() {
        List<Book> books = bookRepository.findAllByOrderByTitleAsc();
        books.forEach(book -> {
            if (book.getAuthor() != null) {
                book.getAuthor().getName(); // Força inicialização
            }
        });
        return books;
    }

    @Transactional(readOnly = true)
    public List<Book> findBooksByLanguage(String language) {
        List<Book> books = bookRepository.findByLanguageIgnoreCaseOrderByTitleAsc(language);
        books.forEach(book -> {
            if (book.getAuthor() != null) {
                book.getAuthor().getName(); // Força inicialização
            }
        });
        return books;
    }

    @Transactional(readOnly = true)
    public List<Book> findTop10Books() {
        return bookRepository.findTop10ByOrderByDownloadCountDesc();
    }

    @Transactional(readOnly = true)
    public boolean bookExists(String title) {
        return bookRepository.findByTitleIgnoreCase(title).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<Book> findBookByTitle(String title) {
        return bookRepository.findByTitleIgnoreCase(title);
    }
}