package br.com.library.api.service;

import br.com.library.api.model.Book;

import java.util.Optional;

public interface BookService {

    Book save(Book entity);

    Optional<Book> getById(Long id);

    void delete(Long id);
}
