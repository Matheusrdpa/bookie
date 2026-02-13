package com.estudo.bookie.services.specifications;

import com.estudo.bookie.entities.Book;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;

public class BookSpecifications {

    public static Specification<Book> recommendedForUser(List<Long> readBookIds, List<String> authors, List<String> genres) {
        return (root, query, cb) -> {
            Predicate notRead = cb.not(root.get("id").in(readBookIds));

            List<String> normalizedAuthors = authors.stream()
                    .map(a -> a.toLowerCase().trim())
                    .toList();

            List<String> normalizedGenres = genres.stream()
                    .map(g -> g.toLowerCase().trim())
                    .toList();


            Predicate sameAuthor = cb.or(normalizedAuthors.stream()
                    .map(a -> cb.like(cb.lower(root.join("author").get("name")), "%" + a + "%"))
                    .toArray(Predicate[]::new));

            Predicate sameGenre = cb.or(normalizedGenres.stream()
                    .map(g -> cb.like(cb.lower(cb.trim(root.get("genre"))), "%" + g + "%"))
                    .toArray(Predicate[]::new));


            Predicate authorOrGenre = cb.or(sameAuthor, sameGenre);

            return cb.and(notRead, authorOrGenre);
        };
    }

    public static Specification<Book> hasKeyword(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase().trim() + "%");
    }

    public static Specification<Book> hasAuthor(String author) {
        return (root, query, cb) -> {
            if (author == null) {
                return null;
            }
            return cb.like(cb.lower(root.join("author").get("name")),  "%" + author.toLowerCase().trim() + "%");
        };
    }

    public static Specification<Book> minRating(Double rating) {
        return (root,query, cb) -> {
            if (rating == null) return null;
            return cb.greaterThanOrEqualTo(root.get("rating"), rating);
        };
    }
}
