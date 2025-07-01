package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.BookDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookClient bookClient;
    // Controller
    @GetMapping("/")
    public String home(Model model) {

        List<BookDto> books = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            BookDto book = new BookDto();
            book.setTitle("Book Title " + i);
            book.setAuthor("Author " + i);
            book.setRegularPrice(20000 + (i * 1000));
            book.setSalePrice(15000 + (i * 500));
            book.setBookUrl("http://storage.java21.net:8000/dodream-images/book/0524d09f-0fb9-4671-8611-ef6e00983628.png");
            books.add(book);
        }

        // 책 리스트를 6개씩 나누기
        List<List<BookDto>> chunks = new ArrayList<>();
        for (int i = 0; i < books.size(); i += 6) {
            chunks.add(books.subList(i, Math.min(i + 6, books.size())));
        }

        model.addAttribute("bookChunks", chunks);
        return "home";
    }
}
