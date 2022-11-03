package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.SchoolClass;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.repository.ChildRepository;
import pl.sda.treasury.repository.SchoolClassRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class SchoolClassService {
    private final SchoolClassRepository repository;

    public SchoolClass find(long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Class with id=" + id + " not found"));
    }

    public List<SchoolClass> findAll() {
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public SchoolClass findLastId () {
//        return repository.findTopByOrderByIdDesc();
        //zamiennie ostatni ze streama:
        return StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .reduce((first, second) -> second)
                .orElse(null);
    }

    public SchoolClass create(SchoolClass schoolClass) {
        return repository.save(schoolClass);
    }

    public SchoolClass update(SchoolClass schoolClass) {
        return repository.save(schoolClass);
    }
//patch???

    public void delete(long id) {
        repository.deleteById(id);
    }

}