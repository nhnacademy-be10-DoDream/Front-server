package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.CategoryResponse;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class CategoryController {

    private final BookClient bookClient;

//    @ModelAttribute("categories")
//    public List<CategoryResponse> getCategories() {
//        return bookClient.getCategoriesByDepth(1L);
//    }
    @ModelAttribute("categories")
    public List<CategoryResponse> getCategories() {
        List<CategoryResponse> categories = bookClient.getCategoriesByDepth(1L);
        System.out.println("✅ [카테고리 호출 성공] 조회된 카테고리 개수: " + categories.size());
        for (CategoryResponse category : categories) {
            System.out.println("카테고리명: " + category.getCategoryName());
        }
        return categories;
    }
}
