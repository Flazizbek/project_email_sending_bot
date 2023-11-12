package org.example.model;

import lombok.NoArgsConstructor;
import org.example.model.SendEmailDetails;
import org.example.model.SenderEmailDetails;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Hello world!
 *
 */@NoArgsConstructor
public class EmailSending {
    public void emailSenderMethod(SendEmailDetails sendEmailDetails , SenderEmailDetails senderEmailDetails) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","465");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.ssl.enable","true");

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                         senderEmailDetails.getUserEmail(),
                        senderEmailDetails.getUserPassword());
            }
        };

        Session session = Session.getInstance(props, authenticator);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmailDetails.getUserEmail()));
        message.setRecipient(javax.mail.Message.RecipientType.TO , new InternetAddress(sendEmailDetails.getRecipient()));
        message.setSubject(sendEmailDetails.getSubject());
        message.setText(sendEmailDetails.getText());
            Transport.send(message);


    }
}