package br.com.library.api.service.impl;

import br.com.library.api.exception.BusinessException;
import br.com.library.api.model.Book;
import br.com.library.api.repository.BookRepository;
import br.com.library.api.service.BookService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book entity) {
        if(bookRepository.existsByIsbn(entity.getIsbn())) {
            throw new BusinessException("ISBN already registered");
        }
        return bookRepository.save(entity);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        if(id == null) {
            throw new IllegalArgumentException("Book id cant be null");
        }
        bookRepository.deleteById(id);
    }

    @Override
    public Book update(Book book) {
        if(book.getId() == null) {
            throw new IllegalArgumentException("Book id cant be null");
        }
        return bookRepository.save(book);
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {
        Example<Book> example = Example.of(filter, ExampleMatcher.matching()
                                                            .withIgnoreCase()
                                                            .withIgnoreNullValues()
                                                            .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING));
        return bookRepository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }
}
