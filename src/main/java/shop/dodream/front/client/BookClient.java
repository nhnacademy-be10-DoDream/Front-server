package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.dodream.front.config.FeignMultipartSupportConfig;
import shop.dodream.front.dto.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;


//@FeignClient(name = "bookClient", url = "http://localhost:10320", configuration = FeignMultipartSupportConfig.class)
@FeignClient(name = "bookClient", url = "http://localhost:8090", configuration = FeignMultipartSupportConfig.class)
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

    @GetMapping("/admin/reviews/{book-id}/review-summary")
    ReviewSummaryResponse getReviewSummary(@PathVariable("book-id") Long bookId);



    @PostMapping(value = "/books/{book-id}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Void createReview(
            @PathVariable("book-id") Long bookId,
            @RequestHeader("X-USER-ID") String userId,
            @RequestPart(value = "review") ReviewCreateRequest reviewCreateRequest,
            @RequestPart(value = "files", required = false) MultipartFile[] files
    );


}
