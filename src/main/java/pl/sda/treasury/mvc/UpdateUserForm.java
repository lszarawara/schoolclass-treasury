package pl.sda.treasury.mvc;

import lombok.Data;
import lombok.EqualsAndHashCode;
import pl.sda.treasury.entity.SchoolClass;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateUserForm extends CreateUserForm{
    private Long id;
    private List<SchoolClass> schoolClasses;
}
