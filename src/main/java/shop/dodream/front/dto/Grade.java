package shop.dodream.front.dto;

import lombok.Data;

@Data
public class Grade {
    private GradeType gradeTypeId;
    private Long minAmount;
}