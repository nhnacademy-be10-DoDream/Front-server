package shop.dodream.front.advice;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalMinioPathAdvice {


    @Value("${minio.prefix}")
    private String prefix;

    @Value("${minio.book}")
    private String book;

    @Value("${minio.review}")
    private String review;


    @ModelAttribute("minioPath")
    public Map<String, String> minioPath(){
        Map<String, String> map = new HashMap<>();
        map.put("book", prefix+book);
        map.put("review", prefix+review);
        return map;
    }
}
