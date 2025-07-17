package shop.dodream.front.controller;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.client.CartClient;
import shop.dodream.front.dto.BookDto;
import shop.dodream.front.dto.BookTagInfo;
import shop.dodream.front.dto.PageResponse;
import shop.dodream.front.dto.TagResponse;
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
                             Model model) {

        BookDetailDto bookDetailDto = bookClient.getBookDetail(bookId);
        Page<ReviewResponse> reviewResponse = bookClient.getBooksReview(bookId, page, size);
        ReviewSummaryResponse reviewSummaryResponse = bookClient.getReviewSummary(bookId);
        List<CategoryTreeResponse> bookCategories = bookClient.getCategoriesByBookId(bookId);
        BookWithTagsResponse bookTag = bookClient.getTagsByBookId(bookId);



        // 컨트롤러에서 로그인 여부 조회 검증하는게 되면 가능
//        boolean isLiked = bookClient.bookLikeFindMe(bookId);


        model.addAttribute("book", bookDetailDto);
        model.addAttribute("reviews", reviewResponse);
        model.addAttribute("reviewCount", reviewResponse.getTotalElements());
        model.addAttribute("reviewSummary", reviewSummaryResponse);
        model.addAttribute("bookCategories", bookCategories);
        model.addAttribute("bookTags", bookTag);


//        model.addAttribute("isLiked", isLiked);
        return "book/detail";
    }

    @PostMapping("/books/{bookId}/like")
    public String likeBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes) {
        try {
            bookClient.registerBookLike(bookId);
            redirectAttributes.addFlashAttribute("likeMsg", "book.like.success");
        } catch (FeignException.Conflict e) {
            redirectAttributes.addFlashAttribute("likeMsg", "book.like.already");
        }
        return "redirect:/books/" + bookId;

    }


    @PostMapping("/books/{book-id}/reviews")
    public String postReview(@PathVariable("book-id") Long bookId,
                             @ModelAttribute ReviewCreateRequest reviewCreateRequest,
                             @RequestParam(value = "files", required = false) List<MultipartFile> files) {


        MultipartFile[] nonEmptyFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .toList().toArray(MultipartFile[]::new);


        bookClient.createReview(bookId, reviewCreateRequest, nonEmptyFiles);


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
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "100") int size,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) Integer minPrice,
                              @RequestParam(required = false) Integer maxPrice,
                              Model model) {

        PageResponse<BookItemResponse> books;
        final Set<Long> categoryIds = new HashSet<>();
        List<CategoryTreeResponse> categoryTree = null;
        try {
            if(categoryId != null) {
                categoryTree = bookClient.getCategoriesChildren(categoryId);
                collectCategoryIds(categoryTree, categoryIds);
            }
            books = bookClient.searchBooks(keyword, sort, page, size);
        } catch (Exception e) {
            model.addAttribute("books", Collections.emptyList());
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 0);
            return "book/bookSearchList";
        }

        model.addAttribute("totalPages", books.getTotalPages());

        List<BookItemResponse> filteredBooks = books.getContent().stream()
                .filter(book -> {
                    if (minPrice != null && book.getSalePrice() < minPrice) return false;
                    if (maxPrice != null && book.getSalePrice() > maxPrice) return false;
                    if (categoryId != null && book.getCategoryIds() != null) {
                        return book.getCategoryIds().stream().anyMatch(categoryIds::contains);
                    }
                    return true;
                })
                .toList();

        Map<Long, Integer> categoryCountMap = new HashMap<>();
        buildCategoryCountMap(filteredBooks, bookClient, categoryCountMap);

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

        int pageSize = 20;
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, filteredBooks.size());
        if (fromIndex >= filteredBooks.size()) {
            fromIndex = 0;
            toIndex = Math.min(pageSize, filteredBooks.size());
        }
        List<BookItemResponse> pagedBooks = filteredBooks.subList(fromIndex, toIndex);
        int totalPages = (int) Math.ceil((double) filteredBooks.size() / pageSize);

        model.addAttribute("selectedCategoryTree", categoryTree);
        model.addAttribute("books", pagedBooks);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", totalPages);
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
    private void buildCategoryCountMap(List<BookItemResponse> books, BookClient bookClient,
                                       Map<Long, Integer> categoryCountMap) {
        for (BookItemResponse book : books) {
            Set<Long> countedCategoryIds = new HashSet<>();

            List<CategoryResponse> categories = bookClient.getFlatCategoriesByBookId(book.getBookId());
            for (CategoryResponse category : categories) {
                List<CategoryResponse> path = bookClient.getCategoriesPath(category.getCategoryId());
                for (CategoryResponse c : path) {
                    if (countedCategoryIds.add(c.getCategoryId())) {
                        categoryCountMap.put(
                                c.getCategoryId(),
                                categoryCountMap.getOrDefault(c.getCategoryId(), 0) + 1
                        );
                    }
                }
            }
        }
    }
}
