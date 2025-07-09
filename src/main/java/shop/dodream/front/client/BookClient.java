package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import shop.dodream.front.dto.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@FeignClient(name = "bookClient", url = "${gateway.url}" )
public interface BookClient {
    @GetMapping("/admin/books")
    List<BookDto> getBooks();

    @GetMapping("/public/books/all")
    List<BookDto> getAllBooks();

    // 카테고리 영역
    @PostMapping("/admin/categories")
    void createCategory(@RequestBody CategoryRequest request);


    @GetMapping("/public/categories/{depth}/depth")
    List<CategoryResponse> getCategoriesByDepth(@PathVariable("depth") Long depth);

    @GetMapping("/public/categories")
    List<CategoryResponse> getAllCategories();

    @GetMapping("/public/categories/{category-id}/children")
    List<CategoryTreeResponse> getCategoriesChildren(@PathVariable("category-id") Long categoryId);

    @GetMapping("/public/categories/{category-id}")
    CategoryResponse getCategory(@PathVariable("category-id") Long categoryId);

    @PutMapping("/admin/categories/{category-id}")
    CategoryResponse updateCategory(@PathVariable("category-id") Long categoryId,
                                           @RequestBody CategoryRequest request);

    @DeleteMapping("/admin/categories/{category-id}")
    void deleteCategory(@PathVariable("category-id") Long categoryId);

    // 도서 카테고리 영역
    @PostMapping("/admin/books/{book-id}/categories")
    void registerCategory(@PathVariable("book-id") Long bookId,
                          @RequestBody IdsListRequest categoryIds);

    @GetMapping("/public/books/{book-id}/categories")
    List<CategoryTreeResponse> getCategoriesByBookId(@PathVariable("book-id") Long bookId);

    @GetMapping("/public/categories/{category-id}/books")
    PageResponse<BookDto> getBooksByCategoryId(@PathVariable("category-id") Long categoryId,
                                               @RequestParam("page") int page,
                                               @RequestParam("size") int size);

    @PutMapping("/admin/books/{book-id}/categories/{category-id}")
    void updateCategoryByBook(@PathVariable("book-id") Long bookId,
                                     @PathVariable("category-id") Long categoryId,
                                     @RequestParam(value = "new-category-id") Long newCategoryId);

    @DeleteMapping("/admin/books/{book-id}/categories")
    void deleteCategoriesByBook(@PathVariable("book-id") Long bookId,
                                                       @RequestBody IdsListRequest categoryIds);

    // 태그 영역
    @PostMapping("/admin/tags")
    void createTag(@RequestParam String newTagName);

    @GetMapping("/public/tags/{tag-id}")
    TagResponse getTag(@PathVariable("tag-id") Long tagId);

    @GetMapping("/public/tags")
    List<TagResponse> getTags();

    @PutMapping("/admin/tags/{tag-id}")
    void updateTag(@PathVariable("tag-id") Long tagId,
                                 @RequestParam String newTagName);

    @DeleteMapping("/admin/tags/{tag-id}")
    void deleteTag(@PathVariable("tag-id") Long tagId);

    // 도서 태그 영역
    @PostMapping("/admin/books/{book-id}/tags/{tag-id}")
    void registerTag(@PathVariable("book-id") Long bookId,
                     @PathVariable("tag-id") Long tagId);

    @GetMapping("/public/books/{book-id}/tags")
    BookWithTagsResponse getTagsByBookId(@PathVariable("book-id") Long bookId);

    @GetMapping("/public/tags/{tag-id}/books")
    PageResponse<BookDto> getBooksByTagId(@PathVariable("tag-id") Long tagId);

    @GetMapping("/public/tags/{tag-id}/books")
    PageResponse<BookDto> getBooksByTagId(@PathVariable("tag-id") Long tagId,
                                                 @RequestParam("page") int page,
                                                 @RequestParam("size") int size);

    @PutMapping("/admin/books/{book-id}/tags/{tag-id}")
    void updateTagByBook(@PathVariable("book-id") Long bookId,
                                               @PathVariable("tag-id") Long tagId,
                                               @RequestParam Long newTagId);

    @DeleteMapping("/admin/books/{book-id}/tags/{tag-id}")
    void deleteTagByBook(@PathVariable("book-id") Long bookId,
                         @PathVariable("tag-id") Long tagId);


    @GetMapping("/public/books/{book-id}")
    BookDetailDto getBookDetail(@PathVariable("book-id") Long bookId);

    @GetMapping("/public/books/{book-id}")
    BookDto getBook(@PathVariable("book-id") Long bookId);

    @GetMapping("/public/books/{book-id}/reviews")
    Page<ReviewResponse> getBooksReview(@PathVariable("book-id") Long bookId);

    @GetMapping("/public/reviews/{book-id}/review-summary")
    ReviewSummaryResponse getReviewSummary(@PathVariable("book-id") Long bookId);

    @GetMapping("/books/search")
    PageResponse<BookItemResponse> searchBooks(@RequestParam String keyword,
                                               @RequestParam(value = "sort", required = false, defaultValue = "NONE") BookSortType sort,
                                               @RequestParam("page") int page,
                                               @RequestParam("size") int size);

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
