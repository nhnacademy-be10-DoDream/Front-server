package shop.dodream.front.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class SearchBookResponse {
    private PageResponse<BookItemResponse> books;
    private Map<Long, Integer> categoryCountMap;
}
