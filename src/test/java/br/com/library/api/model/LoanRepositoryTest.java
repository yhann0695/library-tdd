package br.com.library.api.model;

import br.com.library.api.repository.LoanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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

        Book book = Book.builder().title("Clean Code").author("Robert C. Martin").isbn("123").build();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Joãozin").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        boolean exists = loanRepository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
    }
}
