package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import shop.dodream.front.dto.BookDto;

import java.util.List;

@FeignClient(name = "bookClient", url = "http://localhost:10320")
public interface BookClient {
    List<BookDto> bookList();
}
