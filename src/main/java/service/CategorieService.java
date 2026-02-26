package service;

import interfaces.service;
import models.Categorie;
import utils.mydb;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        String req = "INSERT INTO `categorie`(`nom_categorie`, `description`, `icone_url`) VALUES (?, ?, ?)";
        try {
            PreparedStatement pstmt = cnx.prepareStatement(req);
            pstmt.setString(1, categorie.getNomCategorie());
            pstmt.setString(2, categorie.getDescription());
            pstmt.setString(3, categorie.getIconeUrl());
            pstmt.executeUpdate();
            System.out.println("Categorie ajoutee ! Chemin image: " + categorie.getIconeUrl());
        } catch (SQLException e) {
            System.err.println("Erreur add categorie: " + e.getMessage());
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
        String req = "UPDATE `categorie` SET `nom_categorie`=?, `description`=?, `icone_url`=? WHERE `id_cat`=?";
        try {
            PreparedStatement pstmt = cnx.prepareStatement(req);
            pstmt.setString(1, categorie.getNomCategorie());
            pstmt.setString(2, categorie.getDescription());
            pstmt.setString(3, categorie.getIconeUrl());
            pstmt.setInt(4, categorie.getIdCat());
            pstmt.executeUpdate();
            System.out.println("Categorie mise a jour ! Chemin image: " + categorie.getIconeUrl());
        } catch (SQLException e) {
            System.err.println("Erreur update categorie: " + e.getMessage());
        }
    }

    @Override
    public void delete(Categorie categorie) {
        String req = "DELETE FROM `categorie` WHERE `id_cat`=" + categorie.getIdCat();
        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Categorie supprimee !");
        } catch (SQLException e) {
            System.out.println("Erreur delete: " + e.getMessage());
        }
    }

    public Categorie getById(int idCat) {
        String req = "SELECT * FROM categorie WHERE id_cat = " + idCat;
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