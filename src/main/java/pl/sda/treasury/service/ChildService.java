package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.repository.ChildRepository;
import pl.sda.treasury.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ChildService {
    private final ChildRepository repository;
//    private final PasswordEncoder passwordEncoder; //po security

    public Child find(long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Child with id=" + id + " not found"));
    }

    public List<Child> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Child> findAllBySchoolClass(SchoolClass schoolClass) {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .filter(child -> child.getSchoolClass().equals(schoolClass))
                .collect(Collectors.toList());
    }

    public Child create(Child child) {
        return repository.save(child);
    }

    public Child update(Child child) {
        return repository.save(child);
    }
//patch???

    public void delete(long id) {
        repository.deleteById(id);
    }

}