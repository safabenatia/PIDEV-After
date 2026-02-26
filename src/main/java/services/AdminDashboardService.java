package services;


import models.CategorieDocument;
import utils.MyDataBase;

import java.sql.*;
import java.util.*;
import models.Document;
import models.CategorieDocument;
public class AdminDashboardService {

    Connection cnx = MyDataBase.getMyInstance().getCnx();

    // 🏆 Most Active User
    public String getMostActiveUser() {
        String query = "SELECT COUNT(*) as total FROM document";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return "Total documents: " + rs.getInt("total");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "No Data";
    }


    // 📊 Documents By Category
    public Map<String, Integer> getDocumentsByCategory() {
        Map<String, Integer> map = new HashMap<>();
        String query = """
            SELECT c.libelle, COUNT(d.id_document) AS total
            FROM document d
            JOIN categorie_document c ON d.id_categorie = c.id_categorie
            GROUP BY c.libelle
        """;
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                map.put(rs.getString("libelle"), rs.getInt("total"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    // 📈 Monthly Uploads
    public Map<String, Integer> getMonthlyUploads() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String query = """
            SELECT DATE_FORMAT(date_ajout, '%Y-%m') AS month,
                   COUNT(*) AS total
            FROM document
            GROUP BY month
            ORDER BY month
        """;
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                map.put(rs.getString("month"), rs.getInt("total"));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return map;
    }

    public int getExpiredDocumentsCount() {
        String query = "SELECT COUNT(*) FROM document WHERE date_expiration < CURDATE()";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    public int getExpiringSoonCount() {
        String query = """
            SELECT COUNT(*) FROM document
            WHERE date_expiration BETWEEN CURDATE()
            AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)
        """;
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    public List<Document> getExpiredDocuments() {
        List<Document> list = new ArrayList<>();
        String query = """
        SELECT d.*, c.libelle, c.description 
        FROM document d
        JOIN categorie_document c ON d.id_categorie = c.id_categorie
        WHERE d.date_expiration < CURDATE()
        ORDER BY d.date_expiration DESC
    """;
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                CategorieDocument cat = new CategorieDocument();
                cat.setLibelle(rs.getString("libelle"));

                Document doc = new Document();
                doc.setNomDocument(rs.getString("nom_document"));
                doc.setDateAjout(rs.getDate("date_ajout"));
                doc.setDateExpiration(rs.getDate("date_expiration"));
                doc.setCategorie(cat);

                list.add(doc);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

}