package pl.sda.treasury.mapper;

import org.springframework.stereotype.Component;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.mvc.CreateChildForm;
import pl.sda.treasury.mvc.UpdateChildForm;

@Component
public class ChildMapper {

    public static Child toEntity(CreateChildForm form) {
        return Child.builder()
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .schoolClass(form.getSchoolClass())
                .isActive(form.getIsActive())
                .build();
    }

    public static Child toEntity(UpdateChildForm form) {
        return Child.builder()
                .id(form.getId())
                .firstName(form.getFirstName())
                .lastName(form.getLastName())
                .schoolClass(form.getSchoolClass())
                .isActive(form.getIsActive())
                .build();
    }
}
