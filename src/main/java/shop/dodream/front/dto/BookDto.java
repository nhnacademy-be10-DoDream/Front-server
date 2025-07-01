package shop.dodream.front.dto;

import lombok.Data;

@Data
public class BookDto {
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private int regularPrice;
    private int salePrice;
    private String bookUrl; // 책 상세 페이지 URL
}
