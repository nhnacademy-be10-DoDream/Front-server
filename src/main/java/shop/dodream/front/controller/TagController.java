package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.*;

import java.util.List;

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

        return "/book/bookList";
    }

    @GetMapping("/admin/tag/register")
    public String showRegisterForm() {
        return "admin/tagRegister";
    }


    @PostMapping("/admin/tag/register")
    public String registerTag(@RequestParam("tagName") String newTagName) {
        bookClient.createTag(newTagName);
        return "redirect:/admin/tags";
    }

    @GetMapping("/admin/tags")
    public String getTags(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "20") int size,
                                Model model){
        List<TagResponse> tags = bookClient.getTags();

        int start = page * size;
        int end = Math.min(start + size, tags.size());
        List<TagResponse> pagedTags = tags.subList(start, end);

        model.addAttribute("tags", pagedTags);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) tags.size() / size));
        return "/admin/tagList";
    }

    @GetMapping("/admin/tag/modify/{tag-id}")
    public String showModifyForm(@PathVariable("tag-id") Long tagId, Model model) {
        TagResponse tag = bookClient.getTag(tagId);
        model.addAttribute("tag", tag);
        return "admin/tagModify";
    }


    @PostMapping("/admin/tag/modify/{tag-id}")
    public String modifyTag(@PathVariable("tag-id") Long tagId, @RequestParam("tagName") String newTagName) {
        bookClient.updateTag(tagId, newTagName);
        return "redirect:/admin/tags";
    }


    @PostMapping("/admin/tag/remove")
    public String removeTag(Long tagId) {
        bookClient.deleteTag(tagId);
        return "redirect:/admin/tags";
    }

    @GetMapping("/admin/book/tags")
    public String getTags(@RequestParam("bookId") Long bookId, Model model) {
        BookWithTagsResponse tags = bookClient.getTagsByBookId(bookId);
        BookDetailDto book = bookClient.getBookDetail(bookId);
        model.addAttribute("book", book);
        model.addAttribute("tags", tags);
        return "admin/bookTagList";
    }

    @GetMapping("/admin/book/tag/register")
    public String showBookTagRegisterForm(Model model) {
        return "admin/bookTagRegister";
    }

    @PostMapping("/admin/book/tag/register")
    public String registerBookTag(@RequestParam("bookId") Long bookId,
                                  @RequestParam("bookId") Long tagId) {
        bookClient.registerTag(bookId, tagId);
        return "redirect:/admin/book/tags?bookId=" + bookId;
    }

    @GetMapping("/admin/book/tag/modify")
    public String showBookTagModifyForm(Model model) {
        return "admin/bookTagModify";
    }

    @PostMapping("/admin/book/tag/modify")
    public String modifyBookTag(@RequestParam("bookId") Long bookId,
                                @RequestParam("tagId") Long tagId,
                                @RequestParam("newTagId") Long newTagId) {
        bookClient.updateTagByBook(bookId, tagId, newTagId);
        return "redirect:/admin/book/tags?bookId=" + bookId;
    }

    @PostMapping("/admin/book/tag/remove")
    public String removeBookTag(@RequestParam("bookId") Long bookId,
                                @RequestParam("bookId") Long tagId) {
        bookClient.deleteTagByBook(bookId, tagId);
        return "redirect:/admin/book/tags?bookId=" + bookId;
    }


}
