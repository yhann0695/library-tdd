package br.com.library.api.model;

import br.com.library.api.repository.LoanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository loanRepository;


    @Test
    @DisplayName("should check if there is an unreturned loan for the book")
    void testExistsByBookAndNotReturned() {

        Loan loan = createAndPersistLoan();
        Book book = loan.getBook();

        boolean exists = loanRepository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("must filter loan per isbn or customer")
    void testFindByBookIsbnOrCustomer() {
        Loan loan = createAndPersistLoan();

        Page<Loan> result =  loanRepository
                            .findByBookIsbnOrCustomer("123", "Joãozin", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);

    }

    private Loan createAndPersistLoan() {
        Book book = Book.builder().title("Clean Code").author("Robert C. Martin").isbn("123").build();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Joãozin").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        return loan;
    }
}
