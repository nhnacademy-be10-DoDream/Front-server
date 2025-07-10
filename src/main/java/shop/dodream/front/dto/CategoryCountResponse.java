package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CategoryCountResponse {
    private Long categoryId;
    private String categoryName;
    private Long parentId;
    private int count; // 이게 핵심!
}
