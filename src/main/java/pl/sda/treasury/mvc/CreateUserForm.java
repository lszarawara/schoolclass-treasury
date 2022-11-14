package pl.sda.treasury.mvc;

import lombok.Data;
import pl.sda.treasury.entity.Child;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class CreateUserForm {
    @NotEmpty(message = "Proszę podać imię")
    private String firstName;
    @NotEmpty(message = "Proszę podać nazwisko")
    private String lastName;
    @Email
    @NotEmpty(message = "Proszę podać email")
    private String email;
    @NotEmpty(message = "Proszę wprowadzić login")
    private String login;
    @NotEmpty(message = "Proszę wprowadzić hasło")
    private String password;
    private String role;
    private Boolean isEnabled;
    private List<Child> children;
}
