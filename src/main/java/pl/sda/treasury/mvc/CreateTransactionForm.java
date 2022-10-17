package pl.sda.treasury.mvc;

import lombok.Data;
import pl.sda.treasury.entity.Child;
import pl.sda.treasury.entity.Transaction;

import java.time.LocalDate;

@Data
public class CreateTransactionForm {
    private String description;
    private LocalDate date;
    private Double amount;
    private Child child;
    private String type;

}
