package shop.dodream.front.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "bookClient", url = "dodream.shop:10325")
public interface bookClient {
//    @GetMapping("/books")
//    public String getBooks() {
//        return "";
//    }
}
