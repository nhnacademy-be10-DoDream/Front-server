package shop.dodream.front.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.BookDto;
import shop.dodream.front.dto.BookTagInfo;
import shop.dodream.front.dto.PageResponse;
import shop.dodream.front.dto.TagResponse;
import shop.dodream.front.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookClient bookClient;

    // Controller
    @GetMapping("/")
    public String home(Model model) {
        List<Long> tagIds = List.of(1L, 2L, 3L);

        List<BookTagInfo> bookTagInfos = tagIds.stream()
                .map(this::getBookTagInfo)
                .toList();

        model.addAttribute("bookTagInfos", bookTagInfos);
        model.addAttribute("chunksMap", bookTagInfos.stream()
                .collect(Collectors.toMap(BookTagInfo::getTagId, b -> chunkBooks(b.getBooks(), 6))));

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
