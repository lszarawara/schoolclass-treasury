package pl.sda.treasury.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import pl.sda.treasury.entity.Child;

public interface ChildRepository extends PagingAndSortingRepository<Child, Long> {
}
