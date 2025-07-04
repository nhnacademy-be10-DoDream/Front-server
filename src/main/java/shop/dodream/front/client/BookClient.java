package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.dodream.front.config.FeignMultipartSupportConfig;
import shop.dodream.front.dto.*;

import java.util.List;


//@FeignClient(name = "bookClient", url = "s1.java21.net:10325", configuration = FeignMultipartSupportConfig.class)
@FeignClient(name = "bookClient", url = "http://localhost:8090", configuration = FeignMultipartSupportConfig.class)
public interface BookClient {
    @GetMapping("/admin/books")
    List<BookDto> getBooks();

    @GetMapping("/public/books/{book-id}")
    BookDetailDto getBookDetail(@PathVariable("book-id") Long bookId);

    @GetMapping("/books/{book-id}/reviews")
    List<ReviewResponse> getBooksReview(@PathVariable("book-id") Long bookId);

    @GetMapping("/reviews/{book-id}/review-summary")
    ReviewSummaryResponse getReviewSummary(@PathVariable("book-id") Long bookId);



    @PostMapping(value = "/books/{book-id}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Void createReview(
            @PathVariable("book-id") Long bookId,
            @RequestHeader("X-USER-ID") String userId,
            @RequestPart(value = "review") ReviewCreateRequest reviewCreateRequest,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    );


}
