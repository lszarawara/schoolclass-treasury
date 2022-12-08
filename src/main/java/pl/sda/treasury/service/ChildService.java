package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.repository.ChildRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ChildService {
    private final ChildRepository repository;

    public Child find(long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Child with id=" + id + " not found"));
    }

    public List<Child> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Child> findAllBySchoolClass(SchoolClass schoolClass) {
        return repository.findBySchoolClass(schoolClass)
                .stream()
                .sorted(Comparator.comparing(Child::getLastName).thenComparing(Child::getFirstName))
                .collect(Collectors.toList());
    }

    public List<Child> findAllActiveNonTechnicalBySchoolClass(SchoolClass schoolClass) {
        return findAllBySchoolClass(schoolClass)
                .stream()
                .filter(child -> !child.isTechnical())
                .filter(Child::getIsActive)
                .collect(Collectors.toList());
    }

    public List<Child> findAllNonActiveNonTechnicalBySchoolClass(SchoolClass schoolClass) {
        return findAllBySchoolClass(schoolClass)
                .stream()
                .filter(child -> !child.isTechnical())
                .filter(child -> !child.getIsActive())
                .collect(Collectors.toList());
    }

    public Child findTechnicalBySchoolClass(SchoolClass schoolClass) {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .filter(child -> child.getSchoolClass().equals(schoolClass))
                .filter(Child::isTechnical)
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

    public void delete(long id) {
        repository.deleteById(id);
    }

}