package br.com.library.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BookDTO {

    private Long id;
    private String title;
    private String author;
    private String isbn;
}
