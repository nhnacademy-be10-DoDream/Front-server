package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import shop.dodream.front.dto.BookDto;
import org.springframework.web.bind.annotation.PathVariable;
import shop.dodream.front.dto.CategoryResponse;

import java.util.List;

@FeignClient(name = "bookClient", url = "s1.java21.net:10325")
public interface BookClient {
    @GetMapping("/admin/books")
    List<BookDto> getBooks();
    @GetMapping("/public/categories/{depth}/depth")
    List<CategoryResponse> getCategoriesByDepth(@PathVariable("depth") Long depth);
}
