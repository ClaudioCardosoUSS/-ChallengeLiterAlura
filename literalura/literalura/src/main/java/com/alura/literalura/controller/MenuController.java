package com.alura.literalura.controller;

import com.alura.literalura.entity.Author;
import com.alura.literalura.entity.Book;
import com.alura.literalura.service.AuthorService;
import com.alura.literalura.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

@Controller
public class MenuController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private ConfigurableApplicationContext context;

    private final Scanner scanner;

    public MenuController() {
        this.scanner = new Scanner(System.in);
    }

    public void showMenu() {
        int option = -1;
        do {
            try {
                printMenuOptions();
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    continue;
                }
                option = Integer.parseInt(input);

                switch (option) {
                    case 1 -> searchBookByTitle();
                    case 2 -> searchBooksByAuthor();
                    case 3 -> listRegisteredBooks();
                    case 4 -> listRegisteredAuthors();
                    case 5 -> listAuthorsAliveInYear();
                    case 6 -> listBooksByLanguage();
                    case 0 -> {
                        System.out.println("\n👋 Encerrando o sistema...");
                        context.close();
                        System.exit(0);
                    }
                    default -> System.out.println("\n❌ Opção inválida! Tente novamente.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n❌ Por favor, digite um número válido.");
            } catch (InputMismatchException e) {
                System.out.println("\n❌ Entrada inválida. Por favor, tente novamente.");
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("\n❌ Erro: " + e.getMessage());
                e.printStackTrace();
            }
        } while (option != 0);
    }

    private void printMenuOptions() {
        System.out.println("\n╔════════════ Menu LiterAlura ════════════╗");
        System.out.println("║ 1 - Buscar livro pelo título            ║");
        System.out.println("║ 2 - Buscar livro por autor              ║");
        System.out.println("║ 3 - Listar livros registrados           ║");
        System.out.println("║ 4 - Listar autores registrados          ║");
        System.out.println("║ 5 - Listar autores vivos em um ano      ║");
        System.out.println("║ 6 - Listar livros por idioma            ║");
        System.out.println("║ 0 - Sair                                ║");
        System.out.println("╚═════════════════════════════════════════╝");
        System.out.print("Digite sua opção: ");
    }

    @Transactional
    private void searchBookByTitle() {
        System.out.print("\nDigite o título do livro: ");
        String title = scanner.nextLine().trim();

        try {
            Book book = bookService.searchBookInApi(title);
            if (book != null) {
                System.out.println("\n📚 Detalhes do Livro:");
                System.out.println("═══════════════════════════════════════");
                System.out.printf("📖 Título: %s%n", book.getTitle());
                System.out.printf("✍️ Autor: %s%n", book.getAuthor().getName());
                System.out.printf("🌍 Idioma: %s%n", book.getLanguage());
                System.out.printf("⭐ Downloads: %,d%n", book.getDownloadCount());
                System.out.println("═══════════════════════════════════════");

                if (!bookService.bookExists(book.getTitle())) {
                    System.out.print("\nDeseja salvar este livro no banco de dados? (S/N): ");
                    String response = scanner.nextLine().trim().toUpperCase();
                    if (response.equals("S")) {
                        book = bookService.saveBook(book);
                        System.out.println("\n✅ Livro salvo com sucesso!");
                    }
                }
            } else {
                System.out.println("\n❌ Livro não encontrado!");
            }
        } catch (Exception e) {
            System.out.println("\n❌ Erro ao buscar livro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional
    private void searchBooksByAuthor() {
        System.out.print("\nDigite o nome do autor: ");
        String authorName = scanner.nextLine().trim();

        try {
            List<Book> newBooks = bookService.searchBooksByAuthor(authorName);

            if (newBooks.isEmpty()) {
                System.out.println("\n❌ Nenhum livro novo encontrado para este autor!");
                return;
            }

            System.out.printf("\n📚 %d Novo(s) Livro(s) Encontrado(s) para %s:%n",
                    newBooks.size(), authorName);
            System.out.println("═══════════════════════════════════════");

            int bookCount = 1;
            for (Book book : newBooks) {
                System.out.printf("\n📖 [%d] %s%n", bookCount++, book.getTitle());
                System.out.printf("✍️ Autor: %s%n", book.getAuthor().getName());
                System.out.printf("🌍 Idioma: %s%n", book.getLanguage());
                System.out.printf("⭐ Downloads: %,d%n", book.getDownloadCount());
                System.out.println("───────────────────────────────────────");
            }

            System.out.print("\nDeseja salvar estes livros no banco de dados? (S/N): ");
            String response = scanner.nextLine().trim().toUpperCase();
            if (response.equals("S")) {
                List<Book> savedBooks = bookService.saveAllBooks(newBooks);
                if (!savedBooks.isEmpty()) {
                    System.out.printf("\n✅ %d livro(s) salvo(s) com sucesso!%n", savedBooks.size());
                }
                if (savedBooks.size() < newBooks.size()) {
                    System.out.printf("ℹ️ %d livro(s) já existiam no banco de dados.%n",
                            newBooks.size() - savedBooks.size());
                }
            }
        } catch (Exception e) {
            System.out.println("\n❌ Erro ao buscar livros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    private void listRegisteredBooks() {
        try {
            List<Book> books = bookService.listAllBooks();
            if (books.isEmpty()) {
                System.out.println("\n📚 Nenhum livro registrado!");
                return;
            }

            System.out.printf("\n📚 Total de Livros Registrados: %d%n", books.size());
            System.out.println("═══════════════════════════════════════");

            int bookCount = 1;
            for (Book book : books) {
                System.out.printf("\n📖 [%d] %s%n", bookCount++, book.getTitle());
                System.out.printf("✍️ Autor: %s%n", book.getAuthor().getName());
                System.out.printf("🌍 Idioma: %s%n", book.getLanguage());
                System.out.printf("⭐ Downloads: %,d%n", book.getDownloadCount());
                System.out.println("───────────────────────────────────────");
            }
        } catch (Exception e) {
            System.out.println("\n❌ Erro ao listar livros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    private void listRegisteredAuthors() {
        try {
            List<Author> authors = authorService.listAllAuthors();
            if (authors.isEmpty()) {
                System.out.println("\n👤 Nenhum autor registrado!");
                return;
            }

            System.out.printf("\n👥 Total de Autores Registrados: %d%n", authors.size());
            System.out.println("═══════════════════════════════════════");

            int authorCount = 1;
            for (Author author : authors) {
                System.out.printf("\n✍️ [%d] %s%n", authorCount++, author.getName());
                System.out.printf("🎂 Nascimento: %d%n", author.getBirthYear());
                System.out.printf("📅 Falecimento: %s%n",
                        (author.getDeathYear() != null ? author.getDeathYear() : "Ainda vivo"));

                List<Book> books = author.getBooks();
                System.out.print("📚 Livros: ");
                if (books.isEmpty()) {
                    System.out.println("Nenhum livro registrado");
                } else {
                    String bookTitles = books.stream()
                            .map(Book::getTitle)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");
                    System.out.println(bookTitles);
                }
                System.out.println("───────────────────────────────────────");
            }
        } catch (Exception e) {
            System.out.println("\n❌ Erro ao listar autores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    private void listAuthorsAliveInYear() {
        try {
            System.out.print("\nDigite o ano para pesquisa: ");
            String input = scanner.nextLine().trim();
            int year = Integer.parseInt(input);

            List<Author> authors = authorService.findAuthorsAliveInYear(year);
            if (authors.isEmpty()) {
                System.out.printf("\n👤 Nenhum autor encontrado vivo no ano %d%n", year);
                return;
            }

            System.out.printf("\n👥 Total de Autores vivos em %d: %d%n", year, authors.size());
            System.out.println("═══════════════════════════════════════");

            int authorCount = 1;
            for (Author author : authors) {
                System.out.printf("\n✍️ [%d] %s%n", authorCount++, author.getName());
                System.out.printf("🎂 Nascimento: %d%n", author.getBirthYear());
                System.out.printf("📅 Falecimento: %s%n",
                        (author.getDeathYear() != null ? author.getDeathYear() : "Ainda vivo"));

                List<Book> books = author.getBooks();
                System.out.print("📚 Livros: ");
                if (books.isEmpty()) {
                    System.out.println("Nenhum livro registrado");
                } else {
                    String bookTitles = books.stream()
                            .map(Book::getTitle)
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("");
                    System.out.println(bookTitles);
                }
                System.out.println("───────────────────────────────────────");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ Por favor, digite um ano válido.");
        } catch (Exception e) {
            System.out.println("\n❌ Erro ao listar autores: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Transactional(readOnly = true)
    private void listBooksByLanguage() {
        try {
            System.out.println("\n🌍 Idiomas disponíveis:");
            System.out.println("═══════════════════════════════════════");
            System.out.println("es - Espanhol");
            System.out.println("en - Inglês");
            System.out.println("fr - Francês");
            System.out.println("pt - Português");

            System.out.print("\nDigite o código do idioma: ");
            String language = scanner.nextLine().trim().toLowerCase();

            List<Book> books = bookService.findBooksByLanguage(language);
            if (books.isEmpty()) {
                System.out.printf("\n📚 Nenhum livro encontrado no idioma '%s'%n",
                        getLanguageName(language));
                return;
            }

            System.out.printf("\n📚 Total de Livros em %s: %d%n", getLanguageName(language), books.size());
            System.out.println("═══════════════════════════════════════");

            int bookCount = 1;
            for (Book book : books) {
                System.out.printf("\n📖 [%d] %s%n", bookCount++, book.getTitle());
                System.out.printf("✍️ Autor: %s%n", book.getAuthor().getName());
                System.out.printf("⭐ Downloads: %,d%n", book.getDownloadCount());
                System.out.println("───────────────────────────────────────");
            }
        } catch (Exception e) {
            System.out.println("\n❌ Erro ao listar livros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getLanguageName(String code) {
        return switch (code.toLowerCase()) {
            case "es" -> "Espanhol";
            case "en" -> "Inglês";
            case "fr" -> "Francês";
            case "pt" -> "Português";
            default -> code;
        };
    }
}