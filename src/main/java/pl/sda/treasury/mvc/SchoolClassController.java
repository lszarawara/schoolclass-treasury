package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pl.sda.treasury.mapper.SchoolClassMapper;
import pl.sda.treasury.service.ChildService;
import pl.sda.treasury.service.SchoolClassService;
import pl.sda.treasury.service.TransactionService;
import pl.sda.treasury.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/class")
public class SchoolClassController {
    private final SchoolClassService schoolClassService;
    private final UserService userService;
    private final ChildService childService;
    private final TransactionService transactionService;

    //    @Secured("ROLE_ADMIN")
//    @PostMapping("/preadd")
    @GetMapping("/add")
    public String showCreateForm(@RequestParam("userId") Long userId, ModelMap model) {
        CreateSchoolClassForm form = new CreateSchoolClassForm();
        form.setUser(userService.find(userId));
        model.addAttribute("schoolClass", form);
        return "create-class";
    }

    //    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public String create(@ModelAttribute("user") CreateSchoolClassForm form) {
        schoolClassService.create(SchoolClassMapper.toEntity(form));
        schoolClassService.findLastId();
        childService.createTechnicalAccountForSchoolClass(schoolClassService.findLastId());
//        return "redirect:/mvc/class/add";
        return "redirect:/mvc/user/" + form.getUser().getId();
    }

    @GetMapping("/{id}")
    public String showDetails (@PathVariable("id") long id, ModelMap model) {
        model.addAttribute("schoolClass", schoolClassService.find(id));
        model.addAttribute("childrenBalances", transactionService.getListOfBalancesForSchoolClass(schoolClassService.find(id)));
        model.addAttribute("childrenPaymentBalances", transactionService.getListOfPaymentBalancesForSchoolClass(schoolClassService.find(id)));
        model.addAttribute("schoolClassBalance", transactionService.getBalanceForSchoolClass(schoolClassService.find(id)));
        model.addAttribute("schoolClassPaymentBalance", transactionService.getPaymentBalanceForSchoolClass(schoolClassService.find(id)));
        return "class";
    }

    @GetMapping()
         public String getSchoolClassesList(ModelMap model) {
        model.addAttribute("schoolClasses", schoolClassService.findAll());
        return "classes";
    }

    @GetMapping("/delete/{id}")
    public String delete (@PathVariable("id") long id) {
        schoolClassService.delete(id);
        return "redirect:/mvc/class";
    }
}
