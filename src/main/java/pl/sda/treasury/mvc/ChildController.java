package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.repository.ChildRepository;
import pl.sda.treasury.repository.SchoolClassRepository;
import pl.sda.treasury.rest.mapper.ChildMapper;
import pl.sda.treasury.rest.mapper.UserMapper;
import pl.sda.treasury.service.ChildService;
import pl.sda.treasury.service.SchoolClassService;
import pl.sda.treasury.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/child")
public class ChildController {
    private final ChildService childService;
    private final SchoolClassService schoolClassService;

    @GetMapping()
    public String list (ModelMap model) {
        model.addAttribute("children", childService.findAll());
        return "children";
    }

    //    @Secured("ROLE_ADMIN")
    @GetMapping("/add")
    public String showCreateForm(ModelMap model) {
//        List<SchoolClass> schoolClassList = schoolClassService.findAll();
        model.addAttribute("child", new CreateChildForm());
        model.addAttribute("schoolClassList", schoolClassService.findAll());

        return "create-child";
    }

    //    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public String create(@ModelAttribute("child") CreateChildForm form) {
        childService.create(ChildMapper.toEntity(form));
        return "redirect:/mvc/child/add";
    }
}