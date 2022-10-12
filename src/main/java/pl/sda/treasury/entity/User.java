package pl.sda.treasury.entity;


import lombok.Setter;
import org.hibernate.annotations.ManyToAny;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class User {

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
    @Column(nullable = false, unique = true)
    private String login;

    @Setter
    private String password;

    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    private Role role;

    @ManyToMany(mappedBy = "parents")
    Set<Child> children;

    public static enum Role {
        Admin,
        Superuser,
        User
    }
}
