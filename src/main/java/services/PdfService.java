package services;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import models.Document;

import java.util.List;

public class PdfService {

    // 🎨 Couleurs thème AFTER
    private final DeviceRgb primary = new DeviceRgb(30, 60, 114);
    private final DeviceRgb accent = new DeviceRgb(42, 157, 143);
    private final DeviceRgb light = new DeviceRgb(245, 247, 250);

    // ==========================================
    // EXPORT 1 DOCUMENT
    // ==========================================
    public void exporterDocumentPDF(models.Document doc, String path) {

        try {
            PdfWriter writer = new PdfWriter(path);
            PdfDocument pdf = new PdfDocument(writer);
            com.itextpdf.layout.Document document =
                    new com.itextpdf.layout.Document(pdf);

            ajouterHeader(document, "Document de voyage");

            Table table = new Table(UnitValue.createPercentArray(2))
                    .useAllAvailableWidth();

            table.addCell(label("Nom du document"));
            table.addCell(value(doc.getNomDocument()));

            table.addCell(label("Chemin fichier"));
            table.addCell(value(doc.getCheminFichier()));

            table.addCell(label("Date ajout"));
            table.addCell(value(String.valueOf(doc.getDateAjout())));

            table.addCell(label("Date expiration"));
            table.addCell(value(
                    doc.getDateExpiration() != null
                            ? doc.getDateExpiration().toString()
                            : "Aucune"
            ));

            table.addCell(label("Catégorie"));
            table.addCell(value(doc.getCategorie().getLibelle()));

            document.add(table);

            ajouterFooter(document);
            document.close();

            System.out.println("PDF document AFTER généré ✔");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // EXPORT LISTE DOCUMENTS
    // ==========================================
    public void exporterListePDF(List<models.Document> docs, String path) {

        try {
            PdfWriter writer = new PdfWriter(path);
            PdfDocument pdf = new PdfDocument(writer);
            com.itextpdf.layout.Document document =
                    new com.itextpdf.layout.Document(pdf);

            ajouterHeader(document, "Liste des documents");

            Table table = new Table(UnitValue.createPercentArray(
                    new float[]{3, 4, 2, 2, 2}))
                    .useAllAvailableWidth();

            table.addHeaderCell(header("Nom"));
            table.addHeaderCell(header("Chemin"));
            table.addHeaderCell(header("Ajout"));
            table.addHeaderCell(header("Expiration"));
            table.addHeaderCell(header("Catégorie"));

            for (models.Document d : docs) {
                table.addCell(value(d.getNomDocument()));
                table.addCell(value(d.getCheminFichier()));
                table.addCell(value(String.valueOf(d.getDateAjout())));
                table.addCell(value(
                        d.getDateExpiration() != null
                                ? d.getDateExpiration().toString()
                                : "-"
                ));
                table.addCell(value(d.getCategorie().getLibelle()));
            }

            document.add(table);
            ajouterFooter(document);
            document.close();

            System.out.println("PDF liste AFTER généré ✔");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // UI PDF AFTER
    // ==========================================

    private void ajouterHeader(com.itextpdf.layout.Document document, String sousTitre) {

        Paragraph logo = new Paragraph("✈ AFTER Travel")
                .setFontSize(24)
                .setBold()
                .setFontColor(primary)
                .setTextAlignment(TextAlignment.CENTER);

        Paragraph subtitle = new Paragraph(sousTitre)
                .setFontSize(14)
                .setFontColor(accent)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(logo);
        document.add(subtitle);
        document.add(new Paragraph("\n"));
    }

    private void ajouterFooter(com.itextpdf.layout.Document document) {

        document.add(new Paragraph("\n"));

        Paragraph footer = new Paragraph(
                "AFTER Travel — Voyager après un événement de vie")
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY);

        document.add(footer);
    }

    private Cell label(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(light);
    }

    private Cell value(String text) {
        return new Cell()
                .add(new Paragraph(text));
    }

    private Cell header(String text) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(primary)
                .setTextAlignment(TextAlignment.CENTER);
    }

    public void exporterListeDocumentsPDF(List<Document> documents, String absolutePath) {
    }
}