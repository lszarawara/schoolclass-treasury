package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
//    private final PasswordEncoder passwordEncoder; //po security

    public User find(long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User with id=" + id + " not found"));
    }

    public User findByEmail(String email) {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .filter(user -> user.getEmail().equals(email))
                .findFirst().orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
    }

    public List<User> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public User findLastId () {
//        return repository.findTopByOrderByIdDesc();
        //zamiennie ostatni ze streama:
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .reduce((first, second) -> second)
                .orElse(null);
    }

    public User create(User user) {
//        user.setPassword(encodePassword(user.getPassword())); //do włączenia po dodaniu autentykacji
        return repository.save(user);
    }

    public User update(User user) {
//        user.setPassword(encodePassword(user.getPassword()));
        return repository.save(user);
    }
//patch???

    public void delete(long id) {
        repository.deleteById(id);
    }

//    private String encodePassword(String password) {
//        return passwordEncoder.encode(password);
//
//    }
}