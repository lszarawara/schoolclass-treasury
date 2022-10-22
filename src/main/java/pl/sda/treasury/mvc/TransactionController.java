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

    @GetMapping
    public String getTransactionsList(ModelMap model) {
        model.addAttribute("transactions", transactionService.findAll());
        return "transactions";
    }

    @GetMapping("/{id}")
    public String getTransactionsListByChildId(@PathVariable("id") Long id, ModelMap model) {
        model.addAttribute("transactions", transactionService.findAllbyChild(id));
        model.addAttribute("child", childService.find(id));
        model.addAttribute("sumPayment", transactionService.getPaymentSumForChildren(id));
        model.addAttribute("sumDebit", transactionService.getDebitSumForChildren(id));

        return "transactionsByChild";
    }

    @GetMapping("/precreate/{type}")
    public String showPreCreateForm(@PathVariable("type") String type, ModelMap model) {
        TransactionPreCreationDto preForm = prepareTransactionPreCreationDto(type);
        model.addAttribute("preForm", preForm);
        model.addAttribute("isPredefined", false);

        return "create-transaction2";
    }

    private static TransactionPreCreationDto prepareTransactionPreCreationDto(String type) {
        TransactionPreCreationDto preForm = new TransactionPreCreationDto();
        preForm.setDate(LocalDate.now());
        if (type.equals("pay")) preForm.setDescription("Wpłata");
        preForm.setSelectedOption("0");
        preForm.setTransactionType(type);
        return preForm;
    }

    @PostMapping("/precreate")
    public String showCreateForm(@ModelAttribute TransactionPreCreationDto preForm,
                                 ModelMap model) {
        model.addAttribute("form", prepareTransactionCreationForm(preForm));
        model.addAttribute("isPredefined", true);
        model.addAttribute("preForm", preForm);
        return "create-transaction2";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute TransactionPreCreationDto preForm,
                         @ModelAttribute TransactionCreationDto form) {

        handleSaveRequest(preForm, form);
        return "redirect:/mvc/transaction";
    }

    private void handleSaveRequest(TransactionPreCreationDto preForm, TransactionCreationDto form) {
        switch (preForm.getSelectedOption()) {
            case "0" -> //tryb 1 - przypisanie niezerowych kwot do powiazanych uczniow
                    transactionService.createAll(filterTransactionsWithAmountsGreaterThanZero(form));
            case "3" -> { //tryb 3 - dzielenie kwoty miedzy wszystkich uczniow
                int childrenNumber = form.getTransactions().size();
                BigDecimal amountToDebit = preForm.getAmount().divide(new BigDecimal(childrenNumber), 2, RoundingMode.HALF_EVEN);
                transactionService.createAll(fillTransactionsWithDefinedAmount(form, amountToDebit));
            }
            case "4" -> //tryb 4 - przypisanie danej kwoty do wszystkich uczniow
                    transactionService.createAll(fillTransactionsWithDataFromPreform(preForm, form));
        }
    }

    private static List<Transaction> fillTransactionsWithDataFromPreform(TransactionPreCreationDto preForm, TransactionCreationDto form) {
        return fillTransactionsWithDefinedAmount(form, preForm.getAmount());
    }

    private static List<Transaction> fillTransactionsWithDefinedAmount(TransactionCreationDto form, BigDecimal amountToDebit) {
        return form.getTransactions()
                .stream()
                .peek(transaction -> transaction.setAmount(amountToDebit)) //operacja ktora wykonuje cos na obiekcie,
                                                                            // ale nie zmienia go na inny (w przeciwienstwie do .map)
                .collect(Collectors.toList());
    }

    private static List<Transaction> filterTransactionsWithAmountsGreaterThanZero(TransactionCreationDto form) {
        return form.getTransactions()
                .stream()
                .filter(transaction -> transaction.getAmount().signum() > 0)
                .collect(Collectors.toList());
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
    private TransactionCreationDto prepareTransactionCreationForm(TransactionPreCreationDto preForm) {
        TransactionCreationDto form = new TransactionCreationDto();
        List<Child> childList = childService.findAll();

        for (int i = 0; i < childList.size(); i++) {
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
        return form;
    }
}
