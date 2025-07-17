package shop.dodream.front.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookRegisterRequest {
    private String title;

    private String description;

    private String author;

    private String publisher;

    private LocalDate publishedAt;

    private String isbn;

    private Long regularPrice;

    private Long salePrice;

    private Boolean isGiftable;

    private Long bookCount;

    private String imageUrl;
}
