package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.BookDto;
import shop.dodream.front.dto.PageResponse;

@Controller
@RequiredArgsConstructor
public class TagController {
    private final BookClient bookClient;

    @GetMapping("/tags/{tag-id}")
    public String getBooksByTag(@PathVariable("tag-id") Long tagId,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                Model model) {

        int size  = 8;
        PageResponse<BookDto> bookPage = bookClient.getBooksByTagId(tagId, page, size);
        for (BookDto book : bookPage.getContent()) {
            String bookUrlPrefix = "https://dodream.shop/dodream-images/book/";
            String imageUrl = bookUrlPrefix + book.getBookUrl();
            book.setBookUrl(imageUrl);
        }
        model.addAttribute("books", bookPage.getContent());

        model.addAttribute("currentPage", bookPage.getNumber());
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("tagId", tagId);

        return "book/bookList";
    }
}
