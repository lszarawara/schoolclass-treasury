package pl.sda.treasury.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import pl.sda.treasury.entity.User;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {
}
