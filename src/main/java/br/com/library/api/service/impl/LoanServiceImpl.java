package br.com.library.api.service.impl;

import br.com.library.api.exception.BusinessException;
import br.com.library.api.model.Loan;
import br.com.library.api.repository.LoanRepository;
import br.com.library.api.service.LoanService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        if(loanRepository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book already loaned");
        }
        return loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public Loan update(Loan loan) {
        return null;
    }
}
