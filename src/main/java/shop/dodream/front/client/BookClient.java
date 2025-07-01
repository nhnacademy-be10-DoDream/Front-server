package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import shop.dodream.front.dto.BookDto;

import java.util.List;

@FeignClient(name = "bookClient", url = "http://localhost:10320")
public interface BookClient {
    @GetMapping("/books/internal/book-list")
    List<BookDto> bookList();
}
