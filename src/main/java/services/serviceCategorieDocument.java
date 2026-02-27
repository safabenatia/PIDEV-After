package services;

import interfaces.service;
import javafx.beans.property.Property;
import models.CategorieDocument;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceCategorieDocument implements service<CategorieDocument> {

    private Connection cnx;

    public serviceCategorieDocument() {
        this.cnx = MyDataBase.getMyInstance().getCnx();
    }

    // ================== ADD ==================
    @Override
    public void add(CategorieDocument categorieDocument) {

        String sql = "INSERT INTO categorie_document (libelle, description) VALUES (?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, categorieDocument.getLibelle());
            ps.setString(2, categorieDocument.getDescription());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public CategorieDocument findByLibelle(String libelle) {

        String sql = "SELECT * FROM categorie_document WHERE libelle = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, libelle);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                CategorieDocument cat = new CategorieDocument();
                cat.setIdCategorie(rs.getInt("id_categorie"));
                cat.setLibelle(rs.getString("libelle"));
                cat.setDescription(rs.getString("description"));
                return cat;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public CategorieDocument getOrCreateByLibelle(String libelle) {

        // vérifier si existe déjà
        CategorieDocument cat = findByLibelle(libelle);

        if (cat != null) {
            return cat;
        }

        // sinon créer
        CategorieDocument newCat = new CategorieDocument();
        newCat.setLibelle(libelle);
        newCat.setDescription("Créée automatiquement");

        add(newCat);

        // récupérer la catégorie créée avec ID
        return findByLibelle(libelle);
    }

    // ================== GET ALL ==================
    @Override
    public List<CategorieDocument> getAll() {

        List<CategorieDocument> categories = new ArrayList<>();

        String sql = "SELECT * FROM categorie_document";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                CategorieDocument cat = new CategorieDocument();
                cat.setIdCategorie(rs.getInt("id_categorie"));
                cat.setLibelle(rs.getString("libelle"));
                cat.setDescription(rs.getString("description"));

                categories.add(cat);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return categories;
    }

    // ================== UPDATE ==================
    @Override
    public void update(CategorieDocument categorieDocument) {

        String sql = "UPDATE categorie_document SET libelle=?, description=? WHERE id_categorie=?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setString(1, categorieDocument.getLibelle());
            ps.setString(2, categorieDocument.getDescription());
            ps.setInt(3, categorieDocument.getIdCategorie());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================== DELETE ==================
    @Override
    public void delete(CategorieDocument categorieDocument) {

        String sql = "DELETE FROM categorie_document WHERE id_categorie=?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, categorieDocument.getIdCategorie());
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================== DELETE ALL (POUR TESTS) ==================
    public void deleteAll() throws SQLException {

        String sql = "DELETE FROM categorie_document";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
    // Vérifie si une catégorie existe déjà par son libellé
    public boolean existsByLibelle(String libelle) {
        String sql = "SELECT COUNT(*) FROM categorie_document WHERE libelle = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, libelle);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // Vérifie si une catégorie est utilisée dans la table document
    public boolean isUsed(int idCategorie) {
        String sql = "SELECT COUNT(*) FROM document WHERE id_categorie = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, idCategorie);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // ================== GET ALL NAMES ==================
    public List<String> getAllNames() {

        List<String> list = new ArrayList<>();

        for (CategorieDocument cat : getAll()) {
            list.add(cat.getLibelle());
        }

        return list;
    }
    // ================== AJOUTER SI INEXISTANTE ==================
    public void ajouterSiInexistante(String libelle) {

        if (libelle == null || libelle.trim().isEmpty()) return;

        if (!existsByLibelle(libelle)) {
            CategorieDocument cat = new CategorieDocument();
            cat.setLibelle(libelle);
            cat.setDescription("Ajoutée depuis dashboard");
            add(cat);
        }
    }

    // ================== MODIFIER PAR LIBELLE ==================
    public void modifierNom(String ancienLibelle, String nouveauLibelle) {

        if (ancienLibelle == null || nouveauLibelle == null) return;

        CategorieDocument cat = findByLibelle(ancienLibelle);
        if (cat == null) return;

        cat.setLibelle(nouveauLibelle);
        update(cat);
    }

    // ================== SUPPRIMER PAR LIBELLE (SAFE) ==================
    public boolean supprimerParLibelle(String libelle) {

        CategorieDocument cat = findByLibelle(libelle);

        if (cat == null) return false;

        if (isUsed(cat.getIdCategorie())) {
            System.out.println("Impossible : catégorie utilisée");
            return false;
        }

        delete(cat);
        return true;
    }
    public void supprimerDocumentsExpires() {

        String sql = "DELETE FROM document WHERE date_expiration < CURDATE()";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



}
