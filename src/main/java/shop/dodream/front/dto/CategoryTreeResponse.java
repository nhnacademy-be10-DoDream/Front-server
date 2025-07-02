package shop.dodream.front.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CategoryTreeResponse {

    private Long categoryId;
    private String categoryName;
    private Long depth;
    private Long parentId;
    private List<CategoryTreeResponse> children = new ArrayList<>();


    // 직접 값 주입 생성자 (Mock 데이터용)
    public CategoryTreeResponse(Long categoryId, String categoryName, Long depth, Long parentId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.depth = depth;
        this.parentId = parentId;
    }
}
