package service;

import interfaces.service;
import models.Categorie;
import utils.mydb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements service<Categorie> {

    private Connection cnx;

    public CategorieService() {
        this.cnx = mydb.getInstance().getCnx();
    }

    @Override
    public void add(Categorie categorie) {
        String req = "INSERT INTO `categorie`(`nom_categorie`, `description`, `icone_url`) VALUES ('"
                + categorie.getNomCategorie() + "', '"
                + categorie.getDescription() + "', '"
                + categorie.getIconeUrl() + "')";

        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Catégorie ajoutée !");
        } catch (SQLException e) {
            System.out.println("Erreur add: " + e.getMessage());
        }
    }

    @Override
    public List<Categorie> getAll() {
        List<Categorie> categories = new ArrayList<>();
        String req = "SELECT * FROM categorie";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                Categorie c = new Categorie();
                c.setIdCat(rs.getInt("id_cat"));
                c.setNomCategorie(rs.getString("nom_categorie"));
                c.setDescription(rs.getString("description"));
                c.setIconeUrl(rs.getString("icone_url"));
                categories.add(c);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll: " + e.getMessage());
        }
        return categories;
    }

    @Override
    public void update(Categorie categorie) {
        String req = "UPDATE `categorie` SET `nom_categorie`='" + categorie.getNomCategorie()
                + "', `description`='" + categorie.getDescription()
                + "', `icone_url`='" + categorie.getIconeUrl()
                + "' WHERE `id_cat`=" + categorie.getIdCat();
        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Catégorie mise à jour !");
        } catch (SQLException e) {
            System.out.println("Erreur update: " + e.getMessage());
        }
    }

    @Override
    public void delete(Categorie categorie) {
        String req = "DELETE FROM `categorie` WHERE `id_cat`=" + categorie.getIdCat();
        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Catégorie supprimée !");
        } catch (SQLException e) {
            System.out.println("Erreur delete: " + e.getMessage());
        }
    }
    
    public Categorie getById(int id) {
        String req = "SELECT * FROM categorie WHERE id_cat = " + id;
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            if (rs.next()) {
                Categorie c = new Categorie();
                c.setIdCat(rs.getInt("id_cat"));
                c.setNomCategorie(rs.getString("nom_categorie"));
                c.setDescription(rs.getString("description"));
                c.setIconeUrl(rs.getString("icone_url"));
                return c;
            }
        } catch (SQLException e) {
            System.out.println("Erreur getById: " + e.getMessage());
        }
        return null;
    }
}
