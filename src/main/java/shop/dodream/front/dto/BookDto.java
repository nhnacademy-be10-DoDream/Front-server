package shop.dodream.front.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class BookDto {
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private Long regularPrice;
    private Long salePrice;
    private String bookUrl; // 책 상세 페이지 URL
    private ZonedDateTime createdAt; // 책 등록일
    private String status; // 책상태
}
