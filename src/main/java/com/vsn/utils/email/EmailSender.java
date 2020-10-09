package com.vsn.utils.email;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailSender {

    private static final String CONFIRM_LOGIN = "VSN | Reset password";


    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final JavaMailSender mailSender;


    private void send(String toEmail, String subject, String emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText(emailMessage);
        message.setSubject(subject);
        message.setTo(toEmail);
        message.setFrom("vsn@gmail.com");
        mailSender.send(message);
    }

    public void sendEmailToAddress(List<String> emails, String subject, String message) {
        executorService.submit(() -> emails.forEach(email -> send(email, subject, message)));
    }

    public void sendEmailOnLoginConfirm(String email, String code) {
        sendEmailToAddress(Stream.of(email).collect(toList()), CONFIRM_LOGIN, String.format(
                        "You code :\n" +
                        "%s\n" +
                        "Thank you for using our service!",code
        ));
    }

    public void sendEmailOnRestorePasswordConfirm(String email, String code) {
        sendEmailToAddress(Stream.of(email).collect(toList()), CONFIRM_LOGIN, String.format(
                "You link :\n" +
                        "%s\n" +
                        "Thank you for using our service!",code
        ));
    }


}
