package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sda.treasury.rest.mapper.SchoolClassMapper;
import pl.sda.treasury.service.SchoolClassService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/class")
public class SchoolClassController {
    private final SchoolClassService schoolClassService;

    //    @Secured("ROLE_ADMIN")
    @GetMapping("/add")
    public String showCreateForm(ModelMap model) {
        model.addAttribute("schoolClass", new CreateSchoolClassForm());
        return "create-class";
    }

    //    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public String create(@ModelAttribute("user") CreateSchoolClassForm form) {
        schoolClassService.create(SchoolClassMapper.toEntity(form));
        return "redirect:/mvc/class/add";
    }

    @GetMapping()
         public String getSchoolClassesList(ModelMap model) {
        model.addAttribute("schoolClasses", schoolClassService.findAll());
        return "classes";
    }
}
