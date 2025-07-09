package shop.dodream.front.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

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
    private Long salePrice;
    private Date publishedAt;
    private Long viewCount;
    private Float ratingAvg;
    private Long reviewCount;
}
