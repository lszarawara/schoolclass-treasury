package pl.sda.treasury.mvc;

import lombok.Data;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.User;

@Data
public class CreateSchoolClassForm {
    private String name;
    private User user;
//    private SchoolClass schoolClass;

}
