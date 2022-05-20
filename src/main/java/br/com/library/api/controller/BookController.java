package br.com.library.api.controller;

import br.com.library.api.dto.BookDTO;
import br.com.library.api.exception.ApiErrors;
import br.com.library.api.exception.BusinessException;
import br.com.library.api.model.Book;
import br.com.library.api.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        Book book = modelMapper.map(dto, Book.class);
        book = bookService.save(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @GetMapping(value = "/{id}")
    public BookDTO getBookDetails(@PathVariable Long id) {
        return bookService.getById(id)
                .map( book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
       Book book =  bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        bookService.delete(book.getId());
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO update(@PathVariable Long id, BookDTO dto) {
        return bookService.getById(id).map(book -> {
            book.setTitle(dto.getTitle());
            book.setAuthor(dto.getAuthor());
            book = bookService.update(book);
            return modelMapper.map(book, BookDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = bookService.find(filter, pageRequest);
        List<BookDTO> bookDTOList = result.getContent()
                .stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class)).collect(Collectors.toList());

        return new PageImpl<BookDTO>(bookDTOList, pageRequest, result.getTotalElements());

    }
}
