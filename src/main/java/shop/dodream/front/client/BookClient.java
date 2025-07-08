package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.dodream.front.config.FeignMultipartSupportConfig;
import shop.dodream.front.dto.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@FeignClient(name = "bookClient", url = "${gateway.url}", configuration = FeignMultipartSupportConfig.class)
//@FeignClient(name = "bookClient", url = "http://localhost:10320", configuration = FeignMultipartSupportConfig.class)
public interface BookClient {

    @GetMapping("/admin/books")
    Page<BookDto> getBooks(Pageable pageable);

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
    Page<ReviewResponse> getBooksReview(@PathVariable("book-id") Long bookId,
                                        @RequestParam("page") int page,
                                        @RequestParam("size") int size);

    @GetMapping("/public/reviews/{book-id}/review-summary")
    ReviewSummaryResponse getReviewSummary(@PathVariable("book-id") Long bookId);



    @PostMapping(value = "/books/{book-id}/reviews", consumes = MULTIPART_FORM_DATA_VALUE)
    Void createReview(
            @PathVariable("book-id") Long bookId,
//            @RequestHeader("X-USER-ID") String userId,
            @RequestPart(value = "review") ReviewCreateRequest reviewCreateRequest,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    );

    @PostMapping("/admin/books/aladdin-api")
    Void aladdinRegisterBook(@RequestParam("isbn") String isbn);

    @PostMapping(value = "/admin/books", consumes = MULTIPART_FORM_DATA_VALUE)
    Void registerBook(@RequestPart(value = "book") BookRegisterRequest bookRegisterRequest,
                      @RequestPart(value = "files", required = false) MultipartFile[] files);



    @DeleteMapping("/admin/books/{book-id}")
    Void deleteBook(@PathVariable("book-id") Long bookId);

    @GetMapping("/admin/books/{book-id}")
    BookDetailDto getAdminBookDetail(@PathVariable("book-id") Long bookId);


    @PutMapping(value = "/admin/books/{book-id}", consumes = MULTIPART_FORM_DATA_VALUE)
    Void updateBook(@PathVariable("book-id") Long bookId,
                    @RequestPart(value = "book") BookUpdateRequest bookUpdateRequest,
                    @RequestPart(value = "files", required = false) MultipartFile[] files);














    @GetMapping("/reviews/me")
    Page<ReviewResponse> getReviews(Pageable pageable);

    @GetMapping("/likes/me")
    Page<BookListResponse> getLikedBooks(Pageable pageable);
}
