package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pl.sda.treasury.rest.mapper.UserMapper;
import pl.sda.treasury.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/user")
public class UserController {
    private final UserService userService;

    //    @Secured("ROLE_ADMIN")
    @GetMapping()
    public String list (ModelMap model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    //    @Secured("ROLE_ADMIN")
    @GetMapping("/add")
    public String showCreateForm(ModelMap model) {
        model.addAttribute("user", new CreateUserForm());
        return "create-user";
    }

    //    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public String create(@ModelAttribute("user") CreateUserForm form) {
        userService.create(UserMapper.toEntity(form));
        return "redirect:/mvc/user/add";
    }

    @GetMapping("delete/{id}")
    public String delete (@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/mvc/user";
    }
}
