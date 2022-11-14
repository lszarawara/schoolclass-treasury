package pl.sda.treasury.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import pl.sda.treasury.entity.User;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.NamingException;

@Service
@RequiredArgsConstructor
public class MailService {
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;

    private final UserService userService;

//    public MailService(TemplateEngine templateEngine, JavaMailSender javaMailSender) {
//        this.templateEngine = templateEngine;
//        this.javaMailSender = javaMailSender;
//    }

    public String sendMail(User user) throws MessagingException {
//        Context context = new Context();
//        context.setVariable("user", user);
//
//        String process = templateEngine.process("emails/welcome", context);
        String process = templateEngine.process("welcome", (IContext) user);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("Welcome " + user.getLastName());
        helper.setText(process, true);
        helper.setTo(user.getEmail());
        javaMailSender.send(mimeMessage);
        return "Sent";
    }
}
