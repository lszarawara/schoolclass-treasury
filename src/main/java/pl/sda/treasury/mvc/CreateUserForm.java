package pl.sda.treasury.mvc;

import lombok.Data;
import pl.sda.treasury.entity.Child;
import java.util.List;

@Data
public class CreateUserForm {
    private String firstName;
    private String lastName;
    private String email;
    private String login;
    private String password;
    private String role;
    private Boolean isEnabled;
    private List<Child> children;
}
