package shop.dodream.front.controller;

import feign.FeignException;
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


    // Controller
    @GetMapping("/")
    public String home(@RequestParam(value = "page", defaultValue = "0") int page,
                       Model model) {
        List<Long> tagIds = List.of(1L, 2L, 3L);

        List<BookTagInfo> bookTagInfos = tagIds.stream()
                .map(this::getBookTagInfo)
                .toList();

        model.addAttribute("bookTagInfos", bookTagInfos);
        model.addAttribute("chunksMap", bookTagInfos.stream()
                .collect(Collectors.toMap(BookTagInfo::getTagId, b -> chunkBooks(b.getBooks(), 6))));

        List<BookDto> books = bookClient.getAllBooks();
        for( BookDto book : books) {
            String bookUrlPrefix = "https://dodream.shop/dodream-images/book/";
            String imageUrl = bookUrlPrefix + book.getBookUrl();
            book.setBookUrl(imageUrl);
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
        String bookUrlPrefix = "https://dodream.shop/dodream-images/book/";
        books.forEach(book -> book.setBookUrl(bookUrlPrefix + book.getBookUrl()));
        TagResponse tag = bookClient.getTag(tagId);
        return new BookTagInfo(tagId, tag, books);
    }


    @GetMapping("/books/{book-id}")
    public String bookDetail(@PathVariable("book-id") Long bookId,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "5") int size,
                             Model model){

        BookDetailDto bookDetailDto = bookClient.getBookDetail(bookId);
        Page<ReviewResponse> reviewResponse = bookClient.getBooksReview(bookId, page, size);
        ReviewSummaryResponse reviewSummaryResponse = bookClient.getReviewSummary(bookId);



        // 컨트롤러에서 로그인 여부 조회 검증하는게 되면 가능
//        boolean isLiked = bookClient.bookLikeFindMe(bookId);





        model.addAttribute("book", bookDetailDto);
        model.addAttribute("reviews", reviewResponse);
        model.addAttribute("reviewCount", reviewResponse.getTotalElements());
        model.addAttribute("reviewSummary", reviewSummaryResponse);
//        model.addAttribute("isLiked", isLiked);
        return "book/detail";
    }

    @PostMapping("/books/{bookId}/like")
    public String likeBook(@PathVariable Long bookId, RedirectAttributes redirectAttributes){
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
                             @RequestParam(value = "files", required = false)List<MultipartFile> files){


        MultipartFile[] nonEmptyFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .toList().toArray(MultipartFile[]::new);


        bookClient.createReview(bookId, reviewCreateRequest, nonEmptyFiles);


        return "redirect:/books/"+bookId;



    }

    @GetMapping("/admin/books")
    public String adminBookList(Model model, @PageableDefault(value = 20) Pageable pageable){
        Page<BookDto> bookDtoList = bookClient.getBooks(pageable);
        model.addAttribute("activeMenu", "books");

        model.addAttribute("books", bookDtoList);
        return "admin/book/book";
    }

    @PostMapping("/admin/books/register")
    public String registerBook(@ModelAttribute BookRegisterRequest registerRequest,
                               @RequestParam(value = "files", required = false)List<MultipartFile> files) {


        MultipartFile[] nonEmptyFiles = files.stream()
                .filter(file -> !file.isEmpty())
                .toList().toArray(MultipartFile[]::new);

        bookClient.registerBook(registerRequest, nonEmptyFiles);

        return "redirect:/admin/books";

    }

    @PostMapping("/admin/books/register-api")
    public String registerBookFromAladdin(@RequestParam("isbn") String isbn){
        bookClient.aladdinRegisterBook(isbn);
        return "redirect:/admin/books";
    }

    @DeleteMapping("/admin/books/delete/{book-id}")
    public String deleteBook(@PathVariable("book-id") Long bookId){
        bookClient.deleteBook(bookId);
        return "redirect:/admin/books";
    }

    @GetMapping("/admin/books/detail/{book-id}")
    public String adminDetailBook(@PathVariable("book-id") Long bookId, Model model){
        BookDetailDto bookDetailDto = bookClient.getAdminBookDetail(bookId);

        model.addAttribute("book", bookDetailDto);

        return "admin/book/book-detail";

    }

    @GetMapping("/admin/books/edit")
    public String editBookForm(@RequestParam("bookId") Long bookId, Model model){
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

        return "redirect:/admin/books/detail/"+bookId;

    }


    @GetMapping("/search")
    public String searchBooks(@RequestParam String keyword,
                              @RequestParam(defaultValue = "NONE") BookSortType sort,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "8") int size,
                              Model model) {

        PageResponse<BookItemResponse> books;
        try {
            books = bookClient.searchBooks(keyword, sort, page, size);
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("books", Collections.emptyList());
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentPage", 0);
            return "book/bookSearchList";
        }
        System.out.println(books.getContent().size());

        List<BookDto> bookDtos = new ArrayList<>();
        for (BookItemResponse book : books.getContent()) {
            try {
                BookDto bookDto = bookClient.getBook(book.getBookId());
                String prefix = "https://dodream.shop/dodream-images/book/";
                String imageUrl = prefix + bookDto.getBookUrl();
                bookDto.setBookUrl(imageUrl);
                bookDtos.add(bookDto);
            } catch (FeignException.NotFound e) {
                continue;
            }
        }
        model.addAttribute("books", bookDtos);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalPages", books.getTotalPages());
        model.addAttribute("currentPage", books.getNumber());
        model.addAttribute("sort", sort);
        return "book/bookSearchList";
    }








}
