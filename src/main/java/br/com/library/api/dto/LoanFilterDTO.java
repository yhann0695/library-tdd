package br.com.library.api.dto;

import lombok.Data;

@Data
public class LoanFilterDTO {

    private String isbn;
    private String customer;
}
