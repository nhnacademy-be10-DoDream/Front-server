package shop.dodream.front.dto;


import lombok.Data;

@Data
public class ReviewSummaryResponse {

    private Double ratingAvg;
    private Long reviewCount;

    public ReviewSummaryResponse(Double ratingAvg, Long reviewCount){
        this.ratingAvg = ratingAvg;
        this.reviewCount = reviewCount;
    }
}
