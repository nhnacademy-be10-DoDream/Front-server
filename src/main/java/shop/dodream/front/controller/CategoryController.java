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
public class CategoryController {

    private final BookClient bookClient;

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


        return "/book/bookListCategory";
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

    @GetMapping("/admin/category/modify/{category-id}")
    public String showCategoryModifyForm(@PathVariable("category-id") Long categoryId, Model model) {
        CategoryResponse category = bookClient.getCategory(categoryId);
        model.addAttribute("category", category);
        return "admin/category/categoryModify";
    }


    @PostMapping("/admin/category/modify/{category-id}")
    public String modifyCategory(@PathVariable("category-id") Long categoryId, @ModelAttribute CategoryRequest request) {
        bookClient.updateCategory(categoryId, request);
        return "redirect:/admin/categories";
    }


    @PostMapping("/admin/category/remove")
    public String removeCategory(Long categoryId) {
        bookClient.deleteCategory(categoryId);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/book/categories")
    public String getCategories(@RequestParam("bookId") Long bookId, Model model) {
        List<CategoryTreeResponse> categories = bookClient.getCategoriesByBookId(bookId);
        BookDetailDto book = bookClient.getBookDetail(bookId);
        model.addAttribute("book", book);
        model.addAttribute("categories", categories);
        return "admin/bookCategoryList";
    }

    @GetMapping("/admin/book/category/register")
    public String showBookCategoryRegisterForm(Model model) {
        return "admin/bookCategoryRegister";
    }

    @PostMapping("/admin/book/category/register")
    public String registerBookCategory(@RequestParam("bookId") Long bookId,
                                       @RequestBody IdsListRequest categoryIds) {
        bookClient.registerCategory(bookId, categoryIds);
        return "redirect:/admin/book/categories?bookId=" + bookId;
    }

    @GetMapping("/admin/book/category/modify")
    public String showBookCategoryModifyForm(Model model) {
        return "admin/bookCategoryModify";
    }

    @PostMapping("/admin/book/category/modify")
    public String modifyBookCategory(@RequestParam("bookId") Long bookId,
                                     @RequestParam("categoryId") Long categoryId,
                                     @RequestParam("newCategoryId") Long newCategoryId) {
        bookClient.updateCategoryByBook(bookId, categoryId, newCategoryId);
        return "redirect:/admin/book/categories?bookId=" + bookId;
    }

    @PostMapping("/admin/book/category/remove")
    public String removeBookCategory(@RequestParam("bookId") Long bookId,
                                     @RequestBody IdsListRequest categoryIds) {
        bookClient.deleteCategoriesByBook(bookId, categoryIds);
        return "redirect:/admin/book/categories?bookId=" + bookId;
    }

}
