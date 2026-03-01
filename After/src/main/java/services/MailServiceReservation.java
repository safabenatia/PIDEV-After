package services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import model.Reservation;

public class MailServiceReservation {

    private final String username = "asma.antri@esprit.tn";
    private final String password = "pvxzttrkhypjxovc"; // Gmail App Password provided by user

    /**
     * Sends a confirmation email. Does not throw.
     * @return true if sent successfully, false otherwise (e.g. TLS/SSL error).
     */
    public boolean sendConfirmationEmail(String recipientEmail, Reservation res) {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.starttls.required", "true");
        // Prefer TLS 1.2 to avoid "No appropriate protocol" on Java 17+
        prop.put("mail.smtp.ssl.protocols", "TLSv1.2");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Confirmation de votre Réservation # " + res.getId() + " - After Travel");

            String htmlContent = "<html><body style='font-family: Arial, sans-serif; background-color: #FEFAF6; padding: 20px;'>"
                    +
                    "<div style='max-width: 600px; margin: auto; background-color: white; border-radius: 15px; overflow: hidden; border: 1px solid #A7927744;'>"
                    +
                    "<div style='background-color: #005082; padding: 30px; text-align: center; color: white;'>" +
                    "<h1>Confirmation de Réservation</h1>" +
                    "</div>" +
                    "<div style='padding: 30px; color: #333;'>" +
                    "<p>Bonjour,</p>" +
                    "<p>Votre réservation pour le voyage <strong>" + res.getLieu()
                    + "</strong> a été enregistrée avec succès !</p>" +
                    "<div style='background-color: #FEFAF6; padding: 20px; border-radius: 10px; margin: 20px 0; border: 1px solid #A7927722;'>"
                    +
                    "<p style='margin: 5px 0;'><strong>ID Réservation :</strong> #" + res.getId() + "</p>" +
                    "<p style='margin: 5px 0;'><strong>Destination :</strong> " + res.getLieu() + "</p>" +
                    "<p style='margin: 5px 0;'><strong>Nombre de personnes :</strong> " + res.getNbPersonnes() + "</p>"
                    +
                    "<p style='margin: 5px 0;'><strong>Prix Total :</strong> " + res.getPrixTotal() + " TND</p>" +
                    "<p style='margin: 5px 0;'><strong>Statut :</strong> <span style='color: #A79277; text-transform: uppercase; font-weight: bold;'>"
                    + res.getStatut() + "</span></p>" +
                    "</div>" +
                    "<p style='color: #d35400; font-weight: bold;'>⚠️ Rappel : Veuillez procéder au paiement pour confirmer définitivement votre voyage.</p>"
                    +
                    "<p>Vous pouvez effectuer le paiement directement depuis votre tableau de bord.</p>" +
                    "</div>" +
                    "<div style='background-color: #f8f9fa; padding: 20px; text-align: center; font-size: 12px; color: #777; border-top: 1px solid #eee;'>"
                    +
                    "<p>Merci d'avoir choisi <strong>After Travel</strong> !</p>" +
                    "<p>© 2026 After Travel. Tous droits réservés.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body></html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("📧 E-mail de confirmation envoyé à " + recipientEmail);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Erreur lors de l'envoi de l'e-mail: " + e.getMessage());
            return false;
        }
    }
}
