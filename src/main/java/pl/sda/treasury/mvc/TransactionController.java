package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pl.sda.treasury.dto.TransactionCreationDto;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.Transaction;
import pl.sda.treasury.mapper.TransactionMapper;
import pl.sda.treasury.service.ChildService;
import pl.sda.treasury.service.SchoolClassService;
import pl.sda.treasury.service.TransactionService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final ChildService childService;
    private final SchoolClassService schoolClassService;

    @GetMapping("/create")
    public String showCreateForm(ModelMap model) {
        TransactionCreationDto form = new TransactionCreationDto();

        for (int i=1; i< childService.findAll().size(); i++) {
//            Transaction transaction = new Transaction();
//            Child child = childService.findAll().get(i);
//            transaction.setChild(child);
            form.addTransaction(new Transaction());

        }
        model.addAttribute("form", form);
        return "create-transaction2";

    }



    @GetMapping("/add")
    public String showCreateFormClassSelection(ModelMap model) {
        model.addAttribute("schoolClassList", schoolClassService.findAll());
        model.addAttribute("selectedSchoolClass", new SchoolClass());

        return "create-transaction";
    }

    @GetMapping("/add/{schoolClass}")
    public String showCreateForm(@PathVariable("schoolClass") SchoolClass schoolClass, ModelMap model) {
        model.addAttribute("transaction", new CreateTransactionForm());

        model.addAttribute("childList", childService.findAllBySchoolClass(schoolClass));

        return "create-transaction";
    }

    //    @Secured("ROLE_ADMIN")
    @PostMapping("/add")
    public String create(@ModelAttribute("user") CreateTransactionForm form) {
        transactionService.create(TransactionMapper.toEntity(form));
        return "redirect:/mvc/user/add";
    }
}
