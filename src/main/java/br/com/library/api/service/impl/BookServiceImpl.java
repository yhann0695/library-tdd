package br.com.library.api.service.impl;

import br.com.library.api.exception.BusinessException;
import br.com.library.api.model.Book;
import br.com.library.api.repository.BookRepository;
import br.com.library.api.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

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
        bookRepository.deleteById(id);
    }

    @Override
    public Book update(Book book) {
        return bookRepository.save(book);
    }
}
