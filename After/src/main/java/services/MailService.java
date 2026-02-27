package services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class MailService {

    private final String username = "mahdi.bribech12@gmail.com";
    private final String password = "nctm rnll ewfr xnor"; // 🔴 Mets ton App Password

    public void sendMail(String to, String subject, String messageText) {

        Properties props = new Properties();

        // 🔥 Configuration SSL CORRECTE
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );

            message.setSubject(subject);
            message.setText(messageText);

            Transport.send(message);

            System.out.println("Email envoyé avec succès !");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}