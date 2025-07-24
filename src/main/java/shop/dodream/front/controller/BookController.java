package shop.dodream.front.controller;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.client.CartClient;
import shop.dodream.front.dto.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookClient bookClient;
    private final CartClient cartClient;
    private final CartController cartController;


    // Controller
    @GetMapping("/")
    public String home(@RequestParam(value = "page", defaultValue = "0") int page,
                       Model model, HttpServletRequest request, HttpServletResponse response) {
        List<Long> tagIds = List.of(1L, 2L, 3L);

        List<BookTagInfo> bookTagInfos = tagIds.stream()
                .map(this::getBookTagInfo)
                .toList();

        model.addAttribute("bookTagInfos", bookTagInfos);
        model.addAttribute("chunksMap", bookTagInfos.stream()
                .collect(Collectors.toMap(BookTagInfo::getTagId, b -> chunkBooks(b.getBooks(), 6))));

        List<BookDto> books = bookClient.getAllBooks();
        String guestId = cartController.getGuestIdFromCookie(request);
        String acessToken = cartController.getAccessTokenFromCookies(request.getCookies());
        if (guestId != null && acessToken != null) {
            cartClient.mergeCart(guestId);
            cartController.deleteGuestIdCookie(response);
        }
        model.addAttribute("books", books);
        return "home";
    }

    private List<List<BookDto>> chunkBooks(List<BookDto> books, int chunkSize) {
        List<List<BookDto>> chunks = new ArrayList<>();
        for (int i = 0; i < books.size(); i += chunkSize) {
            chunks.add(books.subList(i, Math.min(i + chunkSize, books.size())));
        }
        return chunks;
    }

    private BookTagInfo getBookTagInfo(Long tagId) {
        PageResponse<BookDto> response = bookClient.getBooksByTagId(tagId);
        List<BookDto> books = response.getContent();
        TagResponse tag = bookClient.getTag(tagId);
        return new BookTagInfo(tagId, tag, books);
    }


    @GetMapping("/books/{book-id}")
    public String bookDetail(@PathVariable("book-id") Long bookId,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size,
                             Model model, HttpServletRequest request) {

        BookDetailDto bookDetailDto = bookClient.getBookDetail(bookId);
        Page<ReviewResponse> reviewResponse = bookClient.getBooksReview(bookId, page, size);
        ReviewSummaryResponse reviewSummaryResponse = bookClient.getReviewSummary(bookId);
        List<CategoryTreeResponse> bookCategories = bookClient.getCategoriesByBookId(bookId);
        BookWithTagsResponse bookTag = bookClient.getTagsByBookId(bookId);


        boolean isLiked = false;

        String accessToken = cartController.getAccessTokenFromCookies(request.getCookies());
        if (accessToken != null) {
            isLiked = bookClient.bookLikeFindMe(bookId);
        }

        model.addAttribute("book", bookDetailDto);
        model.addAttribute("reviews", reviewResponse);
        model.addAttribute("reviewCount", reviewResponse.getTotalElements());
        model.addAttribute("reviewSummary", reviewSummaryResponse);
        model.addAttribute("bookCategories", bookCategories);
        model.addAttribute("bookTags", bookTag);
        model.addAttribute("isLiked", isLiked);
        return "book/detail";
    }

    @PostMapping("/books/{bookId}/like")
    public String likeBook(@PathVariable Long bookId) {
            bookClient.registerBookLike(bookId);
        return "redirect:/books/" + bookId;

    }


    @PostMapping("/books/{book-id}/reviews")
    public String postReview(@PathVariable("book-id") Long bookId,
                             @Valid @ModelAttribute ReviewCreateRequest reviewCreateRequest,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files,
                             RedirectAttributes redirectAttributes) {


        MultipartFile[] nonEmptyFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .toList().toArray(MultipartFile[]::new);

        try {
            bookClient.createReview(bookId, reviewCreateRequest, nonEmptyFiles);
        }catch (FeignException.Forbidden e) {
            redirectAttributes.addFlashAttribute("error", "구매한 도서만 리뷰를 작성 가능합니다.");
        }


        return "redirect:/books/" + bookId;
    }


    @GetMapping("/admin/books")
    public String adminBookList(Model model, @PageableDefault(value = 20) Pageable pageable) {
        Page<BookDto> bookDtoList = bookClient.getBooks(pageable);
        model.addAttribute("activeMenu", "books");

        model.addAttribute("books", bookDtoList);
        return "admin/book/book";
    }

    @PostMapping("/admin/books/register")
    public String registerBook(@ModelAttribute BookRegisterRequest registerRequest,
                               @RequestParam(value = "files", required = false) List<MultipartFile> files) {


        MultipartFile[] nonEmptyFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .toList().toArray(MultipartFile[]::new);

        bookClient.registerBook(registerRequest, nonEmptyFiles);

        return "redirect:/admin/books";

    }

    @GetMapping("/admin/books/aladdin-search")
    public String AladdinSearchList(@RequestParam("query") String query,
                                    @RequestParam(defaultValue = "25") int size,
                                    @RequestParam(defaultValue = "1") int page,
                                    Model model){

        AladdinBookSearchResult aladdinBookSearchResult = bookClient.getAladdinBookList(query, size, page);
        int totalPages = (int) Math.ceil((double) aladdinBookSearchResult.getTotalResults() / size);

        int groupSize = 8;
        int startPage = ((page - 1) / groupSize) * groupSize + 1;
        int endPage = Math.min(startPage + groupSize - 1, totalPages);


        model.addAttribute("aladdin", aladdinBookSearchResult);
        model.addAttribute("query", query);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/book/book-api-register";
    }

    @PostMapping("/admin/books/register-api")
    public String registerBookFromAladdin(@ModelAttribute BookRegisterRequest request) {
        bookClient.registerFromAladdin(request);
        return "redirect:/admin/books";
    }

    @DeleteMapping("/admin/books/delete/{book-id}")
    public String deleteBook(@PathVariable("book-id") Long bookId) {
        bookClient.deleteBook(bookId);
        return "redirect:/admin/books";
    }

    @GetMapping("/admin/books/detail/{book-id}")
    public String adminDetailBook(@PathVariable("book-id") Long bookId, Model model) {
        BookDetailDto bookDetailDto = bookClient.getAdminBookDetail(bookId);
        BookWithTagsResponse bookTag = bookClient.getTagsByBookId(bookId);
        List<CategoryTreeResponse> bookCategories = bookClient.getCategoriesByBookId(bookId);


        model.addAttribute("book", bookDetailDto);
        model.addAttribute("bookTags", bookTag);
        model.addAttribute("bookCategories", bookCategories);

        return "admin/book/book-detail";

    }

    @GetMapping("/admin/books/edit")
    public String editBookForm(@RequestParam("bookId") Long bookId, Model model) {
        BookDetailDto bookDetailDto = bookClient.getAdminBookDetail(bookId);
        model.addAttribute("book", bookDetailDto);

        return "admin/book/book-edit";
    }

    @PutMapping("/admin/books/{book-id}/edit")
    public String updateBook(@PathVariable("book-id") Long bookId,
                             @ModelAttribute BookUpdateRequest bookUpdateRequest,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files) {

        MultipartFile[] nonEmptyFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .toList().toArray(MultipartFile[]::new);

        bookClient.updateBook(bookId, bookUpdateRequest, nonEmptyFiles);

        return "redirect:/admin/books/detail/" + bookId;

    }


    @GetMapping("/search")
    public String searchBooks(@RequestParam String keyword,
                              @RequestParam(defaultValue = "NONE") BookSortType sort,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) Integer minPrice,
                              @RequestParam(required = false) Integer maxPrice,
                              Model model) {

        Pageable pageable = PageRequest.of(page, size);
        List<Long> categoryIds = null;
        List<CategoryTreeResponse> categoryTree = null;


        if (categoryId != null) {
            categoryTree = bookClient.getCategoriesChildren(categoryId);
            Set<Long> categoryIdSet = new HashSet<>();
            categoryIdSet.add(categoryId);
            collectCategoryIds(categoryTree, categoryIdSet);
            categoryIds = new ArrayList<>(categoryIdSet);
        }

        SearchBookResponse response = bookClient.searchBooks(keyword, sort, pageable, categoryIds, minPrice, maxPrice);

        Map<Long, Integer> categoryCountMap = response.getCategoryCountMap();
        PageResponse<BookItemResponse> books = response.getBooks();

        if (categoryTree == null) {
            List<CategoryResponse> depth1Categories = bookClient.getCategoriesByDepth(1L);
            categoryTree = new ArrayList<>();
            for (CategoryResponse parent : depth1Categories) {
                List<CategoryTreeResponse> children = bookClient.getCategoriesChildren(parent.getCategoryId());
                categoryTree.addAll(children);
            }
        }

        if (categoryId != null) {
            List<CategoryResponse> breadcrumb = bookClient.getCategoriesPath(categoryId);
            model.addAttribute("breadcrumb", breadcrumb);
        }

        model.addAttribute("selectedCategoryTree", categoryTree);
        model.addAttribute("books", books.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("sort", sort);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("categoryCountMap", categoryCountMap);

        return "book/bookSearchList";
    }

    private void collectCategoryIds(List<CategoryTreeResponse> nodes, Set<Long> idSet) {
        if (nodes == null) return;
        for (CategoryTreeResponse node : nodes) {
            idSet.add(node.getCategoryId());
            collectCategoryIds(node.getChildren(), idSet);
        }
    }


}
