package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import shop.dodream.front.dto.BookDto;

import java.util.List;

@FeignClient(name = "bookClient", url = "s1.java21.net:10325")
public interface BookClient {
    @GetMapping("/admin/books")
    List<BookDto> getBooks();
}
