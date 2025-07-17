package shop.dodream.front.dto;

import lombok.Data;

import java.util.List;

@Data
public class AladdinBookSearchResult {
    private int totalResults;
    private List<BookItemResponse> items;
}
