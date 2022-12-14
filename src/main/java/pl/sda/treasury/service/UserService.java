package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public User find(long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User with id=" + id + " not found"));
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(() -> new RuntimeException("User with email " + email + " not found"));
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    public User findByLogin(String login) {
        return repository.findByLogin(login).orElseThrow(() -> new RuntimeException("User with email " + login + " not found"));
    }

    public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }
    public List<User> findAll() {
        return repository.findAll().stream().collect(Collectors.toList());
    }

    public User create(User user) {
        user.setPassword(encodePassword(user.getPassword()));
        return repository.save(user);
    }

    public User update(User user) {
        user.setPassword(encodePassword(user.getPassword()));
        return repository.save(user);
    }

    public User updateChildList(User user) {
        return repository.save(user);
    }

    public void delete(long id) {
        repository.deleteById(id);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);

    }
}