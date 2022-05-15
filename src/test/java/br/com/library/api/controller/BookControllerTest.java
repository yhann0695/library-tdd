package br.com.library.api.controller;

import br.com.library.api.dto.BookDTO;
import br.com.library.api.exception.BusinessException;
import br.com.library.api.model.Book;
import br.com.library.api.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("Test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    private static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Successfully create a book")
    void testCreateBook() throws Exception {
        BookDTO bookDTO = createNewBook();

        BDDMockito.given(bookService.save(Mockito.any(Book.class)))
                .willReturn(Book.builder().id(1L).title("Clean Code").author("Robert Cecil Martin").isbn("121321").build());

        String json = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDTO.getIsbn()));
    }

    @Test
    @DisplayName("Throws error for insufficient data in book creation")
    void testCreateInvalidBook() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));

    }

    @Test
    @DisplayName("the ISBN cannot be repeated")
    void testCreateBookWithDuplicatedIsbn() throws Exception {

        BookDTO dto = createNewBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        final String msgError = "ISBN already registered";
        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessException(msgError));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(msgError));
    }

    @Test
    @DisplayName("must get information from the book")
    void testGetBookDetailsTest() throws Exception {

        Long id = 1L;

        Book book = Book.builder()
                                .id(id)
                                .title(createNewBook().getTitle())
                                .author(createNewBook().getAuthor())
                                .isbn(createNewBook().getIsbn()).build();

        BDDMockito.given(bookService.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    @DisplayName("should return not found if the book ID does not exist.")
    void testBookNotFound() throws Exception {
        BDDMockito.given(bookService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("must delete a book")
    void testDeleteBook() throws Exception {
        BDDMockito.given(bookService.getById(anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("should return not found when a book does not exist to delete")
    void testDeleteNonExistentBook() throws Exception {
        BDDMockito.given(bookService.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mockMvc
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("must update a book")
    void testUpdateBook() throws Exception {
        Long id = 1L;

        String json = new ObjectMapper().writeValueAsString(createNewBook());

        BDDMockito.given(bookService.getById(id))
                .willReturn(Optional.of(Book.builder().id(1L).title("new title").author("new author").isbn("123").build()));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                                                    .put(BOOK_API.concat("/" + id))
                                                    .content(json)
                                                    .accept(MediaType.APPLICATION_JSON)
                                                    .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                    .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBook().getTitle()))
                    .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBook().getAuthor()))
                    .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    @DisplayName("should return 404 when trying to update a book that doesn't exist")
    void testUpdateNonExistentBook() throws Exception {

    }

    private BookDTO createNewBook() {
        return BookDTO.builder().title("Clean Code").author("Robert Cecil Martin").isbn("121321").build();
    }
}
