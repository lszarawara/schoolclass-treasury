package pl.sda.treasury.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.Transaction;

import java.util.List;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

}
