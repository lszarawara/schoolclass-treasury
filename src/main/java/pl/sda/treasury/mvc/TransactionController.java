package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import pl.sda.treasury.dto.TransactionCreationDto;
import pl.sda.treasury.dto.TransactionPreCreationDto;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.Transaction;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.mapper.TransactionMapper;
import pl.sda.treasury.service.*;

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
    private final CurrentSchoolClass currentSchoolClass;
    private final EmailServiceImpl emailService;

    @Secured({"ROLE_ADMIN"})
    @GetMapping
    public String getTransactionsList(ModelMap model) {
        model.addAttribute("transactions", transactionService.findAll());
        return "transactions";
    }

    @Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
    @GetMapping("/precreate/{type}")
    public String showPreCreateForm(@PathVariable("type") String type, ModelMap model) {
        TransactionPreCreationDto preForm = prepareTransactionPreCreationDto(type);
        model.addAttribute("preForm", preForm);
        model.addAttribute("isPredefined", false);
//        model.addAttribute("mailing", mailing);
        return "create-transaction";
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

    @Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
    @PostMapping("/precreate")
        public String showCreateForm(@ModelAttribute TransactionPreCreationDto preForm,
                                     @RequestParam String mailing,
                                     ModelMap model) {
            model.addAttribute("form", prepareTransactionCreationForm(preForm));
            model.addAttribute("isPredefined", true);
            model.addAttribute("preForm", preForm);
            model.addAttribute("mailing", mailing);

            return "create-transaction";

    }

    private TransactionCreationDto prepareTransactionCreationForm(TransactionPreCreationDto preForm) {
        TransactionCreationDto form = new TransactionCreationDto();
        List<Child> childList = childService.findAllActiveNonTechnicalBySchoolClass(schoolClassService.find(currentSchoolClass.getId()));

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
    @Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
    @PostMapping("/create")
    public String create(@ModelAttribute TransactionPreCreationDto preForm,
                         @ModelAttribute TransactionCreationDto form,
                         @RequestParam String mailing,
                         ModelMap model) {

        List<Transaction> transactions = prepareSaveRequest(preForm, form);
        model.addAttribute("createdTransactions", transactions);
        transactionService.createAll(transactions);
        if (mailing.equals("on")) prepareEmailMessage(transactions);
//        if (preForm.getTransactionType().equals("due")) prepareEmailMessage(transactions);

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
            roundingTransaction.setChild(childService.findTechnicalBySchoolClass(schoolClassService.find(currentSchoolClass.getId())));
            setTransactionType(preForm, roundingTransaction);
            transactionService.create(roundingTransaction);
        }
    }
    private void prepareEmailMessage(List<Transaction> transactions) {

        for (Transaction transaction : transactions) {
            for (User parent : transaction.getChild().getParents()) {
                String to = parent.getEmail();
                String subject = "Skarbnik Klasowy - nowa składka";
                String text = "Dzień Dobry!\n\n" + "Proszę o dokonanie wpłaty dla dziecka "
                        + childService.find(transaction.getChild().getId()).getFirstName() + " " + childService.find(transaction.getChild().getId()).getLastName()
                        + " w klasie " + childService.find(transaction.getChild().getId()).getSchoolClass().getName() + ".\n"
                        + "Tytuł wpłaty składki: " + transaction.getDescription() + " - " + childService.find(transaction.getChild().getId()).getFirstName() + " " + childService.find(transaction.getChild().getId()).getLastName()+ ".\n"
                        + "Kwota: " + transaction.getAmount() + " zł.\n"
                        + "\n\nPozdrawiamy\nSkarbnik Klasowy";
                emailService.sendSimpleMessage(to, subject, text);
            }
        }
    }
}