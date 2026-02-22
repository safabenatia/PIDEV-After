package services;

import interfaces.service;
import models.CategorieDocument;
import models.Document;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceDocument implements service<Document> {

    private Connection cnx;

    public serviceDocument() {
        this.cnx = MyDataBase.getMyInstance().getCnx();
    }

    @Override
    public void add(Document document) {
        String sql = "INSERT INTO document (nom_document, chemin_fichier, date_ajout, date_expiration, id_categorie) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, document.getNomDocument());
            ps.setString(2, document.getCheminFichier());
            ps.setDate(3, document.getDateAjout());
            ps.setDate(4, document.getDateExpiration());
            ps.setInt(5, document.getCategorie().getIdCategorie());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Document> getAll() {
        List<Document> documents = new ArrayList<>();
        String sql = "SELECT d.*, c.libelle, c.description FROM document d JOIN categorie_document c ON d.id_categorie = c.id_categorie";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                CategorieDocument cat = new CategorieDocument();
                cat.setIdCategorie(rs.getInt("id_categorie"));
                cat.setLibelle(rs.getString("libelle"));
                cat.setDescription(rs.getString("description"));

                Document doc = new Document();
                doc.setIdDocument(rs.getInt("id_document"));
                doc.setNomDocument(rs.getString("nom_document"));
                doc.setCheminFichier(rs.getString("chemin_fichier"));
                doc.setDateAjout(rs.getDate("date_ajout"));
                doc.setDateExpiration(rs.getDate("date_expiration"));
                doc.setCategorie(cat);

                documents.add(doc);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return documents;
    }
    public List<Document> getByCategorie(int idCategorie) {

        List<Document> list = new ArrayList<>();

        String req = """
        SELECT d.*, c.libelle, c.description
        FROM document d
        JOIN categorie_document c ON d.id_categorie = c.id_categorie
        WHERE c.id_categorie = ?
    """;

        try (PreparedStatement ps = cnx.prepareStatement(req)) {

            ps.setInt(1, idCategorie);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                CategorieDocument cat = new CategorieDocument();
                cat.setIdCategorie(rs.getInt("id_categorie"));
                cat.setLibelle(rs.getString("libelle"));
                cat.setDescription(rs.getString("description"));

                Document d = new Document();
                d.setIdDocument(rs.getInt("id_document"));
                d.setNomDocument(rs.getString("nom_document"));
                d.setCheminFichier(rs.getString("chemin_fichier"));
                d.setDateAjout(rs.getDate("date_ajout"));
                d.setDateExpiration(rs.getDate("date_expiration"));
                d.setCategorie(cat);

                list.add(d);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    @Override
    public void update(Document document) {
        String sql = "UPDATE document SET nom_document=?, chemin_fichier=?, date_ajout=?, date_expiration=?, id_categorie=? WHERE id_document=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, document.getNomDocument());
            ps.setString(2, document.getCheminFichier());
            ps.setDate(3, document.getDateAjout());
            ps.setDate(4, document.getDateExpiration());
            ps.setInt(5, document.getCategorie().getIdCategorie());
            ps.setInt(6, document.getIdDocument());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Document document) {
        String sql = "DELETE FROM document WHERE id_document=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, document.getIdDocument());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================== Méthode GET BY ID ==================
    public Document getById(int id) {
        String sql = "SELECT d.*, c.libelle, c.description FROM document d JOIN categorie_document c ON d.id_categorie = c.id_categorie WHERE d.id_document=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CategorieDocument cat = new CategorieDocument();
                cat.setIdCategorie(rs.getInt("id_categorie"));
                cat.setLibelle(rs.getString("libelle"));
                cat.setDescription(rs.getString("description"));

                Document doc = new Document();
                doc.setIdDocument(rs.getInt("id_document"));
                doc.setNomDocument(rs.getString("nom_document"));
                doc.setCheminFichier(rs.getString("chemin_fichier"));
                doc.setDateAjout(rs.getDate("date_ajout"));
                doc.setDateExpiration(rs.getDate("date_expiration"));
                doc.setCategorie(cat);

                return doc;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM document";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}
