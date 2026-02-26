package services;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.UUID;

public class EmailService {

    private static final String FROM_EMAIL = "ayzchagra@gmail.com"; // ← CHANGE ÇA
    private static final String APP_PASSWORD = "evwb tble ajxj wqzu"; // ← App Password Gmail (pas ton mot de passe normal)

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    public void sendVerificationEmail(String toEmail, String token) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", String.valueOf(SMTP_PORT));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("After Travel - Activez votre compte");

        String verificationLink = "http://localhost:8081/verify?token=" + token; // ← change l'URL en prod

        String body = "Bonjour,\n\n" +
                "Merci de vous être inscrit sur After Travel !\n\n" +
                "Pour activer votre compte, cliquez sur ce lien :\n" +
                verificationLink + "\n\n" +
                "Ce lien est valide pendant 24 heures.\n\n" +
                "À bientôt sur After Travel !";

        message.setText(body);

        Transport.send(message);
        System.out.println("Email de vérification envoyé à : " + toEmail);
    }
}