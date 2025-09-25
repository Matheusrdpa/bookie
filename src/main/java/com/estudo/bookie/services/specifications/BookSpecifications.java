package com.estudo.bookie.services.specifications;

import com.estudo.bookie.entities.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecifications {

    public static Specification<Book> hasTitle(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.equal(root.get("title"), title);
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
