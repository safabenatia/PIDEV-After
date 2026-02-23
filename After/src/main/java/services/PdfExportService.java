package services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import models.Activite;

import java.io.FileOutputStream;

public class PdfExportService {

    public void exportActivite(Activite a, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // Couleurs
            BaseColor headerBlue = new BaseColor(0, 102, 204);
            BaseColor lightGray = new BaseColor(245, 245, 245);
            BaseColor darkGray = BaseColor.DARK_GRAY;

            // Polices
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, headerBlue);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, headerBlue);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, darkGray);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, darkGray);

            // Titre principal
            Paragraph title = new Paragraph("DÉTAIL DE L'ACTIVITÉ", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            // Ligne séparatrice
            LineSeparator line = new LineSeparator();
            line.setLineColor(headerBlue);
            document.add(new Chunk(line));

            // Tableau des détails
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            addTableRow(table, "Nom", a.getNom(), headerBlue, normalFont, lightGray);
            addTableRow(table, "Catégorie", a.getCategorie(), headerBlue, normalFont, lightGray);
            addTableRow(table, "Lieu", a.getLieu(), headerBlue, normalFont, lightGray);
            addTableRow(table, "Prix", a.getPrix() + " DT", headerBlue, normalFont, lightGray);
            addTableRow(table, "Description", a.getDescription(), headerBlue, normalFont, lightGray);

            document.add(table);

            // Footer
            document.add(new Chunk(line));
            Paragraph footer = new Paragraph("Merci d'utiliser notre application pour gérer vos activités !", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(10);
            document.add(footer);

            Paragraph date = new Paragraph("Généré le: " + new java.util.Date(), smallFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);

            document.close();
            System.out.println("PDF généré avec succès : " + filePath);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur PDF: " + e.getMessage());
        }
    }

    private void addTableRow(PdfPTable table, String label, String value,
                             BaseColor headerColor, Font normalFont, BaseColor rowColor) {

        Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(headerColor);
        labelCell.setPadding(6);
        labelCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", normalFont));
        valueCell.setBackgroundColor(rowColor);
        valueCell.setPadding(6);
        valueCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}