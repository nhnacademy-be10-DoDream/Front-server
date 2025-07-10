package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CategoryCountResponse {
    private Long categoryId;
    @Setter
    private String categoryName;
    @Setter
    private int count;
    private List<CategoryCountResponse> children = new ArrayList<>();

    public void increment(){
        this.count++;
    }

    public CategoryCountResponse(Long categoryId, String categoryName, int count) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.count = count;
    }
}
