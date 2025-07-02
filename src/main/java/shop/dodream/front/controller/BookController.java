package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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


        List<BookDto> books = bookClient.getBooks();
        for( BookDto book : books) {
            // 이미지 URL을 BOOK_API_URL과 결합
            String bookUrlPrefix = "http://storage.java21.net:8000/dodream-images/book/";
            String imageUrl = bookUrlPrefix + book.getBookUrl();
            book.setBookUrl(imageUrl);
        }
        model.addAttribute("books", books);
        // 책 리스트를 6개씩 나누기
        List<List<BookDto>> chunks = new ArrayList<>();
        for (int i = 0; i < books.size(); i += 6) {
            chunks.add(books.subList(i, Math.min(i + 6, books.size())));
        }

        model.addAttribute("bookChunks", chunks);
        return "home";
    }


    @GetMapping("/books")
    public String bookDetail(Model model){

//        model.addAttribute("bookId", bookId);
        return "book/detail";
    }




}
