package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.Transaction;
import pl.sda.treasury.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
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
        return repository.findAllByChild_Id(id);
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
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDebitSumForChildren(Long id) {
        return findAllbyChild(id)
                .stream()
                .filter(transaction -> transaction.getType().equals(Transaction.Type.DEBIT))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getDueSumForChildren(Long id) {
        return findAllbyChild(id)
                .stream()
                .filter(transaction -> transaction.getType().equals(Transaction.Type.DUE))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBalanceForChild(Long id) {
        return getPaymentSumForChildren(id).subtract(getDebitSumForChildren(id));
    }

    public BigDecimal getPaymentBalanceForChild(Long id) {
        return getPaymentSumForChildren(id).subtract(getDueSumForChildren(id));
    }

    public BigDecimal getBalanceForSchoolClass(SchoolClass schoolClass) {
        return childService.findAllBySchoolClass(schoolClass)
                .stream()
                .map(child -> getBalanceForChild(child.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public List<BigDecimal> getListOfNonTechnicalBalancesForSchoolClass(SchoolClass schoolClass) {
        return childService.findAllActiveNonTechnicalBySchoolClass(schoolClass)
                .stream()
                .map(childEntity -> getBalanceForChild(childEntity.getId()))
                .collect(Collectors.toList());
    }
    public List<BigDecimal> getListOfNonActiveNonTechnicalBalancesForSchoolClass(SchoolClass schoolClass) {
        return childService.findAllNonActiveNonTechnicalBySchoolClass(schoolClass)
                .stream()
                .map(childEntity -> getBalanceForChild(childEntity.getId()))
                .collect(Collectors.toList());

    }
    public BigDecimal getPaymentBalanceForSchoolClass(SchoolClass schoolClass) {
        return childService.findAllBySchoolClass(schoolClass)
                .stream()
                .map(child -> getPaymentBalanceForChild(child.getId()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<BigDecimal> getListOfNonTechnicalPaymentBalancesForSchoolClass(SchoolClass schoolClass) {
        return childService.findAllActiveNonTechnicalBySchoolClass(schoolClass)
                .stream()
                .map(childEntity -> getPaymentBalanceForChild(childEntity.getId()))
                .collect(Collectors.toList());
    }

    public List<BigDecimal> getListOfNonActiveNonTechnicalPaymentBalancesForSchoolClass(SchoolClass schoolClass) {
        return childService.findAllNonActiveNonTechnicalBySchoolClass(schoolClass)
                .stream()
                .map(childEntity -> getPaymentBalanceForChild(childEntity.getId()))
                .collect(Collectors.toList());
    }

    public BigDecimal getBalanceForTechnicalAccountBySchoolClass(SchoolClass schoolClass) {
        Long id = childService.findTechnicalBySchoolClass(schoolClass).getId();
        return getPaymentSumForChildren(id).subtract(getDebitSumForChildren(id));
    }
}