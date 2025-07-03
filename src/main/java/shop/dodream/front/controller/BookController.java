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


    @GetMapping("/books/{book-id}")
    public String bookDetail(@PathVariable("book-id") Long bookId, Model model){

        BookDetailDto bookDetailDto = bookClient.getBookDetail(bookId);
        String bookUrlPrefix = "https://dodream.shop/dodream-images/book/";
        List<String> convertedUrls = new ArrayList<>();

        for (String url : bookDetailDto.getBookUrls()) {
            convertedUrls.add(bookUrlPrefix + url);
        }

        bookDetailDto.setBookUrls(convertedUrls);

//        List<ReviewResponse> reviewResponse = bookClient.getBooksReview(bookId);

        List<ReviewResponse> reviewResponse = new ArrayList<>();

        reviewResponse.add(new ReviewResponse("북러버",  (short) 5, "초보자가 보기 정말 좋은 책이에요. 실습 위주 구성이라 이해가 잘 됩니다!"));
        reviewResponse.add(new ReviewResponse("개발꿈나무",  (short) 4, "대체로 좋았지만 몇몇 설명이 부족한 부분이 있어요. 그래도 만족!"));
        reviewResponse.add(new ReviewResponse("리뷰왕",  (short) 3, "내용이 방대해서 초반에 따라가기 힘들었습니다. 반복 학습 필요해요."));

//        ReviewSummaryResponse reviewSummaryResponse = bookClient.getReviewSummary(bookId);

        ReviewSummaryResponse reviewSummaryResponse = new ReviewSummaryResponse(2.5, 10L);


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

        String id = "testUser";
        bookClient.creteReview(bookId, reviewCreateRequest, files);


        return "redirect:/books/"+bookId;






    }




}
