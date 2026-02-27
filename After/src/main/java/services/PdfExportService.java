package services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import models.voyage;
import models.Activite;

import java.io.FileOutputStream;

public class PdfExportService {

    // ================= VOYAGE PDF =================
    public void exportVoyage(voyage v, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            BaseColor darkBlue = new BaseColor(22, 50, 92);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            addSimpleRow(table, "Titre", v.getTitre());
            addSimpleRow(table, "Prix", v.getPrix() + " TND");

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= ACTIVITE PDF =================
    public void exportActivite(Activite a, String filePath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            addSimpleRow(table, "Nom", a.getNom());
            addSimpleRow(table, "Prix", a.getPrix() + " DT");

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SHARED METHOD =================
    private void addSimpleRow(PdfPTable table, String label, String value) {
        table.addCell(new PdfPCell(new Phrase(label)));
        table.addCell(new PdfPCell(new Phrase(value != null ? value : "N/A")));
    }
}