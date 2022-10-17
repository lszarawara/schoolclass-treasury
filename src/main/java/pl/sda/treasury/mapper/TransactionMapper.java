package pl.sda.treasury.mapper;

import org.springframework.stereotype.Component;
import pl.sda.treasury.entity.Transaction;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.mvc.CreateTransactionForm;
import pl.sda.treasury.mvc.CreateUserForm;
import pl.sda.treasury.mvc.UpdateUserForm;

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

//    public static User toEntity(UpdateUserForm form) {
//        return User.builder()
//                .id(form.getId())
//                .firstName(form.getFirstName())
//                .lastName(form.getLastName())
//                .email(form.getEmail())
//                .login(form.getLogin())
//                .password(form.getPassword())
//                .role(User.Role.valueOf(form.getRole()))
//                .build();
//    }
}
