package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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

import java.security.Principal;
import java.util.ArrayList;
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

    @GetMapping("/{id}/checkparent")
    public String checkParentsEmail(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("parent", new CreateUserForm());
//        String idi = String.valueOf(id);
        model.addAttribute("childId", String.valueOf(id));
        return "precreate-parent";
    }


    @PostMapping("/{id}/checkparent")
//    @PostMapping("/checkparent")
    public String showForm(@ModelAttribute("parent") CreateUserForm form, @PathVariable("id") Long id, ModelMap model) {
//    public String showForm(@ModelAttribute("parent") CreateUserForm form, @ModelAttribute("childId") String id, ModelMap model) {
//        tu nie działa Lang w Model Attribute

        try {
            model.addAttribute("user", userService.findByEmail(form.getEmail()));
            model.addAttribute("existingUser", "Y");
        } catch (RuntimeException e){
            //dodany wiersz kolejny:
            form.setRole(String.valueOf(User.Role.ROLE_USER));
            model.addAttribute("parent", form);
            model.addAttribute("existingUser", "N");
        } finally {
            model.addAttribute("childId", id);
            return "create-parent";
        }
    }
    @PostMapping("/parent/{id}")
    public String createParent(@ModelAttribute("parent") CreateUserForm formC,
                               @ModelAttribute("user") UpdateUserForm formU,
                               @ModelAttribute("existingUser") String userExists,
                               @PathVariable("id") Long id) {
        //todo: powtórzenia
        if(formU.getId().describeConstable().isPresent()) {
//        if(userExists.equals("Y")) {
            try {
                List<Child> newListOfChildren = formU.getChildren();
                Child newChild = childService.find(id);
                newListOfChildren.add(newChild);
                formU.setChildren(newListOfChildren);
            } catch (NullPointerException e) {
                List<Child> newListOfChildren = new ArrayList<>();
                Child newChild = childService.find(id);
                newListOfChildren.add(newChild);
                formU.setChildren(newListOfChildren);
            } finally {
                userService.update(UserMapper.toEntity(formU));
            }
        } else {
            List<Child> newListOfChildren = new ArrayList<>();
            Child newChild = childService.find(id);
            newListOfChildren.add(newChild);
            formC.setChildren(newListOfChildren);
            userService.update(UserMapper.toEntity(formC));
         }
            return "redirect:/mvc/class/" + childService.find(id).getSchoolClass().getId();
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @PreAuthorize("@securityService.isParent(#id)")
//    @PreAuthorize("isParent(#id)")
    @GetMapping("/balance/{id}")
    public String getTransactionsListByChildId(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("transactions", transactionService.findAllbyChild(id));
        model.addAttribute("child", childService.find(id));
        model.addAttribute("sumPayment", transactionService.getPaymentSumForChildren(id));
        model.addAttribute("sumDebit", transactionService.getDebitSumForChildren(id));

        return "transactionsByChild";
    }

//    public boolean isParent(Principal principal, Long ChildId) {
//        return userService.findByLogin(principal.getName()).getChildren().contains(childService.find(ChildId));
//    }

}
