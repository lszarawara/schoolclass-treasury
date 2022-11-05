package pl.sda.treasury.mvc;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateChildForm extends CreateChildForm{
    private Long id;
}
