package pl.sda.treasury.mapper;

import org.springframework.stereotype.Component;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.mvc.CreateSchoolClassForm;
import pl.sda.treasury.mvc.CreateUserForm;

@Component
public class SchoolClassMapper {

    public static SchoolClass toEntity(CreateSchoolClassForm form) {
        return SchoolClass.builder()
//                .name(form.getName())
                .name(form.getName())
                .user(form.getUser())
                .build();
    }
}
