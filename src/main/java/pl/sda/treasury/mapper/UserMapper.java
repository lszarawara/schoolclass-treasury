package pl.sda.treasury.mapper;

import org.springframework.stereotype.Component;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.mvc.CreateUserForm;
import pl.sda.treasury.mvc.UpdateUserForm;

@Component
public class UserMapper {

    public static User toEntity(CreateUserForm form) {
        return User.builder()
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .email(form.getEmail())
                .login(form.getLogin())
                .password(form.getPassword())
                .role(User.Role.valueOf(form.getRole()))
                .children(form.getChildren())
                .build();
    }

    public static User toEntity(UpdateUserForm form) {
        return User.builder()
                .id(form.getId())
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .email(form.getEmail())
                .login(form.getLogin())
                .password(form.getPassword())
                .role(User.Role.valueOf(form.getRole()))
                .children(form.getChildren())
                .build();
    }
}
