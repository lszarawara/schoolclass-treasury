package pl.sda.treasury.service;

public interface EmailService {
    void sendSimpleMessage(String to,
                           String subject,
                           String text);
}
