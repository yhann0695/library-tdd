package br.com.library.api.service;

import br.com.library.api.dto.LoanFilterDTO;
import br.com.library.api.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    public Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO dto, Pageable pageable);
}
