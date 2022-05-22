package br.com.library.api.repository;

import br.com.library.api.model.Book;
import br.com.library.api.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndNotReturned(Book book);
}
