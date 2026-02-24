package api;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailAPI {

    // ===== TES VRAIES INFORMATIONS =====
    private static final String EXPEDITEUR = "safebenatiaa2020@gmail.com";
    private static final String MOT_DE_PASSE = "zwal gkce gykj tuvb"; // ← Ton mot de passe d'application
    // ===================================

    private static final String HOTE_SMTP = "smtp.gmail.com";
    private static final int PORT_SMTP = 587;

    public void envoyerFacture(String destinataire, String factureTexte, String reference) {
        String sujet = "Votre facture AFTER Travel - " + reference;
        String corpsHTML = "<html>" +
                "<head><style>body { font-family: Arial; }</style></head>" +
                "<body>" +
                "<h2 style='color:#16325c;'>AFTER Travel</h2>" +
                "<h3>Facture " + reference + "</h3>" +
                "<pre style='background-color:#F5F5DC; padding:15px;'>" + factureTexte + "</pre>" +
                "<p>Merci pour votre paiement !</p>" +
                "</body></html>";

        envoyerEmail(destinataire, sujet, corpsHTML, true);
    }

    private void envoyerEmail(String destinataire, String sujet, String corps, boolean estHTML) {

        Properties props = new Properties();
        props.put("mail.smtp.host", HOTE_SMTP);
        props.put("mail.smtp.port", PORT_SMTP);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EXPEDITEUR, MOT_DE_PASSE);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EXPEDITEUR));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);

            if (estHTML) {
                message.setContent(corps, "text/html; charset=utf-8");
            } else {
                message.setText(corps);
            }

            Transport.send(message);
            System.out.println("✅ Email réel envoyé à " + destinataire);

        } catch (MessagingException e) {
            System.err.println("❌ Erreur email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}