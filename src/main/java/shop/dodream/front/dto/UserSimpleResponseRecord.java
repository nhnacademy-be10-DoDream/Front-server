package shop.dodream.front.dto;


public record UserSimpleResponseRecord(
        String userId,
        String status,
        GradeType grade
) {}