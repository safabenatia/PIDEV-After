package services;

import interfaces.Services;
import models.Activite;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceActivite implements Services<Activite> {

    private Connection cnx = MyDataBase.getInstance().getCnx();

    @Override
    public void add(Activite a) {
        String sql = "INSERT INTO activite (nom, description, categorie, lieu, prix) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, a.getNom());
            ps.setString(2, a.getDescription());
            ps.setString(3, a.getCategorie());
            ps.setString(4, a.getLieu());
            ps.setDouble(5, a.getPrix());
            ps.executeUpdate();
            System.out.println("Activité ajoutée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Activite> getAll() {
        List<Activite> list = new ArrayList<>();
        String sql = "SELECT * FROM activite";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Activite a = new Activite(
                        rs.getInt("id_activite"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("categorie"),
                        rs.getString("lieu"),
                        rs.getDouble("prix")
                );
                list.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void delete(Activite a) {
        String sql = "DELETE FROM activite WHERE id_activite = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, a.getIdActivite());
            ps.executeUpdate();
            System.out.println("Activité supprimée avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Activite a) {
        String sql = "UPDATE activite SET nom=?, description=?, categorie=?, lieu=?, prix=? WHERE id_activite=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, a.getNom());
            ps.setString(2, a.getDescription());
            ps.setString(3, a.getCategorie());
            ps.setString(4, a.getLieu());
            ps.setDouble(5, a.getPrix());
            ps.setInt(6, a.getIdActivite());
            ps.executeUpdate();
            System.out.println("Activité mise à jour avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Activite getById(int id) {
        String sql = "SELECT * FROM activite WHERE id_activite = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Activite(
                        rs.getInt("id_activite"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("categorie"),
                        rs.getString("lieu"),
                        rs.getDouble("prix")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // si non trouvé
    }
    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM `activite`";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}

