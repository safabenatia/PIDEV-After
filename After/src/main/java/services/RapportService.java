package services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import utils.MyDataBase;

public class RapportService {

    private Connection cnx;

    public RapportService() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    public Map<String, double[]> getDonneesCategories(int mois, int annee) {
        Map<String, double[]> data = new LinkedHashMap<>();
        YearMonth yearMonth = YearMonth.of(annee, mois);
        LocalDate dateDebut = yearMonth.atDay(1);
        LocalDate dateFin = yearMonth.atEndOfMonth();

        String req = "SELECT c.nom_categorie, "
                + "COALESCE(SUM(d.montant), 0) as total, "
                + "COUNT(d.id_dep) as nb "
                + "FROM categorie c "
                + "LEFT JOIN depense d ON c.id_cat = d.id_categorie "
                + "AND d.date_depense >= '" + dateDebut + "' "
                + "AND d.date_depense <= '" + dateFin + "' "
                + "GROUP BY c.id_cat, c.nom_categorie "
                + "ORDER BY total DESC";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                data.put(rs.getString("nom_categorie"), new double[]{rs.getDouble("total"), rs.getInt("nb")});
            }
        } catch (SQLException e) {
            System.err.println("Erreur getDonneesCategories: " + e.getMessage());
        }
        return data;
    }

    public String genererRapportMoisCourant() {
        LocalDate now = LocalDate.now();
        return genererRapportMensuelComplet(now.getMonthValue(), now.getYear());
    }

    public String genererRapportMensuelComplet(int mois, int annee) {
        StringBuilder rapport = new StringBuilder();
        rapport.append("================================================================================\n");
        rapport.append("           RAPPORT MENSUEL - AFTER TRAVEL\n");
        rapport.append("================================================================================\n");
        rapport.append(String.format("Periode: %02d/%d\n", mois, annee));
        rapport.append("Date: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        rapport.append("================================================================================\n\n");

        Map<String, double[]> data = getDonneesCategories(mois, annee);
        double totalGeneral = data.values().stream().mapToDouble(v -> v[0]).sum();
        int totalNb = (int) data.values().stream().mapToDouble(v -> v[1]).sum();

        rapport.append("REPARTITION PAR CATEGORIE:\n");
        rapport.append("--------------------------------------------------------------------------------\n");
        rapport.append(String.format("%-25s | %15s | %8s | %12s\n", "CATEGORIE", "MONTANT (DT)", "NB DEP.", "POURCENTAGE"));
        rapport.append("--------------------------------------------------------------------------------\n");

        for (Map.Entry<String, double[]> entry : data.entrySet()) {
            double montant = entry.getValue()[0];
            int nb = (int) entry.getValue()[1];
            double pct = totalGeneral > 0 ? (montant / totalGeneral) * 100 : 0;
            rapport.append(String.format("%-25s | %15.2f | %8d | %11.1f%%\n", entry.getKey(), montant, nb, pct));
        }

        rapport.append("--------------------------------------------------------------------------------\n");
        rapport.append(String.format("%-25s | %15.2f | %8d | %11.1f%%\n", "TOTAL GENERAL", totalGeneral, totalNb, 100.0));
        rapport.append("\n");
        rapport.append("RESUME:\n");
        rapport.append("----------------------------------------\n");
        rapport.append("Nombre total de depenses : ").append(totalNb).append("\n");
        rapport.append("Montant total            : ").append(String.format("%.2f DT", totalGeneral)).append("\n");
        if (totalNb > 0) {
            rapport.append("Depense moyenne          : ").append(String.format("%.2f DT", totalGeneral / totalNb)).append("\n");
        }
        data.entrySet().stream()
                .max(Map.Entry.comparingByValue((a, b) -> Double.compare(a[0], b[0])))
                .ifPresent(e -> rapport.append("Categorie la plus depensiere : ")
                        .append(e.getKey()).append(String.format(" (%.2f DT)\n", e.getValue()[0])));

        rapport.append("================================================================================\n");
        return rapport.toString();
    }

    public String exporterRapportPDF(int mois, int annee, String cheminFichier) {
        try {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(cheminFichier));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.HELVETICA, 11);
            Font boldFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD);
            Font whiteFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
            BaseColor navyColor = new BaseColor(22, 50, 92);

            Paragraph title = new Paragraph("RAPPORT MENSUEL - AFTER TRAVEL", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(String.format("Periode: %02d/%d", mois, annee), normalFont));
            document.add(new Paragraph("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), normalFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            Map<String, double[]> data = getDonneesCategories(mois, annee);
            double totalGeneral = data.values().stream().mapToDouble(v -> v[0]).sum();
            int totalNb = (int) data.values().stream().mapToDouble(v -> v[1]).sum();

            document.add(new Paragraph("REPARTITION PAR CATEGORIE", headerFont));
            document.add(new Paragraph(" "));

            PdfPTable tableCat = new PdfPTable(4);
            tableCat.setWidthPercentage(100);
            tableCat.setWidths(new int[]{4, 3, 2, 2});

            for (String h : new String[]{"Categorie", "Montant (DT)", "Nb depenses", "Pourcentage"}) {
                PdfPCell c = new PdfPCell(new Phrase(h, whiteFont));
                c.setBackgroundColor(navyColor);
                c.setPadding(7);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCat.addCell(c);
            }

            DefaultPieDataset dataset = new DefaultPieDataset();

            for (Map.Entry<String, double[]> entry : data.entrySet()) {
                String cat = entry.getKey();
                double montant = entry.getValue()[0];
                int nb = (int) entry.getValue()[1];
                double pct = totalGeneral > 0 ? (montant / totalGeneral) * 100 : 0;

                tableCat.addCell(new Phrase(cat, normalFont));
                PdfPCell mc = new PdfPCell(new Phrase(String.format("%.2f", montant), normalFont));
                mc.setHorizontalAlignment(Element.ALIGN_RIGHT);
                tableCat.addCell(mc);
                PdfPCell nc = new PdfPCell(new Phrase(String.valueOf(nb), normalFont));
                nc.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCat.addCell(nc);
                PdfPCell pc = new PdfPCell(new Phrase(String.format("%.1f%%", pct), normalFont));
                pc.setHorizontalAlignment(Element.ALIGN_CENTER);
                tableCat.addCell(pc);

                if (montant > 0) dataset.setValue(cat, montant);
            }

            PdfPCell tl = new PdfPCell(new Phrase("TOTAL GENERAL", boldFont));
            tl.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tableCat.addCell(tl);
            PdfPCell tm = new PdfPCell(new Phrase(String.format("%.2f", totalGeneral), boldFont));
            tm.setHorizontalAlignment(Element.ALIGN_RIGHT);
            tm.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tableCat.addCell(tm);
            PdfPCell tn = new PdfPCell(new Phrase(String.valueOf(totalNb), boldFont));
            tn.setHorizontalAlignment(Element.ALIGN_CENTER);
            tn.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tableCat.addCell(tn);
            PdfPCell tp = new PdfPCell(new Phrase("100.0%", boldFont));
            tp.setHorizontalAlignment(Element.ALIGN_CENTER);
            tp.setBackgroundColor(BaseColor.LIGHT_GRAY);
            tableCat.addCell(tp);

            document.add(tableCat);

            if (dataset.getItemCount() > 0) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph(" "));
                document.add(new Paragraph("REPARTITION GRAPHIQUE:", headerFont));
                document.add(new Paragraph(" "));

                JFreeChart chart = ChartFactory.createPieChart(
                        "Depenses par categorie - " + String.format("%02d/%d", mois, annee),
                        dataset, true, true, false);

                String tempChart = System.getProperty("java.io.tmpdir") + "/chart_temp.png";
                ChartUtils.saveChartAsPNG(new File(tempChart), chart, 500, 380);
                Image chartImage = Image.getInstance(tempChart);
                chartImage.scaleToFit(450, 350);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
                new File(tempChart).delete();
            }

            document.close();
            return cheminFichier;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}