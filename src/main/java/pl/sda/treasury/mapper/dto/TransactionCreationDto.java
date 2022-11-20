package pl.sda.treasury.mapper.dto;

import lombok.*;
import pl.sda.treasury.entity.Transaction;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
public class TransactionCreationDto {
    private List<Transaction> transactions = new ArrayList<>();


    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}
