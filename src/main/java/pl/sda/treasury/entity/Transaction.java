package pl.sda.treasury.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.annotation.Transient;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate date; //data

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @Setter
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Child_id", nullable = false)
    private Child child;

    @Transient
    private Boolean isParticipating;


    public static enum Type {
        DUE("Składka"),
        PAYMENT("Wpłata"),
        DEBIT("Wydatek");

        public final String label;

        private Type(String label) {
            this.label = label;
        }

        public String getLabel(){
            return label;
        }

    }

}
