package pl.sda.treasury.rest.mapper;

import org.springframework.stereotype.Component;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.mvc.CreateUserForm;

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
//                .role(User.Role.valueOf(form.getRole()))
                .build();
    }
}
