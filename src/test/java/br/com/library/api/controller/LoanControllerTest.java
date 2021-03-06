package br.com.library.api.controller;

import br.com.library.api.dto.LoanDTO;
import br.com.library.api.dto.LoanFilterDTO;
import br.com.library.api.dto.ReturnedLoadDTO;
import br.com.library.api.exception.BusinessException;
import br.com.library.api.model.Book;
import br.com.library.api.model.Loan;
import br.com.library.api.service.BookService;
import br.com.library.api.service.LoanService;
import br.com.library.api.service.LoanServiceTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("Test")
@WebMvcTest(controllers = {LoanController.class})
@AutoConfigureMockMvc
public class LoanControllerTest {

    private static final String  LOAN_API = "/api/loans";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    @Test
    @DisplayName("must take out a loan")
    void testCreateLoan() throws Exception {

        LoanDTO dto = LoanDTO.builder().isbn("123321").customer("Jaozin").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn(dto.getIsbn()).build();
        BDDMockito.given(bookService.getBookByIsbn(dto.getIsbn())).willReturn(Optional.of(book));

        Loan loan = Loan.builder().id(1L).customer("Jaozin").book(book).loanDate(LocalDate.now()).build();
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("1"));
    }

    @Test
    @DisplayName("should return an error if you try to borrow a book that doesn't exist")
    void testInvalidIsbnCreateLoan() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("123321").customer("Jaozin").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn(dto.getIsbn())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book not found for passed ISBN"));
    }

    @Test
    @DisplayName("should return error when trying to borrow a book that is already borrowed")
    void tesLoanedBookErrorOnCreateLoan() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("123321").customer("Jaozin").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder().id(1L).isbn(dto.getIsbn()).build();
        BDDMockito.given(bookService.getBookByIsbn(dto.getIsbn())).willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class)))
                .willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LOAN_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value("Book already loaned"));
    }

    @Test
    @DisplayName("must return a book")
    void testReturnBook() throws Exception {
        ReturnedLoadDTO dto = ReturnedLoadDTO.builder().returned(true).build();
        Loan loan = Loan.builder().id(1L).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        mockMvc.perform(patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk());

        // check if the function was called
        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }

    @Test
    @DisplayName("should return a 404 when trying to return a non-existent book")
    void testReturnNonexistentBook() throws Exception {
        ReturnedLoadDTO dto = ReturnedLoadDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        mockMvc.perform(patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("must filter loan")
    void testFindLoan() throws Exception {

        Long id = 1L;
        Loan loan = LoanServiceTest.createLoan();
        loan.setId(id);
        loan.setBook(Book.builder().id(1L).isbn("123").build());

        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 100), 1));

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=100", loan.getBook().getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("content", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("totalElements").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageSize").value(100))
                .andExpect(MockMvcResultMatchers.jsonPath("pageable.pageNumber").value(0));
    }
}
