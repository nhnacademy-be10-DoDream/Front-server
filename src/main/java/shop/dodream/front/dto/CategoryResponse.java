package shop.dodream.front.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class CategoryResponse {
    private Long categoryId;
    @Setter
    private String categoryName;
    @Setter
    private Long depth;
    @Setter
    private Long parentId;

    public CategoryResponse(Long categoryId, String categoryName, Long depth, Long parentId) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.depth = depth;
        this.parentId = parentId;
    }
}
