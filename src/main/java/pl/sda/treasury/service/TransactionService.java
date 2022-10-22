package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.treasury.entity.Transaction;
import pl.sda.treasury.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository repository;

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
}
