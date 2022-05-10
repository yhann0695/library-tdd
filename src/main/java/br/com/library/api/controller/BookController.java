package br.com.library.api.controller;

import br.com.library.api.dto.BookDTO;
import br.com.library.api.model.Book;
import br.com.library.api.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody BookDTO dto) {
        Book book = modelMapper.map(dto, Book.class);
        book = bookService.save(book);
        return modelMapper.map(book, BookDTO.class);
    }
}
