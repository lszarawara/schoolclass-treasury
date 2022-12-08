package pl.sda.treasury.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;

import java.util.List;

public interface ChildRepository extends PagingAndSortingRepository<Child, Long> {

    List<Child> findBySchoolClass(SchoolClass schoolClass);
}
