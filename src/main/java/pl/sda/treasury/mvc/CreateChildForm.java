package pl.sda.treasury.mvc;

import lombok.Data;
import pl.sda.treasury.entity.SchoolClass;

@Data
public class CreateChildForm {
    private String firstName;
    private String lastName;
    private SchoolClass schoolClass;
    private Boolean isActive = Boolean.valueOf("true");
}
