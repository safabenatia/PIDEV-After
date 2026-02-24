package api;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PDFAPI {

    /**
     * Génère un PDF de facture à partir des informations de paiement
     * @param facture La facture générée par PaiementAPI
     * @param cheminDestination Le chemin où sauvegarder le PDF
     * @return true si le PDF a été généré avec succès
     */
    public boolean genererFacturePDF(PaiementAPI.Facture facture, String cheminDestination) {
        try {
            // Créer le fichier PDF
            PdfWriter writer = new PdfWriter(cheminDestination);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Formatage de la date
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateStr = facture.getDate().format(formatter);

            // ===== EN-TÊTE =====
            Paragraph titre = new Paragraph("AFTER TRAVEL")
                    .setBold()
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titre);

            Paragraph sousTitre = new Paragraph("FACTURE")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(sousTitre);

            document.add(new Paragraph("\n"));

            // ===== INFORMATIONS FACTURE =====
            document.add(new Paragraph("Date : " + dateStr));
            document.add(new Paragraph("Référence : " + facture.getReference()));
            document.add(new Paragraph("Client : " + facture.getClientEmail()));
            document.add(new Paragraph("\n"));

            // ===== TABLEAU DES SERVICES =====
            float[] columnWidths = {2, 1, 1};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // En-têtes du tableau
            table.addHeaderCell("Service");
            table.addHeaderCell("Montant");
            table.addHeaderCell("Statut");

            // Ligne du service
            table.addCell(facture.getService());
            table.addCell(facture.getMontant() + " DT");
            table.addCell(facture.getStatut());

            document.add(table);

            document.add(new Paragraph("\n"));

            // ===== TOTAL =====
            Paragraph total = new Paragraph("TOTAL : " + facture.getMontant() + " DT")
                    .setBold()
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(total);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("\n"));

            // ===== PIED DE PAGE =====
            Paragraph footer = new Paragraph("Merci pour votre confiance !")
                    .setItalic()
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);

            Paragraph contact = new Paragraph("AFTER Travel - Votre agence de voyages intelligente")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(contact);

            // Fermer le document
            document.close();

            System.out.println("✅ PDF généré avec succès : " + cheminDestination);
            return true;

        } catch (FileNotFoundException e) {
            System.err.println("❌ Erreur lors de la génération du PDF : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Génère un PDF et retourne le chemin du fichier
     */
    public String genererFactureEtRetournerChemin(PaiementAPI.Facture facture) {
        String nomFichier = "facture_" + facture.getReference() + ".pdf";
        String chemin = System.getProperty("user.dir") + File.separator + nomFichier;

        if (genererFacturePDF(facture, chemin)) {
            return chemin;
        }
        return null;
    }
}