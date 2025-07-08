package shop.dodream.front.dto;

public record BookListResponse(
        Long bookId,
        String title,
        String author,
        String isbn,
        Long regularPrice,
        Long salePrice,
        String bookUrl
){}