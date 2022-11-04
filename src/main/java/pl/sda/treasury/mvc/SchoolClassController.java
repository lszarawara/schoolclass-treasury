package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import pl.sda.treasury.mapper.SchoolClassMapper;
import pl.sda.treasury.service.*;


@Controller
@SessionScope
@RequiredArgsConstructor
@RequestMapping("/mvc/class")
public class SchoolClassController {
    private final SchoolClassService schoolClassService;
    private final UserService userService;
    private final ChildService childService;
    private final TransactionService transactionService;

    private final CurrentSchoolClass currentSchoolClass;

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
        currentSchoolClass.setId(id);
        model.addAttribute("schoolClass", schoolClassService.find(id));
        model.addAttribute("children", childService.findAllNonTechnicalBySchoolClass(schoolClassService.find(id)));
        model.addAttribute("childrenBalances", transactionService.getListOfNonTechnicalBalancesForSchoolClass(schoolClassService.find(id)));
        model.addAttribute("childrenPaymentBalances", transactionService.getListOfNonTechnicalPaymentBalancesForSchoolClass(schoolClassService.find(id)));
        model.addAttribute("schoolClassBalance", transactionService.getBalanceForSchoolClass(schoolClassService.find(id)));
        model.addAttribute("schoolClassPaymentBalance", transactionService.getPaymentBalanceForSchoolClass(schoolClassService.find(id)));
        model.addAttribute("schoolClassTechnicalAccountBalance", transactionService.getBalanceForTechnicalAccountBySchoolClass(schoolClassService.find(id)));

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
