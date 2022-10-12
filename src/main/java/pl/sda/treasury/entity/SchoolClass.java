package pl.sda.treasury.entity;

import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
public class SchoolClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    @Column(nullable = false)
    private String name;

//    @OneToMany (cascade = CascadeType.ALL, fetch = FetchType.LAZY )
//    private List<Child> children;
}
