package shop.dodream.front.dto;

import lombok.Data;

@Data
public class UserAddressDto {
    private String alias; // 주소별칭
    private String zipcode; // 우편번호
    private String roadAddress; // 도로명주소
    private String jibunAddress; // 지번주소
    private String extraAddress; // 참고항목
    private String detailAddress; // 상세주소
}
