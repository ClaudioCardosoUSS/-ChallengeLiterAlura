package com.alura.literalura.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 10)
    private String language;

    @Column(name = "download_count")
    private Integer downloadCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Author author;

    // Constructors
    public Book() {
    }

    public Book(String title, String language, Integer downloadCount) {
        this.title = title;
        this.language = language;
        this.downloadCount = downloadCount;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
        if (author != null && !author.getBooks().contains(this)) {
            author.getBooks().add(this);
        }
    }

    // Equals and HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // ToString
    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", language='" + language + '\'' +
                ", downloadCount=" + downloadCount +
                ", author=" + (author != null ? author.getName() : "null") +
                '}';
    }

    // Helper method for deep copy
    public Book copy() {
        Book copy = new Book();
        copy.setTitle(this.title);
        copy.setLanguage(this.language);
        copy.setDownloadCount(this.downloadCount);
        return copy;
    }

    // Validation method
    public boolean isValid() {
        return title != null && !title.trim().isEmpty() &&
                language != null && !language.trim().isEmpty() &&
                downloadCount != null && downloadCount >= 0;
    }
}