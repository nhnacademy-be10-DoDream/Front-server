package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.dodream.front.dto.*;

import java.util.List;


@FeignClient(name = "bookClient", url = "${gateway.url}")
public interface BookClient {
    @GetMapping("/admin/books")
    List<BookDto> getBooks();

    @GetMapping("/public/categories/{depth}/depth")
    List<CategoryResponse> getCategoriesByDepth(@PathVariable("depth") Long depth);

    @GetMapping("/public/categories/{category-id}/books")
    PageResponse<BookDto> getBooksByCategoryId(@PathVariable("category-id") Long categoryId,
                                                                     @RequestParam("page") int page,
                                                                     @RequestParam("size") int size);

    @GetMapping("/public/categories/{category-id}/children")
    List<CategoryTreeResponse> getCategoriesChildren(@PathVariable("category-id") Long categoryId);

    @GetMapping("/public/categories/{category-id}")
    CategoryResponse getCategory(@PathVariable("category-id") Long categoryId);

    @GetMapping("/public/tags/{tag-id}/books")
    PageResponse<BookDto> getBooksByTagId(@PathVariable("tag-id") Long tagId);

    @GetMapping("/public/tags/{tag-id}/books")
    PageResponse<BookDto> getBooksByTagId(@PathVariable("tag-id") Long tagId,
                                                 @RequestParam("page") int page,
                                                 @RequestParam("size") int size);

    @GetMapping("/public/tags/{tag-id}")
    TagResponse getTag(@PathVariable("tag-id") Long tagId);


    @GetMapping("/public/books/{book-id}")
    BookDetailDto getBookDetail(@PathVariable("book-id") Long bookId);

    @GetMapping("/public/books/{book-id}/reviews")
    Page<ReviewResponse> getBooksReview(@PathVariable("book-id") Long bookId);

    @GetMapping("/public/reviews/{book-id}/review-summary")
    ReviewSummaryResponse getReviewSummary(@PathVariable("book-id") Long bookId);



    @PostMapping(value = "/books/{book-id}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Void createReview(
            @PathVariable("book-id") Long bookId,
            @RequestHeader("X-USER-ID") String userId,
            @RequestPart(value = "review") ReviewCreateRequest reviewCreateRequest,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    );

    @GetMapping("/reviews/me")
    Page<ReviewResponse> getReviews(Pageable pageable);

    @GetMapping("/likes/me")
    Page<BookListResponse> getLikedBooks(Pageable pageable);
}
