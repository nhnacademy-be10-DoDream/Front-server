//package shop.dodream.front.config;
//
//import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.naming.spi.ObjectFactory;
//import java.beans.Encoder;
//
//@Configuration
//public class FeginMultipartSupportConfig {
//
//    @Bean
//    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> converters) {
//        return new SpringFormEncoder(new SpringEncoder(converters));
//    }
//}
