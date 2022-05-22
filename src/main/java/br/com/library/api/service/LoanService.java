package br.com.library.api.service;

import br.com.library.api.model.Loan;

import java.util.List;
import java.util.Optional;

public interface LoanService {

    public Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
