package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.treasury.entity.Transaction;
import pl.sda.treasury.repository.TransactionRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TransactionClassService {
    private final TransactionRepository repository;

    public Transaction find(long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Class with id=" + id + " not found"));
    }

    public List<Transaction> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public Transaction create(Transaction transaction) {
        return repository.save(transaction);
    }

    public Transaction update(Transaction transaction) {
        return repository.save(transaction);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

}