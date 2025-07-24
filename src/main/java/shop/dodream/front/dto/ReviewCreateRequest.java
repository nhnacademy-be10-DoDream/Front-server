package shop.dodream.front.dto;


import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewCreateRequest {
    @NotNull
    @Min(0)
    @Max(5)
    private Short rating;
    @NotBlank
    @Size(min = 5)
    private String content;
}
