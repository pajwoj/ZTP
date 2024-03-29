package pl.pajwoj.ztp.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pl.pajwoj.ztp.models.Book;
import pl.pajwoj.ztp.models.User;
import pl.pajwoj.ztp.repositories.BookRepository;

import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public ResponseEntity<?> add(String isbn, String name, String author) {
        if(isbn.isBlank() || name.isBlank() || author.isBlank()) return new ResponseEntity<>("Details can't be empty!", HttpStatus.BAD_REQUEST);
        if(bookRepository.findByIsbn(isbn).isPresent()) return new ResponseEntity<>("Book already exists!", HttpStatus.BAD_REQUEST);

        bookRepository.save(new Book(
                isbn,
                name,
                author
        ));

        return new ResponseEntity<>(name + " successfully added to database!", HttpStatus.OK);
    }

    public ResponseEntity<?> delete(String isbn) {
        if(isbn.isBlank()) return new ResponseEntity<>("Details can't be empty!", HttpStatus.BAD_REQUEST);
        if(bookRepository.findByIsbn(isbn).isEmpty()) return new ResponseEntity<>("Book not found in database!", HttpStatus.BAD_REQUEST);

        bookRepository.delete(bookRepository.findByIsbn(isbn).get());
        return new ResponseEntity<>("Book succesfully deleted!", HttpStatus.OK);
    }

    public ResponseEntity<?> get(Long id) {
        if(!bookRepository.existsById(id) || bookRepository.findById(id).isEmpty()) return new ResponseEntity<>("Book not found in database!", HttpStatus.BAD_REQUEST);

        else {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            Book b = bookRepository.findById(id).get();

            try {
                String json = ow.writeValueAsString(b);
                return new ResponseEntity<>(json, HttpStatus.OK);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
