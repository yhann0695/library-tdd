package br.com.library.api.model;

import br.com.library.api.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("should return true if the ISBN already exists")
    void testReturnTrueWhenIsbnExists() {
        String isbn = "121321";
        Book book = createNewBook(isbn);
        entityManager.persist(book);

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isTrue();
    }

    private Book createNewBook(String isbn) {
        return Book.builder().title("Clean Code").author("Robert Cecil Martin").isbn(isbn).build();
    }

    @Test
    @DisplayName("should return false when ISBN does not exist")
    void testReturnFalseWhenIsbnExists() {
        String isbn = "121321";

        boolean exists = bookRepository.existsByIsbn(isbn);

        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("must get a book by ID")
    void testFindById() {
        String isbn = "121321";
        Book book = createNewBook(isbn);
        entityManager.persist(book);

        Optional<Book> foundBook = bookRepository.findById(book.getId());

        assertThat(foundBook).isPresent();
    }

    @Test
    @DisplayName("must save a book")
    void testSaveBook() {
        String isbn = "121321";
        Book book = createNewBook(isbn);

        Book savedBook = bookRepository.save(book);

        assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("must delete a book")
    void testDeleteBook() {
        String isbn = "121321";
        Book book = createNewBook(isbn);
        entityManager.persist(book);
        Book foundBook = entityManager.find(Book.class, book.getId());

        bookRepository.deleteById(foundBook.getId());

        Book deletedBook = entityManager.find(Book.class, book.getId());
        assertThat(deletedBook).isNull();


    }


}
