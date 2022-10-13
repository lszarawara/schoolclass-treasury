package pl.sda.treasury.mvc;

import lombok.Data;
import pl.sda.treasury.entity.SchoolClass;

@Data
public class CreateSchoolClassForm {
    private String name;
    private SchoolClass schoolClass;
}
