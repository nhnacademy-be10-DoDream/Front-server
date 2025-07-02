package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.CategoryResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
public class CategoryController {

    private final BookClient bookClient;

    @ModelAttribute("categories")
    public Map<String, List<CategoryResponse>> categories() {
        List<CategoryResponse> parentCategories = bookClient.getCategoriesByDepth(1L);

        List<CategoryResponse> childCategories = bookClient.getCategoriesByDepth(2L);

        Map<String, List<CategoryResponse>> categoryMap = new LinkedHashMap<>();

        for (CategoryResponse parent : parentCategories) {
            List<CategoryResponse> children = childCategories.stream()
                    .filter(child -> child.getParentId().equals(parent.getCategoryId()))
                    .toList();
            categoryMap.put(parent.getCategoryName(), children);
        }

        return categoryMap;
    }
}
