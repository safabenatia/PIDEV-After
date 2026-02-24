package api;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class PaiementAPI {

    public static class Facture {
        private String reference;
        private double montant;
        private LocalDate date;
        private String statut;
        private String service;
        private String clientEmail;

        public Facture(String service, double montant, String email) {
            this.service = service;
            this.montant = montant;
            this.clientEmail = email;
            this.date = LocalDate.now();
            this.reference = "FACT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            this.statut = "PAYÉ";
        }

        public String getReference() { return reference; }
        public double getMontant() { return montant; }
        public LocalDate getDate() { return date; }
        public String getStatut() { return statut; }
        public String getService() { return service; }
        public String getClientEmail() { return clientEmail; }

        public String toTexte() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateStr = date.format(formatter);

            StringBuilder txt = new StringBuilder();
            txt.append("=".repeat(60)).append("\n");
            txt.append("                    FACTURE AFTER TRAVEL\n");
            txt.append("=".repeat(60)).append("\n\n");
            txt.append("Date : ").append(dateStr).append("\n");
            txt.append("Référence : ").append(reference).append("\n");
            txt.append("Client : ").append(clientEmail).append("\n");
            txt.append("Service : ").append(service).append("\n");
            txt.append("Montant : ").append(montant).append(" DT\n");
            txt.append("Statut : ").append(statut).append("\n\n");
            txt.append("-".repeat(60)).append("\n");
            txt.append("Merci pour votre paiement !\n");
            txt.append("=".repeat(60)).append("\n");

            return txt.toString();
        }
    }

    public Facture payer(String service, double montant, String email, String methode) {
        System.out.println("\n💳 TRAITEMENT DU PAIEMENT");
        System.out.println("=".repeat(40));
        System.out.println("Service : " + service);
        System.out.println("Montant : " + montant + " DT");
        System.out.println("Email : " + email);
        System.out.println("Méthode : " + methode);
        System.out.println("=".repeat(40));

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Facture facture = new Facture(service, montant, email);
        System.out.println("✅ Paiement accepté !");
        System.out.println("Référence : " + facture.getReference());

        return facture;
    }
}