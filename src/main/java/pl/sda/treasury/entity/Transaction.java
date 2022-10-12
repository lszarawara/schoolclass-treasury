package pl.sda.treasury.entity;

import lombok.Setter;

import javax.persistence.*;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String date; //data

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private String description;

    @Setter
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Child_id", nullable = false)
    private Child child;

    public static enum Type {
        Payment,
        Debit
    }



}
