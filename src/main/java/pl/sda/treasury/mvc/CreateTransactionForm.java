package pl.sda.treasury.mvc;

import lombok.Data;
import pl.sda.treasury.entity.Child;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateTransactionForm {
    private String description;
    private LocalDate date;
    private BigDecimal amount;
    private Child child;
    private String type;

}
