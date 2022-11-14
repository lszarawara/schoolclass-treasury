package pl.sda.treasury.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import pl.sda.treasury.entity.User;
import pl.sda.treasury.service.ChildService;
import pl.sda.treasury.service.MailService;
import pl.sda.treasury.service.UserService;

import javax.mail.MessagingException;

@Controller
@SessionScope
@RequiredArgsConstructor
@RequestMapping("/mvc/mail")
public class MailController {

    private final MailService mailService;
    private final UserService userService;

    @GetMapping("/{id}")//    public String sendMail(@RequestBody User user)throws MessagingException {
    public String sendMail(@PathVariable("id") Long id)throws MessagingException {
                mailService.sendMail(userService.find(id));
        return "Email Sent Successfully.!";
    }
}
