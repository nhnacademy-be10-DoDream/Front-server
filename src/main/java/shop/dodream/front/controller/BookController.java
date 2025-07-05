package shop.dodream.front.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            String bookUrlPrefix = "https://dodream.shop/dodream-images/book/";
            String imageUrl = bookUrlPrefix + book.getBookUrl();
            book.setBookUrl(imageUrl);
        }
        model.addAttribute("books", books);
        // 책 리스트를 6개씩 나누기
        List<List<BookDto>> chunks = new ArrayList<>();
        for (int i = 0; i < books.size(); i += 5) {
            chunks.add(books.subList(i, Math.min(i + 5, books.size())));
        }

        model.addAttribute("bookChunks", chunks);
        return "home";
    }


    @GetMapping("/books/{book-id}")
    public String bookDetail(@PathVariable("book-id") Long bookId, Model model){

        BookDetailDto bookDetailDto = bookClient.getBookDetail(bookId);
        String bookUrlPrefix = "https://dodream.shop/dodream-images/book/";
        List<String> convertedUrls = new ArrayList<>();

        for (String url : bookDetailDto.getBookUrls()) {
            convertedUrls.add(bookUrlPrefix + url);
        }

        bookDetailDto.setBookUrls(convertedUrls);

        List<ReviewResponse> reviewResponse = bookClient.getBooksReview(bookId);
        ReviewSummaryResponse reviewSummaryResponse = bookClient.getReviewSummary(bookId);


        model.addAttribute("book", bookDetailDto);
        model.addAttribute("reviews", reviewResponse);
        model.addAttribute("reviewCount", reviewResponse.size());
        model.addAttribute("reviewSummary", reviewSummaryResponse);
        return "book/detail";
    }


    @PostMapping("/books/{book-id}/reviews")
    public String postReview(@PathVariable("book-id") Long bookId,
                             @ModelAttribute ReviewCreateRequest reviewCreateRequest,
                             @RequestParam(value = "files", required = false)List<MultipartFile> files){


        MultipartFile[] nonEmptyFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .toList().toArray(MultipartFile[]::new);




        bookClient.createReview(bookId,"1234", reviewCreateRequest, nonEmptyFiles);


        return "redirect:/books/"+bookId;






    }




}
