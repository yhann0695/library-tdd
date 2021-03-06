package br.com.library.api.service;

import br.com.library.api.dto.LoanFilterDTO;
import br.com.library.api.exception.BusinessException;
import br.com.library.api.model.Book;
import br.com.library.api.model.Loan;
import br.com.library.api.repository.LoanRepository;
import br.com.library.api.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(loanRepository).findById(id);
    }

    @Test
    @DisplayName("must update a Loan")
    void testUpdateLoan() {
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);
        loan.setReturned(true);

        Mockito.when(loanRepository.save(loan)).thenReturn(loan);

        Loan updatedLoan =  loanService.update(loan);

        assertThat(updatedLoan.isReturned()).isTrue();
        Mockito.verify(loanRepository).save(loan);
    }

    @Test
    @DisplayName("must filter loan by properties")
    void testFindLoan() {
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().isbn("123").customer("Jo??ozin").build();
        // scenery
        Long id = 1L;
        Loan loan = createLoan();
        loan.setId(id);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> loanList = Collections.singletonList(loan);

        Page<Loan> page = new PageImpl<>(loanList, pageRequest, loanList.size());
        Mockito.when(loanRepository
                .findByBookIsbnOrCustomer(Mockito.anyString(), Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        // execution
        Page<Loan> result  = loanService.find(loanFilterDTO, pageRequest);

        // verification
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(loanList);
        assertThat(result.getPageable().getPageNumber()).isZero();
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    public static Loan createLoan() {
        Book book = Book.builder().id(1L).build();
        String costumer = "Joaozin";

        return Loan.builder()
                .id(1L)
                .customer(costumer)
                .book(book)
                .loanDate(LocalDate.now()).build();
    }
}
