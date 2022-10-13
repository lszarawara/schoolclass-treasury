package pl.sda.treasury.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.User;

import java.util.List;

public interface SchoolClassRepository extends PagingAndSortingRepository<SchoolClass, Long> {

//    List<SchoolClass> findAllByIdGreaterThan(long id);

}
