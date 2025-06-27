package shop.dodream.front.dto;

import lombok.Data;

@Data
public class CreateAccountRequest {
    private String userId;
    private String password; // 비밀번호는 암호화된 형태로 저장되어야 합니다.
    private String email;
    private String name;
    private String phone;
    private String birthDate; // "YYYY-MM-DD" 형식으로 입력
}
