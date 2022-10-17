package pl.sda.treasury.dto;

import lombok.*;
import pl.sda.treasury.entity.Transaction;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class TransactionCreationDto {
    private List<Transaction> transactions;


    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
