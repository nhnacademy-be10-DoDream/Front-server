package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.GradeType;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/grades")
public class AdminGradeController {
    private final UserClient userClient;

    @PutMapping("/{grade}")
    public String getUsers(@PathVariable("grade") GradeType gradeType,
                           @RequestParam("min-amount") Long minAmount,
                           RedirectAttributes redirectAttributes){

        try {
            userClient.updateGrade(gradeType, minAmount);
            redirectAttributes.addFlashAttribute("success", "등급이 성공적으로 수정되었습니다!");

        }catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "등급 수정중 오류 발생");
        }

        return "redirect:/admin/users";
    }
}
