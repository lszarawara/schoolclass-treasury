package pl.sda.treasury.mvc;

import lombok.Data;

@Data
public class CreateUserForm {
    private String firstName;
    private String lastName;
    private String email;
    private String login;
    private String password;
    private String role;
}
