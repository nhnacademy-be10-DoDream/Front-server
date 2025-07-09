package shop.dodream.front.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class BookDetailDto {
    private Long bookId;
    private String title;
    private String author;
    private String description;
    private String publisher;
    private String isbn;
    private LocalDate publishedAt;
    private Long salePrice;
    private Long regularPrice;
    private List<String> bookUrls;
    private Long discountRate;

    // 관리자 조회 필드임 추가
    private String status;
    private ZonedDateTime createdAt;
    private Long viewCount;
    private Long bookCount;


}
