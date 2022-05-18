package br.com.library.api.service;

import br.com.library.api.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {

    Book save(Book entity);

    Optional<Book> getById(Long id);

    void delete(Long id);

    Book update(Book book);

    Page<Book> find(Book filter, Pageable pageRequest);
}
