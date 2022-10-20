package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pl.sda.treasury.dto.TransactionCreationDto;
import pl.sda.treasury.dto.TransactionPreCreationDto;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.Transaction;
import pl.sda.treasury.mapper.TransactionMapper;
import pl.sda.treasury.service.ChildService;
import pl.sda.treasury.service.SchoolClassService;
import pl.sda.treasury.service.TransactionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mvc/transaction")
public class TransactionController {

    private final TransactionService transactionService;
    private final ChildService childService;
    private final SchoolClassService schoolClassService;


    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
//    private int selectedOption = 0;


    @GetMapping()
    public String getTransactionsList(ModelMap model) {
        model.addAttribute("transactions", transactionService.findAll());
        return "transactions";
    }

    @GetMapping("/{id}")
    public String getTransactionsListByChild(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("transactions", transactionService.findAllbyChild(id));
        model.addAttribute("child", childService.find(id));
        BigDecimal sumPayment = transactionService.findAllbyChild(id)
                .stream()
                .filter(transaction -> transaction.getType().equals(Transaction.Type.PAYMENT))
                .map(transaction -> transaction.getAmount())
                .reduce(BigDecimal.ZERO, (acc, curr) -> acc.add(curr));

        BigDecimal sumDebit = transactionService.findAllbyChild(id)
                .stream()
                .filter(transaction -> transaction.getType().equals(Transaction.Type.DEBIT))
                .map(transaction -> transaction.getAmount())
                .reduce(BigDecimal.ZERO, (acc, curr) -> acc.add(curr));
        model.addAttribute("sumPayment", sumPayment);
        model.addAttribute("sumDebit", sumDebit);

        return "transactionsByChild";
    }
    @GetMapping("/precreate/{type}")
    public String showPreCreateForm(@PathVariable("type") String type, ModelMap model) {
        TransactionPreCreationDto preForm = new TransactionPreCreationDto();
        preForm.setDate(LocalDate.now());
        if (type.equals("pay")) preForm.setDescription("Wpłata");
        preForm.setSelectedOption("0");
        preForm.setTransactionType(type);
//        form.setIsPredefined(false);
        model.addAttribute("preForm", preForm);
        Boolean preDefined = false;
        model.addAttribute("isPredefined", preDefined);

        return "create-transaction2";
    }

        @PostMapping("/precreate")
        public String showCreateForm(@ModelAttribute TransactionPreCreationDto preForm,
                                     ModelMap model) {
        TransactionCreationDto form = new TransactionCreationDto();
        List<Child> childList = childService.findAll();

        for (int i=0; i< childList.size(); i++) {
            Transaction transaction = new Transaction();
            transaction.setChild(childList.get(i));
            transaction.setDescription(preForm.getDescription());
            LocalDate fromFormDate = preForm.getDate();
            transaction.setDate(fromFormDate);
            switch (preForm.getTransactionType()) {
                case "pay":
                    transaction.setType(Transaction.Type.PAYMENT);
                    break;
                case "deb":
                    transaction.setType(Transaction.Type.DEBIT);
                    break;
                case "due":
                    transaction.setType(Transaction.Type.DUE);
                    break;
            }
            switch (preForm.getSelectedOption()) {
               case "1":
                    transaction.setAmount(preForm.getAmount().divide(BigDecimal.valueOf(childList.size()), 2, RoundingMode.HALF_EVEN));
                    break;
                case "2":
                    transaction.setAmount(preForm.getAmount());
                    break;
            }
            form.addTransaction(transaction);
        }
        model.addAttribute("form", form);
            Boolean isPredefined = true;
            model.addAttribute("isPredefined", isPredefined);
            model.addAttribute("preForm", preForm);
            return "create-transaction2";

    }

    @PostMapping("/create")
    public String create(@ModelAttribute TransactionPreCreationDto preForm,
                         @ModelAttribute TransactionCreationDto form) {


        switch (preForm.getSelectedOption()) {
            case "0":
                transactionService.createAll(form.getTransactions()
                        .stream()
                        .filter(transaction -> transaction.getAmount().signum()>0)
                        .collect(Collectors.toList()));
                break;
            case "3":
                int childrenNumber = form.getTransactions().size();
                BigDecimal amountToDebit = preForm.getAmount().divide(new BigDecimal(childrenNumber), 2, RoundingMode.HALF_EVEN);
                transactionService.createAll(form.getTransactions()
                        .stream()
                        .map(transaction -> {transaction.setAmount(amountToDebit); return transaction;})
                        .collect(Collectors.toList()));
                break;
            case "4":
                transactionService.createAll(form.getTransactions()
                        .stream()
                        .map(transaction -> {transaction.setAmount(preForm.getAmount()); return transaction;})
                        .collect(Collectors.toList()));
                break;
        }

        transactionService.createAll(form.getTransactions()
                .stream()
                .filter(transaction -> transaction.getAmount().signum()>0)
                .collect(Collectors.toList()));

        return "redirect:/mvc/transaction";
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
