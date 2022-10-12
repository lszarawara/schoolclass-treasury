package pl.sda.treasury.entity;

import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @Column(nullable = false)
    private String firstName;

    @Setter
    @Column(nullable = false)
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schoolClass_id", nullable = false)
    private SchoolClass schoolClass;

    @ManyToMany
    @JoinTable(
            name = "user_child",
            joinColumns = @JoinColumn(name = "child_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<User> parents;



}
