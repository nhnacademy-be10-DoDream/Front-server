package shop.dodream.front.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BookTagInfo {
    private Long tagId;
    private TagResponse tag;
    private List<BookDto> books;
}
