package shop.dodream.front.dto;

public class CartItem {
	private Long id;
	private String name;
	private String thumbnailUrl;
	private int originalPrice;
	private int discountedPrice;
	private int quantity;
	private int wrappingPrice = 0; // optional, can be 0 if no wrapping


	// 생성자
	public CartItem(Long id, String name, String thumbnailUrl, int originalPrice, int discountedPrice, int quantity) {
		this.id = id;
		this.name = name;
		this.thumbnailUrl = thumbnailUrl;
		this.originalPrice = originalPrice;
		this.discountedPrice = discountedPrice;
		this.quantity = quantity;
	}
	
	// Getter, Setter (롬복 쓰면 @Getter/@Setter 사용 가능)
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	
	public int getOriginalPrice() {
		return originalPrice;
	}
	
	public int getDiscountedPrice() {
		return discountedPrice;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public int getTotalPrice() {
		return discountedPrice * quantity + wrappingPrice; // 총 금액 계산
	}
}