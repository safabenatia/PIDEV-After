package services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import models.voyage;

import java.io.FileOutputStream;

public class PdfExportService {

    public void exportVoyage(voyage v, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            // colors
            BaseColor darkBlue = new BaseColor(22, 50, 92);
            BaseColor lightGray = new BaseColor(245, 245, 220);

            // fonts
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, darkBlue);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, darkBlue);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.DARK_GRAY);
            Font smallFont = new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY);

            // agency title
            Paragraph agency = new Paragraph("AFTER - NovaJourney", titleFont);
            agency.setAlignment(Element.ALIGN_CENTER);
            document.add(agency);

            Paragraph tagline = new Paragraph("Où vos rêves deviennent réalité", smallFont);
            tagline.setAlignment(Element.ALIGN_CENTER);
            tagline.setSpacingAfter(20);
            document.add(tagline);

            // separator line
            LineSeparator line = new LineSeparator();
            line.setLineColor(darkBlue);
            document.add(new Chunk(line));

            // ticket title
            Paragraph ticketTitle = new Paragraph("\nBON DE VOYAGE", headerFont);
            ticketTitle.setAlignment(Element.ALIGN_CENTER);
            ticketTitle.setSpacingAfter(15);
            document.add(ticketTitle);

            // voyage details table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            addTableRow(table, "Titre", v.getTitre(), darkBlue, normalFont);
            addTableRow(table, "Description", v.getDescription(), darkBlue, normalFont);
            addTableRow(table, "Date Début", v.getDateDebut().toString(), darkBlue, normalFont);
            addTableRow(table, "Date Fin", v.getDateFin().toString(), darkBlue, normalFont);
            addTableRow(table, "Prix", v.getPrix() + " TND", darkBlue, normalFont);
            addTableRow(table, "Places Disponibles", String.valueOf(v.getNbPlaces()), darkBlue, normalFont);

            document.add(table);

            // separator
            document.add(new Chunk(line));

            // footer
            Paragraph footer = new Paragraph("\nMerci de votre confiance. Bon voyage!", normalFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(15);
            document.add(footer);

            Paragraph date = new Paragraph("Généré le: " + new java.util.Date(), smallFont);
            date.setAlignment(Element.ALIGN_CENTER);
            document.add(date);

            document.close();
            System.out.println("PDF generated: " + filePath);

        } catch (Exception e) {
            System.out.println("PDF error: " + e.getMessage());
        }
    }

    private void addTableRow(PdfPTable table, String label, String value,
                             BaseColor darkBlue, Font normalFont) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(darkBlue);
        labelCell.setPadding(8);
        labelCell.setBorder(Rectangle.NO_BORDER);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", normalFont));
        valueCell.setPadding(8);
        valueCell.setBackgroundColor(new BaseColor(245, 245, 220));
        valueCell.setBorder(Rectangle.NO_BORDER);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}