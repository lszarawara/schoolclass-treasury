package pl.sda.treasury.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.User;

public interface ChildRepository extends PagingAndSortingRepository<Child, Long> {
}
