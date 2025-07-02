package shop.dodream.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import shop.dodream.front.dto.CategoryTreeResponse;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class TestCategoryController {

    @GetMapping("/test-category")
    public String testCategory(Model model) {
        // depth 2 카테고리들 (예시 10개)
        CategoryTreeResponse child1 = new CategoryTreeResponse(11L, "소설/시", 2L, 1L);
        CategoryTreeResponse child2 = new CategoryTreeResponse(12L, "건강/취미", 2L, 1L);
        CategoryTreeResponse child3 = new CategoryTreeResponse(13L, "자기계발", 2L, 1L);
        CategoryTreeResponse child4 = new CategoryTreeResponse(14L, "역사", 2L, 2L);
        CategoryTreeResponse child5 = new CategoryTreeResponse(15L, "과학", 2L, 2L);
        CategoryTreeResponse child6 = new CategoryTreeResponse(16L, "IT/컴퓨터", 2L, 2L);
        CategoryTreeResponse child7 = new CategoryTreeResponse(17L, "영어 원서", 2L, 3L);
        CategoryTreeResponse child8 = new CategoryTreeResponse(18L, "일본 원서", 2L, 3L);
        CategoryTreeResponse child9 = new CategoryTreeResponse(19L, "유아 도서", 2L, 4L);
        CategoryTreeResponse child10 = new CategoryTreeResponse(20L, "청소년 도서", 2L, 4L);

        // depth 1 카테고리들 (예시 5개), 각자 자식 카테고리 할당
        CategoryTreeResponse category1 = new CategoryTreeResponse(1L, "국내도서", 1L, null);
        category1.setChildren(List.of(child1, child2, child3));

        CategoryTreeResponse category2 = new CategoryTreeResponse(2L, "외국도서", 1L, null);
        category2.setChildren(List.of(child4, child5, child6));

        CategoryTreeResponse category3 = new CategoryTreeResponse(3L, "원서", 1L, null);
        category3.setChildren(List.of(child7, child8));

        CategoryTreeResponse category4 = new CategoryTreeResponse(4L, "아동도서", 1L, null);
        category4.setChildren(List.of(child9, child10));

        CategoryTreeResponse category5 = new CategoryTreeResponse(5L, "음반/영상", 1L, null);
        // 자식 카테고리 없으면 빈 리스트 유지

        List<CategoryTreeResponse> categories = List.of(category1, category2, category3, category4, category5);

        model.addAttribute("categories", categories);
        return "home";  // 뷰 이름
    }
}
