package shop.dodream.front.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final BookClient bookClient;
    private static final String REDIRECT_BOOK_CATEGORIES = "redirect:/admin/book/%d/categories";

    @GetMapping("/categories/{category-id}")
    public String getBooksByCategory(@PathVariable("category-id") Long categoryId,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     Model model) {
        int size  = 8;
        PageResponse<BookDto> bookPage = bookClient.getBooksByCategoryId(categoryId, page, size);
        model.addAttribute("books", bookPage.getContent());

        model.addAttribute("currentPage", bookPage.getNumber());
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("categoryId", categoryId);

        CategoryResponse category = bookClient.getCategory(categoryId);
        model.addAttribute("categoryName", category.getCategoryName());
        model.addAttribute("depth", category.getDepth());
        model.addAttribute("parentId", category.getParentId());

        Long baseCategoryId = (category.getParentId() == null) ? categoryId : category.getParentId();
        List<CategoryTreeResponse> response = bookClient.getCategoriesChildren(baseCategoryId);

        CategoryTreeResponse root = response.get(0);
        model.addAttribute("categoryTree", root);


        return "book/bookListCategory";
    }

    @GetMapping("/admin/category/register")
    public String showCategoryRegisterForm() {
        return "admin/category/categoryRegister";
    }


    @PostMapping("/admin/category/register")
    public String registerCategory(@ModelAttribute CategoryRequest request) {
        bookClient.createCategory(request);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories")
    public String getCategories(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "30") int size,
                                Model model){
        List<CategoryResponse> categories = bookClient.getAllCategories();

        int start = page * size;
        int end = Math.min(start + size, categories.size());
        List<CategoryResponse> pagedCategories = categories.subList(start, end);

        model.addAttribute("activeMenu", "categories");
        model.addAttribute("categoryList", pagedCategories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) categories.size() / size));
        return "admin/category/categoryList";
    }

    @GetMapping("/admin/category/edit/{category-id}")
    public String showCategoryEditForm(@PathVariable("category-id") Long categoryId, Model model) {
        CategoryResponse category = bookClient.getCategory(categoryId);
        model.addAttribute("category", category);
        return "admin/category/categoryEdit";
    }


    @PostMapping("/admin/category/edit/{category-id}")
    public String editCategory(@PathVariable("category-id") Long categoryId, @ModelAttribute CategoryRequest request) {
        bookClient.updateCategory(categoryId, request);
        return "redirect:/admin/categories";
    }


    @PostMapping("/admin/category/remove")
    public String removeCategory(Long categoryId) {
        bookClient.deleteCategory(categoryId);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/book/{book-id}/categories")
    public String getBookCategories(@PathVariable("book-id") Long bookId, Model model) throws JsonProcessingException {
        List<CategoryTreeResponse> bookCategory = bookClient.getCategoriesByBookId(bookId);

        List<CategoryResponse> flatBookCategory = bookClient.getFlatCategoriesByBookId(bookId);
        List<Long> flatCategoryIds = flatBookCategory.stream()
                .map(CategoryResponse::getCategoryId)
                .toList();

        List<CategoryResponse> categories = bookClient.getAllCategories();
        BookDetailDto book = bookClient.getBookDetail(bookId);

        model.addAttribute("book", book);
        model.addAttribute("bookId", bookId);
        model.addAttribute("flatBookCategory", flatBookCategory);
        model.addAttribute("flatCategoryIds", flatCategoryIds);
        model.addAttribute("bookCategories", bookCategory);
        model.addAttribute("categoryList", categories);
        return "admin/book/book-categoryList";
    }

    @PostMapping("/admin/book/{book-id}/category/register")
    public String registerBookCategory(@PathVariable("book-id") Long bookId,
                                       @RequestParam("categoryId") Long categoryId) {
        IdsListRequest request = new IdsListRequest(List.of(categoryId));
        bookClient.registerCategory(bookId, request);
        return String.format(REDIRECT_BOOK_CATEGORIES, bookId);
    }

    @PostMapping("/admin/book/{book-id}/category/edit")
    public String editBookCategory(@PathVariable("book-id") Long bookId,
                                     @RequestParam("categoryId") Long categoryId,
                                     @RequestParam("newCategoryId") Long newCategoryId) {
        bookClient.updateCategoryByBook(bookId, categoryId, newCategoryId);
        return String.format(REDIRECT_BOOK_CATEGORIES, bookId);
    }

    @PostMapping("/admin/book/{book-id}/category/remove")
    public String removeBookCategory(@PathVariable("book-id") Long bookId,
                                     @RequestParam("categoryId") Long categoryId) {
        IdsListRequest request = new IdsListRequest(List.of(categoryId));
        bookClient.deleteCategoriesByBook(bookId, request);
        return String.format(REDIRECT_BOOK_CATEGORIES, bookId);
    }

}
