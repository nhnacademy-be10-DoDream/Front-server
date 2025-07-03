package shop.dodream.front.dto;

public record UserAddressResponse(
        long addressId,
        String alias,
        String roadAddress,
        String jibunAddress,
        String extraAddress,
        String detailAddress,
        String zipcode
) {}