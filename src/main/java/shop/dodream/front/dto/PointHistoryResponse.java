package shop.dodream.front.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PointHistoryResponse {
    private long pointId;
    private long amount;
    private long balance;
    private String pointType;
    private String description;
    private ZonedDateTime createdAt;
}
