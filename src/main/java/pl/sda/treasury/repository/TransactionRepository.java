package pl.sda.treasury.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.sda.treasury.entity.Transaction;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {
}
