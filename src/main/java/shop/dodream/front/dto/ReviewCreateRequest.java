package shop.dodream.front.dto;


import lombok.Data;

@Data
public class ReviewCreateRequest {

    private Short rating;
    private String content;

}
