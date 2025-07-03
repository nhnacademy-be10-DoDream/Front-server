package shop.dodream.front.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CategoryTreeResponse extends CategoryResponse {
    private List<CategoryTreeResponse> children = new ArrayList<>();

    public CategoryTreeResponse(Long categoryId, String categoryName, Long depth, Long parentId) {
        super(categoryId, categoryName, depth, parentId);
    }
}
