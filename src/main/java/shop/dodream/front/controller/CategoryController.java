package shop.dodream.front.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final BookClient bookClient;

    @ModelAttribute("categories")
    public Map<CategoryResponse, List<CategoryResponse>> categories() {
        List<CategoryResponse> parentCategories = bookClient.getCategoriesByDepth(1L);
        List<CategoryResponse> childCategories = bookClient.getCategoriesByDepth(2L);

        Map<CategoryResponse, List<CategoryResponse>> categoryMap = new LinkedHashMap<>();

        for (CategoryResponse parent : parentCategories) {
            List<CategoryResponse> children = childCategories.stream()
                    .filter(child -> child.getParentId().equals(parent.getCategoryId()))
                    .toList();
            categoryMap.put(parent, children);
        }

        return categoryMap;
    }

    @GetMapping("/categories/{category-id}")
    public String getBooksByCategory(@PathVariable("category-id") Long categoryId,
                                     @RequestParam(value = "page", defaultValue = "0") int page,
                                     Model model) {
        int size  = 8;
        PageResponse<BookDto> bookPage = bookClient.getBooksByCategoryId(categoryId, page, size);
        for( BookDto book : bookPage.getContent()) {
            String bookUrlPrefix = "https://dodream.shop/dodream-images/book/";
            String imageUrl = bookUrlPrefix + book.getBookUrl();
            book.setBookUrl(imageUrl);
        }
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


        return "book/bookList";
    }
}
