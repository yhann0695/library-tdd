package br.com.library.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Loan")
public class Loan {

    @Id
    @Column(name = "LOAN_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LOAN_CUSTOMER")
    private String customer;

    @JoinColumn(name = "BOOK_ID")
    @ManyToOne
    private Book book;

    @Column(name = "LOAN_DATE")
    private LocalDate loanDate;

    @Column(name = "LOAN_RETURNED")
    private boolean returned;
}
