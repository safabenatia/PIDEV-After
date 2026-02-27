package services;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import model.Paiement;
import model.Reservation;
import services.QrCodeService;
import java.io.File;

public class PdfService {

        private final QrCodeService qrService = new QrCodeService();

        // Theme Colors
        private static final DeviceRgb NAVY = new DeviceRgb(0, 80, 130);
        private static final DeviceRgb BEIGE = new DeviceRgb(167, 146, 119);

        public String generateReceipt(Reservation res, Paiement p) throws Exception {
                String folderPath = "receipts";
                File folder = new File(folderPath);
                if (!folder.exists())
                        folder.mkdirs();

                String fileName = folderPath + "/Recu_" + p.getReference() + ".pdf";
                PdfWriter writer = new PdfWriter(fileName);
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                document.setMargins(30, 40, 30, 40);

                // --- Header Section ---
                Table headerTable = new Table(UnitValue.createPercentArray(new float[] { 70, 30 }))
                                .useAllAvailableWidth();
                headerTable.addCell(new Cell().add(new Paragraph("AFTER TRAVEL")
                                .setFontColor(NAVY)
                                .setBold()
                                .setFontSize(26))
                                .setBorder(Border.NO_BORDER));

                headerTable.addCell(new Cell().add(new Paragraph("REÇU OFFICIEL")
                                .setFontColor(BEIGE)
                                .setBold()
                                .setFontSize(14)
                                .setTextAlignment(TextAlignment.RIGHT))
                                .setBorder(Border.NO_BORDER));

                document.add(headerTable);

                document.add(new Paragraph("Réf: " + p.getReference())
                                .setFontSize(10)
                                .setFontColor(ColorConstants.GRAY)
                                .setTextAlignment(TextAlignment.RIGHT)
                                .setMarginBottom(20));

                // --- Customer & Trip Info ---
                Table tripTable = new Table(UnitValue.createPercentArray(new float[] { 1, 1 })).useAllAvailableWidth();

                tripTable.addCell(createStyledCell("DÉTAILS DU VOYAGE", true));
                tripTable.addCell(createStyledCell("DÉTAILS DU PAIEMENT", true));

                tripTable.addCell(createStyledCell(
                                "Type: " + res.getType() + "\nLieu: " + res.getLieu() + "\nDate: "
                                                + res.getDateReservation(),
                                false));
                tripTable.addCell(createStyledCell(
                                "Méthode: " + p.getMethode() + "\nDate: " + p.getDatePaiement().toLocalDate()
                                                + "\nStatut: " + p.getStatut().toUpperCase(),
                                false));

                document.add(tripTable.setMarginBottom(30));

                // --- Billing Table ---
                Table billTable = new Table(UnitValue.createPercentArray(new float[] { 60, 20, 20 }))
                                .useAllAvailableWidth();

                // Header
                billTable.addHeaderCell(createBillHeaderCell("Description"));
                billTable.addHeaderCell(createBillHeaderCell("Qté"));
                billTable.addHeaderCell(createBillHeaderCell("Total"));

                // Body
                billTable.addCell(
                                new Cell().add(new Paragraph(
                                                "Réservation Voyage #" + res.getId() + " (" + res.getType() + ")"))
                                                .setBorder(Border.NO_BORDER).setPadding(10));
                billTable.addCell(new Cell().add(new Paragraph("1")).setTextAlignment(TextAlignment.CENTER)
                                .setBorder(Border.NO_BORDER).setPadding(10));
                billTable.addCell(new Cell().add(new Paragraph(String.format("%.2f %s", p.getMontant(), p.getDevise())))
                                .setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER).setPadding(10));

                document.add(billTable);

                // --- Total ---
                Table totalTable = new Table(UnitValue.createPercentArray(new float[] { 60, 40 }))
                                .useAllAvailableWidth();
                totalTable.addCell(new Cell().setBorder(Border.NO_BORDER));
                Cell totalCell = new Cell()
                                .add(new Paragraph("TOTAL PAYÉ: "
                                                + String.format("%.2f %s", p.getMontant(), p.getDevise()))
                                                .setBold()
                                                .setFontSize(14)
                                                .setFontColor(ColorConstants.WHITE))
                                .setBackgroundColor(NAVY)
                                .setPadding(10)
                                .setTextAlignment(TextAlignment.CENTER);
                totalTable.addCell(totalCell);

                document.add(totalTable.setMarginTop(20).setMarginBottom(40));

                // --- Footer with QR ---
                String qrText = String.format("AFTER TRAVEL | Ref:%s | ResID:%d | Montant:%.2f",
                                p.getReference(), res.getId(), p.getMontant());
                byte[] qrImage = qrService.generateQRCode(qrText, 120, 120);
                Image img = new Image(ImageDataFactory.create(qrImage));
                img.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.CENTER);

                document.add(new Paragraph("Scannez pour vérifier votre reçu")
                                .setFontSize(9)
                                .setFontColor(BEIGE)
                                .setTextAlignment(TextAlignment.CENTER));
                document.add(img);

                document.add(new Paragraph("\nMerci pour votre confiance !\nBon voyage avec After Travel.")
                                .setTextAlignment(TextAlignment.CENTER)
                                .setFontSize(10)
                                .setItalic()
                                .setMarginTop(20));

                document.close();
                return new File(fileName).getAbsolutePath();
        }

        private Cell createStyledCell(String text, boolean isHeader) {
                Cell cell = new Cell();
                Paragraph p = new Paragraph(text)
                                .setFontSize(isHeader ? 11 : 10)
                                .setFontColor(isHeader ? NAVY : ColorConstants.BLACK);
                if (isHeader)
                        p.setBold();
                cell.add(p);
                cell.setBorder(Border.NO_BORDER);
                if (isHeader)
                        cell.setPaddingBottom(5);
                return cell;
        }

        private Cell createBillHeaderCell(String text) {
                return new Cell().add(new Paragraph(text).setBold().setFontColor(ColorConstants.WHITE))
                                .setBackgroundColor(BEIGE)
                                .setPadding(8)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setBorder(Border.NO_BORDER);
        }
}
