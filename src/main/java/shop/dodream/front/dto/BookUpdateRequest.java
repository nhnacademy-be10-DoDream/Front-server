package shop.dodream.front.dto;

import lombok.Data;


@Data
public class BookUpdateRequest {


    private String title;
    private String description;
    private String author;
    private String publisher;
    private String publishedAt;

    private Long regularPrice; // 정가

    private Long salePrice; // 할인가

    private Boolean isGiftable;

    private Long bookCount;

}
