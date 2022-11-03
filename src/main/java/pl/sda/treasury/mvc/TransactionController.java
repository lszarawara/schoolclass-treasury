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

import static pl.sda.treasury.TreasuryApplication.currentSchoolClass;

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
        if (type.equals("due")) preForm.setDescription("Składka");
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

    private TransactionCreationDto prepareTransactionCreationForm(TransactionPreCreationDto preForm) {
        TransactionCreationDto form = new TransactionCreationDto();
        List<Child> childList = childService.findAllNonTechnicalBySchoolClass(schoolClassService.find(currentSchoolClass));

        for (int i=0; i< childList.size(); i++) {
            Transaction transaction = new Transaction();
            transaction.setChild(childList.get(i));
            transaction.setDescription(preForm.getDescription());
            LocalDate fromFormDate = preForm.getDate();
            transaction.setDate(fromFormDate);
            setTransactionType(preForm, transaction);
            form.addTransaction(transaction);
        }
        return form;
    }

    private static void setTransactionType(TransactionPreCreationDto preForm, Transaction transaction) {
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
    }

    @PostMapping("/create")
    public String create(@ModelAttribute TransactionPreCreationDto preForm,
                         @ModelAttribute TransactionCreationDto form, ModelMap model) {

        List<Transaction> transactions = prepareSaveRequest(preForm, form);
        model.addAttribute("createdTransactions", transactions);
        transactionService.createAll(transactions);
        return "createdTransactions";
    }

    private List<Transaction> prepareSaveRequest(TransactionPreCreationDto preForm, TransactionCreationDto form) {
        List<Transaction> preparedTransactionsList = null;
        switch (preForm.getSelectedOption()) {

            case "1":
                int childrenNumber1 = form.getTransactions().size();
                BigDecimal amountToDebit1 = preForm.getAmount().divide(new BigDecimal(childrenNumber1), 2, RoundingMode.HALF_EVEN);
                addRoundingTransaction(preForm, childrenNumber1, amountToDebit1);
                preparedTransactionsList = form.getTransactions()
                        .stream()
                        .map(transaction -> {transaction.setAmount(amountToDebit1); return transaction;})
                        .collect(Collectors.toList());
                break;
            case "2":
                preparedTransactionsList = form.getTransactions()
                        .stream()
                        .map(transaction -> {transaction.setAmount(preForm.getAmount()); return transaction;})
                        .collect(Collectors.toList());
                break;
            case "3":
                int childrenNumber3 = (int) form.getTransactions()
                        .stream()
                        .filter(transaction -> transaction.getIsParticipating())
                        .count();

                BigDecimal amountToDebit3 = preForm.getAmount().divide(new BigDecimal(childrenNumber3), 2, RoundingMode.HALF_EVEN);
                addRoundingTransaction(preForm, childrenNumber3, amountToDebit3);
                preparedTransactionsList = form.getTransactions()
                        .stream()
                        .filter(transaction -> transaction.getIsParticipating())
                        .map(transaction -> {transaction.setAmount(amountToDebit3); return transaction;})
                        .collect(Collectors.toList());
                break;
            case "4":
                preparedTransactionsList = form.getTransactions()
                        .stream()
                        .filter(transaction -> transaction.getIsParticipating())
                        .map(transaction -> {transaction.setAmount(preForm.getAmount()); return transaction;})
                        .collect(Collectors.toList());
                break;

            case "6":
                preparedTransactionsList = form.getTransactions()
                        .stream()
                        .map(transaction -> {transaction.setAmount(preForm.getAmount()
                                .subtract(transactionService.getBalanceForChild(transaction.getChild().getId()))); return transaction;})
                        .collect(Collectors.toList());
                break;

            default: //CASE 0, 5
                preparedTransactionsList = form.getTransactions()
                        .stream()
                        .filter(transaction -> transaction.getAmount() != null)
                        .collect(Collectors.toList());
        }
    return preparedTransactionsList;
    }

    private void addRoundingTransaction(TransactionPreCreationDto preForm, int childrenNumber, BigDecimal amountToDebit) {
        BigDecimal rounding = preForm.getAmount().subtract(amountToDebit.multiply(new BigDecimal(childrenNumber)));
        if (!rounding.equals(0)) {
            Transaction roundingTransaction = new Transaction();
            roundingTransaction.setDate(preForm.getDate());
            roundingTransaction.setAmount(rounding);
            roundingTransaction.setDescription(preForm.getDescription());
            roundingTransaction.setChild(childService.findTechnicalBySchoolClass(schoolClassService.find(currentSchoolClass)));
            setTransactionType(preForm, roundingTransaction);
//            Transaction roundingTransaction = new Transaction(,preForm.getDate(), rounding, preForm.getDescription(),null,
//                    childService.findTechnicalBySchoolClass(schoolClassService.find(currentSchoolClass)), null);
//            setTransactionType(preForm, roundingTransaction);
////                    to tu jest brzydko, jak to powiązać z zestawem transakcji i spowodować, że zapisze się całość lub nic?
            transactionService.create(roundingTransaction);
        }
    }

