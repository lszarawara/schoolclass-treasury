package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.Transaction;
import pl.sda.treasury.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;
    private final ChildService childService;

    public Transaction find(long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Transaction with id=" + id + " not found"));
    }

    public List<Transaction> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Transaction> findAllbyChild(Long id) {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .filter(transaction -> transaction.getChild().getId()==id)
                .collect(Collectors.toList());
    }
    public Transaction create(Transaction transaction) {
        return repository.save(transaction);
    }

    public Iterable<Transaction> createAll(List<Transaction> transactions) {
        return repository.saveAll(transactions);
    }

    public Transaction update(Transaction transaction) {
        return repository.save(transaction);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    public BigDecimal getPaymentSumForChildren(Long id) {
        return findAllbyChild(id)
                .stream()
                .filter(transaction -> transaction.getType().equals(Transaction.Type.PAYMENT))
                .map(transaction -> transaction.getAmount())
                .reduce(BigDecimal.ZERO, (acc, curr) -> acc.add(curr));
    }

    public BigDecimal getDebitSumForChildren(Long id) {
        return findAllbyChild(id)
                .stream()
                .filter(transaction -> transaction.getType().equals(Transaction.Type.DEBIT))
                .map(transaction -> transaction.getAmount())
                .reduce(BigDecimal.ZERO, (acc, curr) -> acc.add(curr));
    }

    public BigDecimal getDueSumForChildren(Long id) {
        return findAllbyChild(id)
                .stream()
                .filter(transaction -> transaction.getType().equals(Transaction.Type.DUE))
                .map(transaction -> transaction.getAmount())
                .reduce(BigDecimal.ZERO, (acc, curr) -> acc.add(curr));
    }

    public BigDecimal getBalanceForChild(Long id) {
        return getPaymentSumForChildren(id).subtract(getDebitSumForChildren(id));
    }

    public BigDecimal getPaymentBalanceForChild(Long id) {
        return getPaymentSumForChildren(id).subtract(getDueSumForChildren(id));
    }

    public BigDecimal getBalanceForSchoolClass(SchoolClass schoolClass) {
        List<Child> childrenList = childService.findAllBySchoolClass(schoolClass);
        BigDecimal balance = BigDecimal.ZERO;
        for (int i = 0; i < childService.findAllBySchoolClass(schoolClass).size(); i++) {
            balance = balance.add(getBalanceForChild(childrenList.get(i).getId()));
        }
        return balance;
    }
    public List<BigDecimal> getListOfBalancesForSchoolClass(SchoolClass schoolClass) {
        List<Child> childrenList = childService.findAllBySchoolClass(schoolClass);
        List<BigDecimal> list = new ArrayList<>();
        for (int i = 0; i < childService.findAllBySchoolClass(schoolClass).size(); i++) {
            list.add(getBalanceForChild(childrenList.get(i).getId()));
        }
        return list;
    }
    public BigDecimal getPaymentBalanceForSchoolClass(SchoolClass schoolClass) {
        List<Child> childrenList = childService.findAllBySchoolClass(schoolClass);
        BigDecimal balance = BigDecimal.ZERO;
        for (int i = 0; i < childService.findAllBySchoolClass(schoolClass).size(); i++) {
            balance = balance.add(getPaymentBalanceForChild(childrenList.get(i).getId()));
        }
        return balance;
    }

    public List<BigDecimal> getListOfPaymentBalancesForSchoolClass(SchoolClass schoolClass) {
        List<Child> childrenList = childService.findAllBySchoolClass(schoolClass);
        List<BigDecimal> list = new ArrayList<>();
        for (int i = 0; i < childService.findAllBySchoolClass(schoolClass).size(); i++) {
            list.add(getPaymentBalanceForChild(childrenList.get(i).getId()));
        }
        return list;
    }
}