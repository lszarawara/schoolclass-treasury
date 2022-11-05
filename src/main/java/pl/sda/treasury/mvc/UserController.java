package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.mapper.UserMapper;
import pl.sda.treasury.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/user")
public class UserController {
    private final UserService userService;

    //    @Secured("ROLE_ADMIN")
    @GetMapping()
    public String getUsersList(ModelMap model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }




    //    @Secured("ROLE_ADMIN")
    @GetMapping("/add")
    public String showCreateForm(ModelMap model) {
        CreateUserForm createUserForm = new CreateUserForm();
        createUserForm.setRole(String.valueOf(User.Role.ROLE_SUPERUSER));
        model.addAttribute("user", createUserForm);
        return "create-user";
    }

    //    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public String create(@ModelAttribute("user") CreateUserForm form) {
        userService.create(UserMapper.toEntity(form));
        return "redirect:/mvc/user/" + userService.findLastId().getId();
    }




    @GetMapping("/{id}")
    public String showDetails (@PathVariable("id") long id, ModelMap model) {
        model.addAttribute("user", userService.find(id));
        return "user";
    }
    @GetMapping("/{id}/update")
    public String showUpdateForm (@PathVariable("id") long id, ModelMap model) {
        model.addAttribute("user", userService.find(id));
        return "update-user";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("user") UpdateUserForm form) {
        userService.create(UserMapper.toEntity(form));
        return "redirect:/mvc/user";
    }

    @GetMapping("/delete/{id}")
    public String delete (@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/mvc/user";
    }
}
