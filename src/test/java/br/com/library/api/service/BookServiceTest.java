package br.com.library.api.service;

import br.com.library.api.dto.BookDTO;
import br.com.library.api.exception.BusinessException;
import br.com.library.api.model.Book;
import br.com.library.api.repository.BookRepository;
import br.com.library.api.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("Test")
public class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Must save a book")
    void saveBookTest() {
        Book book = createNewValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when( bookRepository.save(book))
                .thenReturn(Book.builder().id(1L).title("Clean Code").author("Robert Cecil Martin").isbn("121321").build());

        Book savedBook = bookService.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Clean Code");
        assertThat(savedBook.getAuthor()).isEqualTo("Robert Cecil Martin");
        assertThat(savedBook.getIsbn()).isEqualTo("121321");
    }

    @Test
    @DisplayName("should throw an error when trying to save a book with duplicate ISBN")
    void shouldNotSaveABookWithDuplicatedISBN() {

        Book book = createNewValidBook();
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> bookService.save(book));
        assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("ISBN already registered");

        Mockito.verify(bookRepository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("must get a book by ID")
    void testGetById() {
        Long id = 1L;

        Book book = createNewValidBook();
        book.setId(id);
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getById(id);

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("should return empty if book ID does not exist")
    void testBookNotFoundById() {
        Long id = 1L;
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> book = bookService.getById(id);

        assertThat(book.isPresent()).isFalse();
    }

    private Book createNewValidBook() {
        return Book.builder().title("Clean Code").author("Robert Cecil Martin").isbn("121321").build();
    }
}
