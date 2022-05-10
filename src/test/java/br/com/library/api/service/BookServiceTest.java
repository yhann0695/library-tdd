package br.com.library.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("Test")
public class BookServiceTest {

    BookService bookService;

    @Test
    @DisplayName("Must save a book")
    void saveBookTest() {

    }
}
