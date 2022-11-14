package pl.sda.treasury.entity;


import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
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
    @Column(nullable = false)
    private Boolean isEnabled;

    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH})
      @JoinTable(
            name = "user_child",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "child_id"))
    List<Child> children = new ArrayList<>();





    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    List<SchoolClass> schoolClasses;


    public static enum Role {
        ROLE_ADMIN("Admin"),
        ROLE_SUPERUSER("SuperUser"),
        ROLE_USER("User");


        public final String label;

        private Role(String label) {
            this.label = label;
        }

        public String getLabel(){
            return label;
        }

    }
}
