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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
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

        Book book = createNewValidBook();
        book.setId(id());
        Mockito.when(bookRepository.findById(id())).thenReturn(Optional.of(book));

        Optional<Book> foundBook = bookService.getById(id());

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getId()).isEqualTo(id());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("should return empty if book ID does not exist")
    void testBookNotFoundById() {
        Mockito.when(bookRepository.findById(id())).thenReturn(Optional.empty());

        Optional<Book> book = bookService.getById(id());

        assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("must delete a book")
    void testDeleteBook() {

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> bookService.delete(id()));

        Mockito.verify(bookRepository, Mockito.times(1)).deleteById(id());
    }

    @Test
    @DisplayName("should return empty if book id does not exist")
    void testDeleteInvalidBook() {
        Mockito.verify(bookRepository, Mockito.never()).deleteById(id());
    }

    @Test
    @DisplayName("should return error if the book does not exist")
    void testUpdateInvalidBook() {
        Book book = createNewValidBook();
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> bookService.update(book));

        Mockito.verify(bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("must update a book")
    void testUpdateBook() {
        Book updatingBook = Book.builder().id(id()).build();

        Book updatedBook = createNewValidBook();
        updatedBook.setId(id());
        Mockito.when(bookRepository.save(updatingBook)).thenReturn(updatedBook);

        Book book = bookService.update(updatingBook);

        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("must filter book by properties")
    void testFindBook() {
        // scenery
        Book book = createNewValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> bookList = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(bookList, pageRequest, 1);
        Mockito.when(bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

        // execution
        Page<Book> result  = bookService.find(book, pageRequest);

        // verification
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(bookList);
        assertThat(result.getPageable().getPageNumber()).isZero();
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("must get the book from isbn")
    void tesGetBookByIsbn() {
        String isbn = "123";
        Mockito.when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));

        Optional<Book> book = bookService.getBookByIsbn(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1L);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        Mockito.verify(bookRepository, Mockito.times(1)).findByIsbn(isbn);
    }

    private Long id() {
        return 1L;
    }

    private Book createNewValidBook() {
        return Book.builder().title("Clean Code").author("Robert Cecil Martin").isbn("121321").build();
    }
}
