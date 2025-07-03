package shop.dodream.front.dto;

public record OrderItemResponse(
        String orderId,
        Long bookId,
        String title,
        int quantity,
        int price,
        WrappingInfo wrappingInfo
) {}
