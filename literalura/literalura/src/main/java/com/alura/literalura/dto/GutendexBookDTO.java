package com.alura.literalura.dto;

import java.util.List;

public class GutendexBookDTO {
    private Results[] results;

    public static class Results {
        private Long id;
        private String title;
        private List<Author> authors;
        private List<String> languages;
        private Integer download_count;

        public static class Author {
            private String name;
            private Integer birth_year;
            private Integer death_year;

            // Getters and Setters
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getBirth_year() {
                return birth_year;
            }

            public void setBirth_year(Integer birth_year) {
                this.birth_year = birth_year;
            }

            public Integer getDeath_year() {
                return death_year;
            }

            public void setDeath_year(Integer death_year) {
                this.death_year = death_year;
            }
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

        public List<Author> getAuthors() {
            return authors;
        }

        public void setAuthors(List<Author> authors) {
            this.authors = authors;
        }

        public List<String> getLanguages() {
            return languages;
        }

        public void setLanguages(List<String> languages) {
            this.languages = languages;
        }

        public Integer getDownload_count() {
            return download_count;
        }

        public void setDownload_count(Integer download_count) {
            this.download_count = download_count;
        }
    }

    // Getters and Setters
    public Results[] getResults() {
        return results;
    }

    public void setResults(Results[] results) {
        this.results = results;
    }
}