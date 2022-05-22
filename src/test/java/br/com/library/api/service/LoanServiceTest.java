package br.com.library.api.service;

import br.com.library.api.model.Book;
import br.com.library.api.model.Loan;
import br.com.library.api.repository.LoanRepository;
import br.com.library.api.service.impl.BookServiceImpl;
import br.com.library.api.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("Test")
public class LoanServiceTest {

    @MockBean
    LoanRepository loanRepository;

    LoanService loanService;

    @BeforeEach
    void setUp() {
        this.loanService = new LoanServiceImpl(loanRepository);
    }

    @Test
    @DisplayName("must save a loan")
    void testSaveLoan() {
        Book book = Book.builder().id(1L).build();
        String costumer = "Joaozin";

        Loan savingLoan = Loan.builder()
                            .id(1L)
                            .customer(costumer)
                            .book(book)
                            .loanDate(LocalDate.now()).build();

        Loan savedLoan = Loan.builder()
                                .id(1L)
                                .customer(costumer)
                                .book(book)
                                .loanDate(LocalDate.now()).build();

        Mockito.when(loanRepository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = loanService.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }
}
