package shop.dodream.front.config;

import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignMultipartSupportConfig {

    @Bean
    JsonFormWriter jsonFormWriter(){
        return new JsonFormWriter();
    }
}