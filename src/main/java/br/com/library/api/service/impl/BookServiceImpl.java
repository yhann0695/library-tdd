package br.com.library.api.service.impl;

import br.com.library.api.model.Book;
import br.com.library.api.repository.BookRepository;
import br.com.library.api.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book entity) {
        return bookRepository.save(entity);
    }
}
