package shop.dodream.front.dto;


import lombok.Data;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Data
public class ReviewResponse {

    long reviewId;
    short rating;
    String content;
    ZonedDateTime createdAt;
    long bookId;
    List<String> images;
    String userId;


    public ReviewResponse(String userId, short rating, String content) {
        this.userId = userId;
        this.rating = rating;
        this.content = content;
    }
}
