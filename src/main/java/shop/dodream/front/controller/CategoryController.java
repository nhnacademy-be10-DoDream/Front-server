package shop.dodream.front.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.BookListResponseRecord;
import shop.dodream.front.dto.CategoryResponse;
import shop.dodream.front.dto.CategoryTreeResponse;
import shop.dodream.front.dto.PageResponse;

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
        PageResponse<BookListResponseRecord> bookPage = bookClient.getBooksByCategoryId(categoryId, page, size);
        //System.out.println("응답 content: " + bookPage.getContent());

        model.addAttribute("books", bookPage.getContent());
        List<BookListResponseRecord> books = bookPage.getContent();

        model.addAttribute("currentPage", bookPage.getNumber());
        model.addAttribute("totalPages", bookPage.getTotalPages());
        model.addAttribute("categoryId", categoryId);

        CategoryResponse category = bookClient.getCategory(categoryId);
        model.addAttribute("categoryName", category.getCategoryName());
        model.addAttribute("depth", category.getDepth());
        model.addAttribute("parentId", category.getParentId());

        Long baseCategoryId = (category.getParentId() == null) ? categoryId : category.getParentId();
        List<CategoryTreeResponse> response = bookClient.getCategoriesChildren(baseCategoryId);

        // 현재 카테고리의 최상위 노드
        CategoryTreeResponse root = response.get(0);
        model.addAttribute("categoryTree", root);

//        model.addAttribute("children", root.getChildren());
//        model.addAttribute("root", root.getCategoryName());

        return "book/bookList";
    }

    private String getFirstAuthor(String author) {
        if (author == null || author.isEmpty()) return "";
        return author.split(",")[0].trim();
    }



}
