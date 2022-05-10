package br.com.library.api.controller;

import br.com.library.api.dto.BookDTO;
import br.com.library.api.model.Book;
import br.com.library.api.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO dto) {
        Book entity = Book.builder().title(dto.getTitle()).author(dto.getAuthor()).isbn(dto.getIsbn()).build();
        entity = bookService.save(entity);
        return BookDTO.builder().id(entity.getId()).author(entity.getAuthor()).title(entity.getTitle()).isbn(entity.getIsbn()).build();
    }
}
