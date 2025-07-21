package shop.dodream.front.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> {
    private List<T> content;
    private PageableInfo pageable;
    private boolean last;
    private Long totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;
    private boolean first;
    private List<Object> sort;
    private Long numberOfElements;
    private boolean empty;

    @Getter
    @Setter
    public static class PageableInfo {
        private int pageNumber;
        private int pageSize;
        private List<Object> sort;
        private long offset;
        private boolean paged;
        private boolean unpaged;
    }


}
