package shop.dodream.front.advice;

import feign.FeignException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public String handleFeign(FeignException e, Model model) {
        model.addAttribute("errorMessage", "서버와 통신 중 오류가 발생했습니다.");
        return "error/500";
    }

    @ExceptionHandler(Exception.class)
    public String handleGlobal(Exception e, Model model) {
        model.addAttribute("errorMessage", "예기치 못한 오류가 발생했습니다.");
        return "error/default";
    }
}
