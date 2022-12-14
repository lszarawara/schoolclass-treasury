package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.mapper.UserMapper;
import pl.sda.treasury.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/user")
public class UserController {
    private final UserService userService;

    @Secured("ROLE_ADMIN")
    @GetMapping()
    public String getUsersList(ModelMap model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    @GetMapping("/add")
    public String showCreateForm(ModelMap model) {
        CreateUserForm createUserForm = new CreateUserForm();
            createUserForm.setRole(String.valueOf(User.Role.ROLE_SUPERUSER));
            createUserForm.setIsEnabled(true);
            model.addAttribute("user", createUserForm);
            return "create-user";
    }

    @PostMapping("/add")
    public String create(@ModelAttribute("user") @Valid CreateUserForm form, BindingResult errors, ModelMap model, HttpServletRequest request) {
        boolean shouldShowCreateUserAgain = false;
        if (userService.existsByEmail(form.getEmail())) {
            model.addAttribute("emailExists", true);
            shouldShowCreateUserAgain = true;
        }
        if (userService.existsByLogin(form.getLogin())) {
            model.addAttribute("loginExists", true);
            shouldShowCreateUserAgain = true;
        }
        if (errors.hasErrors()) {
            shouldShowCreateUserAgain = true;
        }
        if (shouldShowCreateUserAgain) {
            model.addAttribute("user", form);
            return "create-user";
        }
        User user = userService.create(UserMapper.toEntity(form));
        if (request.isUserInRole("ROLE_ADMIN")) {
            return "redirect:/mvc/user/" + user.getId();
        }
        return "redirect:/mvc/user/current";
    }


    @GetMapping("/current")
    public String showCurrentUserDetails (Principal principal, ModelMap model) {
        model.addAttribute("user", userService.findByLogin(principal.getName()));
        if (userService.findByLogin(principal.getName()).getIsEnabled()) {
            return "user";
        } else {
            return "change-password";
        }
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/{id}")
    public String showDetails (@PathVariable("id") long id, ModelMap model) {
        model.addAttribute("user", userService.find(id));
        return "user";
    }

    @PreAuthorize("@securityService.isThisUser(#id) || hasRole('ROLE_ADMIN')")
    @GetMapping("/{id}/update")
    public String showUpdateForm (@PathVariable("id") long id, ModelMap model) {
        model.addAttribute("user", userService.find(id));
        return "update-user";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("user") UpdateUserForm form) {
        userService.create(UserMapper.toEntity(form));
        return "redirect:/mvc/user/current";
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/delete/{id}")
    public String delete (@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/mvc/user";
    }
}
