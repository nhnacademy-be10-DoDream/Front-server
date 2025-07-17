package shop.dodream.front.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import shop.dodream.front.client.UserClient;
import shop.dodream.front.dto.Grade;
import shop.dodream.front.dto.UserSearchFilter;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserClient userClient;

    @GetMapping
    public String getUsers(@ModelAttribute UserSearchFilter filter,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        List<Grade> gradeList = userClient.getGrade().stream()
                .sorted(Comparator.comparingInt(grade -> {
                    return switch (grade.getGradeTypeId()) {
                        case BASIC -> 0;
                        case ROYAL -> 1;
                        case GOLD -> 2;
                        case PLATINUM -> 3;
                        default -> 4;
                    };
                }))
                .toList();

        PageRequest pageable = PageRequest.of(page, size);


        model.addAttribute("grades", gradeList);
        model.addAttribute("userSimples", userClient.getUsers(filter, pageable));
        model.addAttribute("filter", filter);
        return "admin/users/user-list";
    }

    @GetMapping("/{user-id}")
    public String getUser(@PathVariable("user-id") String userId,
                          Model model) {

        model.addAttribute("userDetail", userClient.getUser(userId));
        return "admin/users/detail";
    }


}
