package pl.sda.treasury.mapper;

import org.springframework.stereotype.Component;
import pl.sda.treasury.entity.Transaction;
import pl.sda.treasury.mvc.CreateTransactionForm;

@Component
public class TransactionMapper {

    public static Transaction toEntity(CreateTransactionForm form) {
        return Transaction.builder()
                .date(form.getDate())
                .amount(form.getAmount())
                .description(form.getDescription())
                .child(form.getChild())
                .type(Transaction.Type.valueOf(form.getType()))
                .build();
    }
}
