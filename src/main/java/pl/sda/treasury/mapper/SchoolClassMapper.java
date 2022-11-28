package pl.sda.treasury.mapper;

import org.springframework.stereotype.Component;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.mvc.CreateSchoolClassForm;

@Component
public class SchoolClassMapper {

    public static SchoolClass toEntity(CreateSchoolClassForm form) {
        return SchoolClass.builder()
                .name(form.getName())
                .user(form.getUser())
                .build();
    }
}
