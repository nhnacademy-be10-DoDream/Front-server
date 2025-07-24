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
    private static final String REDIRECT_BOOK_TAGS = "redirect:/admin/book/%d/tags";

    @GetMapping("/tags/{tag-id}")
    public String getBooksByTag(@PathVariable("tag-id") Long tagId,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                Model model) {

        int size  = 12;
        PageResponse<BookDto> bookPage = bookClient.getBooksByTagId(tagId, page, size);

        model.addAttribute("books", bookPage.getContent());

        model.addAttribute("currentPage", bookPage.getNumber());
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("tagId", tagId);

        return "book/bookListTag";
    }

    @GetMapping("/admin/tag/register")
    public String showRegisterForm() {
        return "admin/tag/tagRegister";
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

        model.addAttribute("activeMenu", "tags");
        model.addAttribute("tags", pagedTags);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) tags.size() / size));
        return "admin/tag/tagList";
    }

    @GetMapping("/admin/tag/edit/{tag-id}")
    public String showEditForm(@PathVariable("tag-id") Long tagId, Model model) {
        TagResponse tag = bookClient.getTag(tagId);
        model.addAttribute("tag", tag);
        return "admin/tag/tagEdit";
    }

    @PostMapping("/admin/tag/edit/{tag-id}")
    public String editTag(@PathVariable("tag-id") Long tagId, @RequestParam("tagName") String newTagName) {
        bookClient.updateTag(tagId, newTagName);
        return "redirect:/admin/tags";
    }


    @PostMapping("/admin/tag/remove")
    public String removeTag(Long tagId) {
        bookClient.deleteTag(tagId);
        return "redirect:/admin/tags";
    }

    @GetMapping("/admin/book/{book-id}/tags")
    public String getTags(@PathVariable("book-id") Long bookId, Model model) {
        BookWithTagsResponse bookTag = bookClient.getTagsByBookId(bookId);
        List<TagResponse> tags = bookClient.getTags();
        BookDetailDto book = bookClient.getBookDetail(bookId);

        model.addAttribute("book", book);
        model.addAttribute("bookId", bookId);
        model.addAttribute("bookTag", bookTag);
        model.addAttribute("tags", tags);
        return "admin/book/book-tagList";
    }

    @PostMapping("/admin/book/{book-id}/tag/register")
    public String registerBookTag(@PathVariable("book-id") Long bookId,
                                  @RequestParam("tagId") Long tagId) {
        bookClient.registerTag(bookId, tagId);
        return String.format(REDIRECT_BOOK_TAGS, bookId);
    }

    @PostMapping("/admin/book/{book-id}/tag/{tag-id}/edit")
    public String editBookTag(@PathVariable("book-id") Long bookId,
                                @PathVariable("tag-id") Long tagId,
                                @RequestParam("newTagId") Long newTagId) {
        bookClient.updateTagByBook(bookId, tagId, newTagId);
        return String.format(REDIRECT_BOOK_TAGS, bookId);
    }

    @PostMapping("/admin/book/{book-id}/tag/remove")
    public String removeBookTag(@PathVariable("book-id") Long bookId,
                                @RequestParam("tagId") Long tagId) {
        bookClient.deleteTagByBook(bookId, tagId);
        return String.format(REDIRECT_BOOK_TAGS, bookId);
    }
}
