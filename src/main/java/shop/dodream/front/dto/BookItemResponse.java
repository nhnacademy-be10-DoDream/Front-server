package shop.dodream.front.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemResponse {
    private Long bookId;
    private String title;
    private String description;
    private String author;
    private String publisher;
    private String isbn;
    private String publishedAt;
    private Long regularPrice;
    private Long salePrice;
    private String imageUrl;
    private List<Long> categoryIds;
}
