package api;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.util.Properties;

public class EmailAPI {

    private static final String EXPEDITEUR = "safebenatiaa2020@gmail.com";
    private static final String MOT_DE_PASSE = "zwal gkce gykj tuvb";

    private static final String HOTE_SMTP = "smtp.gmail.com";
    private static final int PORT_SMTP = 587;

    public void envoyerEmailAvecPDF(String destinataire, String sujet, String corpsTexte, String cheminPDF) {

        Properties props = new Properties();
        props.put("mail.smtp.host", HOTE_SMTP);
        props.put("mail.smtp.port", PORT_SMTP);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", HOTE_SMTP);

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

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(corpsTexte);

            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(new File(cheminPDF));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("✅ Email avec PDF envoyé à " + destinataire);

        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void envoyerFacturePDF(String destinataire, PaiementAPI.Facture facture, String cheminPDF) {
        String sujet = "Votre facture AFTER Travel - " + facture.getReference();
        String corps = "Bonjour,\n\n" +
                "Veuillez trouver ci-joint votre facture pour le service : " + facture.getService() + "\n" +
                "Montant : " + facture.getMontant() + " DT\n" +
                "Référence : " + facture.getReference() + "\n\n" +
                "Merci pour votre confiance !\n" +
                "L'équipe AFTER Travel";

        envoyerEmailAvecPDF(destinataire, sujet, corps, cheminPDF);
    }
}