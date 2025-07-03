package shop.dodream.front.dto;

import lombok.Getter;

@Getter
public class BookListResponseRecord {
    private Long bookId;
    private String title;
    private String author;
    private String isbn;
    private Long regularPrice;
    private Long salePrice;
    private String bookUrl;

    @Override
    public String toString() {
        return "BookListResponseRecord{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", regularPrice=" + regularPrice +
                ", salePrice=" + salePrice +
                ", bookUrl='" + bookUrl + '\'' +
                '}';
    }
}
