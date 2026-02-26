package api;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import models.Offre;

import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class QRCodeAPI {

    private static final int SIZE = 300;
    private static final String COLOR_PRIMARY = "#16325c";
    private static final String COLOR_SECONDARY = "#F5F5DC";

    public Image genererQRCodeFX(String texte) {
        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrWriter.encode(texte, BarcodeFormat.QR_CODE, SIZE, SIZE);

            WritableImage image = new WritableImage(SIZE, SIZE);
            PixelWriter pixelWriter = image.getPixelWriter();

            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    if (bitMatrix.get(x, y)) {
                        pixelWriter.setColor(x, y, javafx.scene.paint.Color.web(COLOR_PRIMARY));
                    } else {
                        pixelWriter.setColor(x, y, javafx.scene.paint.Color.web(COLOR_SECONDARY));
                    }
                }
            }
            return image;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean sauvegarderQRCode(String texte, String chemin) {
        try {
            QRCodeWriter qrWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrWriter.encode(texte, BarcodeFormat.QR_CODE, SIZE, SIZE);

            BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < SIZE; x++) {
                for (int y = 0; y < SIZE; y++) {
                    int color = bitMatrix.get(x, y) ? 0xFF16325c : 0xFFF5F5DC;
                    image.setRGB(x, y, color);
                }
            }

            File output = new File(chemin);
            ImageIO.write(image, "png", output);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String formaterOffrePourQR(Offre offre, String serviceName) {
        return "🏨 AFTER TRAVEL 🏨\n" +
                "━━━━━━━━━━━━━━━━\n" +
                "Offre: " + offre.getTitre() + "\n" +        // ← CORRIGÉ : getTitre()
                "Prix: " + offre.getPrix() + " DT\n" +
                "Durée: " + offre.getDuree() + " jours\n" +  // ← CORRIGÉ : getDuree()
                "Service: " + serviceName + "\n" +
                "━━━━━━━━━━━━━━━━\n" +
                "Scannez pour réserver!\n" +
                "📱 ID: " + offre.getId_offre();
    }
}