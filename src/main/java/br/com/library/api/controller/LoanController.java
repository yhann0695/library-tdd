package br.com.library.api.controller;

import br.com.library.api.dto.BookDTO;
import br.com.library.api.dto.LoanDTO;
import br.com.library.api.dto.LoanFilterDTO;
import br.com.library.api.dto.ReturnedLoadDTO;
import br.com.library.api.model.Book;
import br.com.library.api.model.Loan;
import br.com.library.api.service.BookService;
import br.com.library.api.service.LoanService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
        Loan loan = loanService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.getReturned());

        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
        Page<Loan> result = loanService.find(dto, pageRequest);
        List<LoanDTO> loanDTOList = result.getContent()
                                        .stream()
                                        .map(entity -> {
                                            Book book = entity.getBook();
                                            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                                            LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                                            loanDTO.setBook(bookDTO);
                                            return loanDTO;
                                        }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loanDTOList, pageRequest, result.getTotalElements());
    }
}
