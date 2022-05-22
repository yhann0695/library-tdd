package br.com.library.api.service;

import br.com.library.api.exception.BusinessException;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

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

        Mockito.when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(false);
        Mockito.when(loanRepository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = loanService.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("should throw an error when trying to borrow a book that is already borrowed")
    void testLoanedBookSave() {

        Book book = Book.builder().id(1L).build();
        String costumer = "Joaozin";

        Loan savingLoan = Loan.builder()
                .id(1L)
                .customer(costumer)
                .book(book)
                .loanDate(LocalDate.now()).build();

        Mockito.when(loanRepository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> loanService.save(savingLoan));

        assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("Book already loaned");

        Mockito.verify(loanRepository, Mockito.never()).save(savingLoan);
    }

    @Test
    @DisplayName("must obtain the information of a loan by the ID")
    void testGetLoanDetails() {
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(loanRepository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> result = loanService.getById(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(loanRepository).findById(id);
    }

    public Loan createLoan() {
        Book book = Book.builder().id(1L).build();
        String costumer = "Joaozin";

        return Loan.builder()
                .id(1L)
                .customer(costumer)
                .book(book)
                .loanDate(LocalDate.now()).build();
    }
}
