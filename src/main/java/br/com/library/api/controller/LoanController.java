package br.com.library.api.controller;

import br.com.library.api.dto.LoanDTO;
import br.com.library.api.dto.ReturnedLoadDTO;
import br.com.library.api.model.Book;
import br.com.library.api.model.Loan;
import br.com.library.api.service.BookService;
import br.com.library.api.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto) {
        Book book = bookService.getBookByIsbn(dto.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed ISBN"));
        Loan entity = Loan.builder().book(book).customer(dto.getCustomer()).loanDate(LocalDate.now()).build();
        entity = loanService.save(entity);
        return entity.getId();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoadDTO dto) {
        Loan loan = loanService.getById(id).get();
        loan.setReturned(dto.getReturned());

        loanService.update(loan);
    }
}
