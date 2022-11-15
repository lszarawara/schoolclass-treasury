package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.mapper.ChildMapper;
import pl.sda.treasury.mapper.UserMapper;
import pl.sda.treasury.service.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static java.awt.SystemColor.text;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/child")
public class ChildController {
    private final ChildService childService;
    private final SchoolClassService schoolClassService;
    private final TransactionService transactionService;
    private final UserService userService;
    private final EmailServiceImpl emailService;


    @GetMapping()
    public String getChildrenList(ModelMap model) {
        model.addAttribute("children", childService.findAll());
        return "children";
    }

    @Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
    @GetMapping("/add")
    public String showCreateForm(@RequestParam("schoolClassId") Long schoolClassId, ModelMap model) {
        CreateChildForm form = new CreateChildForm();
        form.setSchoolClass(schoolClassService.find(schoolClassId));
        model.addAttribute("child", form);

        // todo: Do decyzji czy zostawiamy dla Admina
        //        model.addAttribute("schoolClassList", schoolClassService.findAll());

        return "create-child";
    }

    @Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
    @PostMapping("/add")
    public String create(@ModelAttribute("child") CreateChildForm form) {
        childService.create(ChildMapper.toEntity(form));
        return "redirect:/mvc/class/" + form.getSchoolClass().getId();
    }
    @Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
    @GetMapping("/{id}/checkparent")
    public String checkParentsEmail(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("parent", new CreateUserForm());
        model.addAttribute("childId", String.valueOf(id));
        return "precreate-parent";
    }

    @Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
    @PostMapping("/{id}/checkparent")
    public String showForm(@ModelAttribute("parent") @Valid CreateUserForm form, BindingResult errors, @PathVariable("id") Long id, ModelMap model) { //Errors errors

        if (errors.hasFieldErrors("email")) {
            model.addAttribute("parent", form);
            model.addAttribute("childId", String.valueOf(id));

//            if(errors.hasErrors() )
//            {
//                BeanPropertyBindingResult result2 = new BeanPropertyBindingResult( form, theBindingResult.getObjectName() );
//                for( ObjectError error : theBindingResult.getGlobalErrors() )
//                {
//                    result2.addError( error );
//                }
//                for( FieldError error : theBindingResult.getFieldErrors() )
//                {
//                    result2.addError( new FieldError( error.getObjectName(), error.getField(), null, error.isBindingFailure(), error.getCodes(), error.getArguments(), error.getDefaultMessage() ) );
//                }
//                model.addAllAttributes( result2.getModel() );
//                return "mng-dir-add";
//            }


            return "precreate-parent";
        }
        CreateUserForm form2 = new CreateUserForm();
        form2.setEmail(form.getEmail());

        try {
            model.addAttribute("user", userService.findByEmail(form2.getEmail()));
            model.addAttribute("existingUser", "Y");
            if (userService.findByEmail(form2.getEmail()).getChildren().contains(childService.find(id))) {
                model.addAttribute("parentAlreadyAdded", true);
                model.addAttribute("schoolClass", childService.find(id).getSchoolClass().getId());}
            } catch (RuntimeException e){
            form2.setRole(String.valueOf(User.Role.ROLE_USER));
            form2.setIsEnabled(false);
            model.addAttribute("parent", form2);
            model.addAttribute("existingUser", "N");
        } finally {
            model.addAttribute("childId", id);
            return "create-parent";
        }
    }
    @Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
    @PostMapping("/parent/{id}")
    public String createParent(@ModelAttribute("parent") @Valid CreateUserForm formC, BindingResult errors,
                               @ModelAttribute("user") UpdateUserForm formU,
                               @ModelAttribute("existingUser") String userExists,
                               @PathVariable("id") Long id, ModelMap model) {
        if (errors.hasErrors()) {
            model.addAttribute("parent", formC);
            model.addAttribute("user", formU);
            model.addAttribute("existingUser", userExists);
            model.addAttribute("schoolClass", childService.find(id).getSchoolClass().getId());
            model.addAttribute("childId", String.valueOf(id));
            return "create-parent";
        }

        if(userExists.equals("Y")) {
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
                prepareEmailMessage(null, formU, id, "AddChild" );
            }
        } else{
            List<Child> newListOfChildren = new ArrayList<>();
            Child newChild = childService.find(id);
            newListOfChildren.add(newChild);
            formC.setChildren(newListOfChildren);
            userService.update(UserMapper.toEntity(formC));
            prepareEmailMessage(formC, null, id, "Welcome" );

        }
        return "redirect:/mvc/class/" + childService.find(id).getSchoolClass().getId();
    }

    private void prepareEmailMessage(CreateUserForm formC, UpdateUserForm formU,Long childId, String messageType) {
        String to = null;
        String subject = null;
        String text = null;

        switch (messageType) {
            case "Welcome":
                to = formC.getEmail();
                subject = "Skarbnik Klasowy - utworzenie konta rodzica";
                text = "Dzień Dobry!\n\n" + "Utworzono dla Ciebie konto rodzica dla dziecka "
                        + childService.find(childId).getFirstName() + " " + childService.find(childId).getLastName() + " w klasie " + childService.find(childId).getSchoolClass().getName() + ".\n\n"
                        + "Zaloguje się w serwisie skarbnikklasowy.pl na Twoje konto i zmień hasło.\n"
                        + "Twój login: " + formC.getLogin() + "\n"
                        + "Twoje tymczasowe hasło: " + formC.getPassword()
                        + "\nPozdrawiamy\nSkarbnik Klasowy";
                break;
            case "AddChild":
                to = formU.getEmail();
                subject = "Skarbnik Klasowy - dodanie dziecka do konta rodzica";
                text = "Dzień Dobry!\n\n" + "Do Twojego konta dodano dziecko: "
                        + childService.find(childId).getFirstName() + " " + childService.find(childId).getLastName() + " w klasie " + childService.find(childId).getSchoolClass().getName() + ".\n"
                        + "\nPozdrawiamy\nSkarbnik Klasowy";
                break;
        }
        emailService.sendSimpleMessage(to, subject, text);
    }


    @Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
    @GetMapping("/{id}")
    public String showUpdateForm (@PathVariable("id") long id, ModelMap model) {
        model.addAttribute("child", childService.find(id));
        model.addAttribute("schoolClassList", schoolClassService.findAll());
        return "update-child";
    }

    @Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
    @PostMapping("/update")
    public String update(@ModelAttribute("child") UpdateChildForm form) {
        childService.create(ChildMapper.toEntity(form));
        return "redirect:/mvc/class/" + childService.find(form.getId()).getSchoolClass().getId();
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping("/delete/{id}")
    public String delete (@PathVariable("id") long id) {
        childService.delete(id);
        return "redirect:/mvc/child";
    }

    @PreAuthorize("@securityService.isParent(#id) || @securityService.isTreasurer(#id) || hasRole('ROLE_ADMIN')")
    @GetMapping("/balance/{id}")
    public String getTransactionsListByChildId(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("transactions", transactionService.findAllbyChild(id));
        model.addAttribute("child", childService.find(id));
        model.addAttribute("sumPayment", transactionService.getPaymentSumForChildren(id));
        model.addAttribute("sumDebit", transactionService.getDebitSumForChildren(id));
        return "transactionsByChild";
    }

}
