package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pl.sda.treasury.mapper.ChildMapper;
import pl.sda.treasury.mapper.UserMapper;
import pl.sda.treasury.service.ChildService;
import pl.sda.treasury.service.SchoolClassService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/child")
public class ChildController {
    private final ChildService childService;
    private final SchoolClassService schoolClassService;

    @GetMapping()
    public String getChildrenList(ModelMap model) {
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


    @GetMapping("/{id}")
    public String showUpdateForm (@PathVariable("id") long id, ModelMap model) {
        model.addAttribute("child", childService.find(id));
        model.addAttribute("schoolClassList", schoolClassService.findAll());
        return "update-child";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("child") UpdateChildForm form) {
        childService.create(ChildMapper.toEntity(form));
        return "redirect:/mvc/child";
    }

    @PostMapping("/delete/{id}")
    public String delete (@PathVariable("id") long id) {
        childService.delete(id);
        return "redirect:/mvc/child";
    }
}
