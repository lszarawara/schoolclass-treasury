package pl.sda.treasury.entity;


import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
        Admin("ROLE_ADMIN"),
        Superuser("ROLE_ADMIN"),
        User("ROLE_ADMIN");

        public final String label;

        private Role(String label) {
            this.label = label;
        }


    }
}
