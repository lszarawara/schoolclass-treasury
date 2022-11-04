package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.repository.ChildRepository;

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
                .stream(repository.findAll(Sort.by("lastName").and(Sort.by("firstName"))).spliterator(), false)
                .filter(child -> child.getSchoolClass().equals(schoolClass))
                .collect(Collectors.toList());
//        return repository.findAllBySchoolClass(schoolClass, Sort.by("firstName").and(Sort.by("lastName")));
    }

    public List<Child> findAllNonTechnicalBySchoolClass(SchoolClass schoolClass) {
        return StreamSupport
                .stream(repository.findAll(Sort.by("lastName").and(Sort.by("firstName"))).spliterator(), false)
                .filter(child -> child.getSchoolClass().equals(schoolClass))
                .filter(child -> !child.isTechnical())
                .collect(Collectors.toList());
    }

    public Child findTechnicalBySchoolClass(SchoolClass schoolClass) {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .filter(child -> child.getSchoolClass().equals(schoolClass))
                .filter(child -> child.isTechnical())
                .findFirst().orElseThrow(() -> new RuntimeException("Technical account for school class =" + schoolClass.getName() + " not found"));
    }
    public Child create(Child child) {
        return repository.save(child);
    }

    public Child createTechnicalAccountForSchoolClass(SchoolClass schoolClass) {
        Child child = new Child();
        child.setFirstName("techniczne");
        child.setLastName("konto zaokrągleń");
        child.setSchoolClass(schoolClass);
        child.setTechnical(true);
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