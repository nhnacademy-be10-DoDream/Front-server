package shop.dodream.front.dto;

import lombok.Data;

@Data
public class CreateAccountResponse {
    private String userId; // 사용자 ID
    private String role; // 사용자 역할 (예: USER, ADMIN)
    private String status; // 사용자 상태 (예: ACTIVE, INACTIVE)
}
