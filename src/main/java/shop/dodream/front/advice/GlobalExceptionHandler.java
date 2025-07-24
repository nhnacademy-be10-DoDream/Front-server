package shop.dodream.front.advice;

import feign.FeignException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, MaxUploadSizeExceededException.class})
    public String handleValidationAndUploadExceptions(Exception e, Model model) {
        if (e instanceof MethodArgumentNotValidException notValidException) {
            BindingResult bindingResult = notValidException.getBindingResult();

            List<String> errorMessages = bindingResult.getFieldErrors().stream()
                    .map(error -> String.format("[%s] : %s", error.getField(), error.getDefaultMessage()))
                    .toList();

            model.addAttribute("errorMessages", errorMessages);
        } else if (e instanceof MaxUploadSizeExceededException) {
            model.addAttribute("errorMessages", List.of("파일 크기가 너무 큽니다. 최대 허용 용량을 확인해주세요. (5MB)"));
        }

        return "error/400";
    }


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
