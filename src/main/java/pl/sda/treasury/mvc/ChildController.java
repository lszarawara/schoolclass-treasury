package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.mapper.ChildMapper;
import pl.sda.treasury.mapper.UserMapper;
import pl.sda.treasury.service.ChildService;
import pl.sda.treasury.service.SchoolClassService;
import pl.sda.treasury.service.TransactionService;
import pl.sda.treasury.service.UserService;

import javax.lang.model.type.ExecutableType;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/child")
public class ChildController {
    private final ChildService childService;
    private final SchoolClassService schoolClassService;
    private final TransactionService transactionService;
    private final UserService userService;


    @GetMapping()
    public String getChildrenList(ModelMap model) {
        model.addAttribute("children", childService.findAll());
        return "children";
    }

    //    @Secured("ROLE_ADMIN")
    //    @Secured("ROLE_SUPERUSER")
    @GetMapping("/add")
    public String showCreateForm(@RequestParam("schoolClassId") Long schoolClassId, ModelMap model) {
//        List<SchoolClass> schoolClassList = schoolClassService.findAll();
        CreateChildForm form = new CreateChildForm();
        form.setSchoolClass(schoolClassService.find(schoolClassId));
        model.addAttribute("child", form);

        // todo: Do decyzji czy zostawiamy dla Admina
        //        model.addAttribute("schoolClassList", schoolClassService.findAll());

        return "create-child";
    }

    //    @Secured("ROLE_ADMIN")
    //    @Secured("ROLE_SUPERUSER")
    @PostMapping("/add")
    public String create(@ModelAttribute("child") CreateChildForm form) {
        childService.create(ChildMapper.toEntity(form));
        return "redirect:/mvc/class/" + form.getSchoolClass().getId();
    }

    @GetMapping("/checkparent/{id}")
    public String checkParentsEmail(@PathVariable("id") long id, ModelMap model) {
        model.addAttribute("parent", new CreateUserForm());
        model.addAttribute("childId", id);
        return "precreate-parent";
    }

    @GetMapping("/parent")
    public String showForm(@ModelAttribute("parent") CreateUserForm form, @ModelAttribute("childId") Long id, ModelMap model) {
        try {
            model.addAttribute("user", userService.findByEmail(form.getEmail()));
            model.addAttribute("existingUser", true);
        } catch (RuntimeException e){
            form.setRole(String.valueOf(User.Role.ROLE_USER));
            model.addAttribute("user", form);
            model.addAttribute("existingUser", false);
        } finally {
            model.addAttribute("childId", id);
            return "create-parent";
        }
    }
    @PostMapping("/parent")
    public String createParent(@ModelAttribute("parent") CreateUserForm form, @ModelAttribute("childId") Long id) {

        List<Child> newListOfChildren = form.getChildren();
        Child newChild = childService.find(id);
        newListOfChildren.add(newChild);
        form.setChildren(newListOfChildren);
        userService.create(UserMapper.toEntity(form));

        return "create-parent";
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

    @GetMapping("/balance/{id}")
    public String getTransactionsListByChildId(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("transactions", transactionService.findAllbyChild(id));
        model.addAttribute("child", childService.find(id));
        model.addAttribute("sumPayment", transactionService.getPaymentSumForChildren(id));
        model.addAttribute("sumDebit", transactionService.getDebitSumForChildren(id));

        return "transactionsByChild";
    }
}
