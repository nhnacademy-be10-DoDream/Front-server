package shop.dodream.front.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import shop.dodream.front.client.BookClient;
import shop.dodream.front.dto.CategoryResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CategoryInterceptor implements HandlerInterceptor {

    private final BookClient bookClient;
    private Map<CategoryResponse, List<CategoryResponse>> cachedCategories;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (cachedCategories == null) {
            List<CategoryResponse> parentCategories = bookClient.getCategoriesByDepth(1L);
            List<CategoryResponse> childCategories = bookClient.getCategoriesByDepth(2L);

            Map<CategoryResponse, List<CategoryResponse>> categoryMap = new LinkedHashMap<>();
            for (CategoryResponse parent : parentCategories) {
                List<CategoryResponse> children = childCategories.stream()
                        .filter(child -> child.getParentId().equals(parent.getCategoryId()))
                        .toList();
                categoryMap.put(parent, children);
            }
            cachedCategories = categoryMap;
        }
        request.setAttribute("categories", cachedCategories);
        return true;
    }


}

