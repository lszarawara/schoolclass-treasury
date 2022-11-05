package pl.sda.treasury.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
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

    @Setter
    private boolean isTechnical;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schoolClass_id", nullable = false)
    private SchoolClass schoolClass;

//    @ManyToMany(cascade = CascadeType.PERSIST)
//    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @JoinTable(
//            name = "user_child",
//            joinColumns = @JoinColumn(name = "child_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id"))
//    List<User> parents = new ArrayList<>();

    @ManyToMany(mappedBy = "children", cascade = CascadeType.PERSIST)
    List<User> parents = new ArrayList<>();


}