//    private void handleSaveRequest(TransactionPreCreationDto preForm, TransactionCreationDto form) {
//        switch (preForm.getSelectedOption()) {
////            case "0":
////                transactionService.createAll(form.getTransactions()
////                        .stream()
////                        .filter(transaction -> transaction.getAmount() != null)
//////                        .filter(transaction -> transaction.getAmount().signum()>0)
////                        .collect(Collectors.toList()));
////                break;
////            case "1":
////                int childrenNumber = form.getTransactions().size();
////                BigDecimal amountToDebit = preForm.getAmount().divide(new BigDecimal(childrenNumber), 2, RoundingMode.HALF_EVEN);
////                transactionService.createAll(form.getTransactions()
////                        .stream()
////                        .map(transaction -> {transaction.setAmount(amountToDebit); return transaction;})
////                        .collect(Collectors.toList()));
////                break;
////            case "2":
////                transactionService.createAll(form.getTransactions()
////                        .stream()
////                        .map(transaction -> {transaction.setAmount(preForm.getAmount()); return transaction;})
////                        .collect(Collectors.toList()));
////                break;
//            case "3":
//                int childrenNumber = (int) form.getTransactions()
//                        .stream()
//                        .filter(transaction -> transaction.getIsParticipating())
//                        .count();
//
//                BigDecimal amountToDebit = preForm.getAmount().divide(new BigDecimal(childrenNumber), 2, RoundingMode.HALF_EVEN);
//                transactionService.createAll(form.getTransactions()
//                        .stream()
//                        .filter(transaction -> transaction.getIsParticipating())
//                        .map(transaction -> {transaction.setAmount(amountToDebit); return transaction;})
//                        .collect(Collectors.toList()));
//                break;
//            case "4":
//                transactionService.createAll(form.getTransactions()
//                        .stream()
//                        .filter(transaction -> transaction.getIsParticipating())
//                        .map(transaction -> {transaction.setAmount(preForm.getAmount()); return transaction;})
//                        .collect(Collectors.toList()));
//                break;
//
////            case "6":
////                transactionService.createAll(form.getTransactions()
////                        .stream()
////                        .map(transaction -> {transaction.setAmount(preForm.getAmount()
////                                .subtract(transactionService.getBalanceForChild(transaction.getChild().getId()))); return transaction;})
////                        .collect(Collectors.toList()));
////                break;
//
//            default:
//                transactionService.createAll(form.getTransactions()
//                        .stream()
//                        .filter(transaction -> transaction.getAmount() != null)
//                        .collect(Collectors.toList()));
//
//        }
//    }


    @GetMapping("/add")
    public String showCreateFormClassSelection(ModelMap model) {
        model.addAttribute("schoolClassList", schoolClassService.findAll());
        model.addAttribute("selectedSchoolClass", new SchoolClass(currentSchoolClass));

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
