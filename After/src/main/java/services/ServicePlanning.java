package services;

import interfaces.Services;
import models.Planning;
import utils.MyDataBase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ServicePlanning implements Services<Planning> {

    Connection cnx = MyDataBase.getInstance().getCnx();

    @Override
    public void add(Planning p) {
        String sql = "INSERT INTO planning (id_user, id_activite, date_activite, heure_debut, duree) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, p.getIdUser());      // temporaire = 1
            ps.setInt(2, p.getIdActivite());
            ps.setDate(3, Date.valueOf(p.getDateActivite()));
            ps.setTime(4, Time.valueOf(p.getHeureDebut()));
            ps.setInt(5, p.getDuree());

            ps.executeUpdate();
            System.out.println("Planning ajouté avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Planning> getAll() {
        List<Planning> list = new ArrayList<>();
        String sql = "SELECT * FROM planning";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Planning p = new Planning(
                        rs.getInt("id_planning"),
                        rs.getInt("id_user"),
                        rs.getInt("id_activite"),
                        rs.getDate("date_activite").toLocalDate(),
                        rs.getTime("heure_debut").toLocalTime(),
                        rs.getInt("duree")
                );
                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void delete(Planning p) {
        String sql = "DELETE FROM planning WHERE id_planning = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, p.getIdPlanning());
            ps.executeUpdate();
            System.out.println("Planning supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Planning p) {
        String sql = "UPDATE planning SET id_activite=?, date_activite=?, heure_debut=?, duree=? WHERE id_planning=?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, p.getIdActivite());
            ps.setDate(2, Date.valueOf(p.getDateActivite()));
            ps.setTime(3, Time.valueOf(p.getHeureDebut()));
            ps.setInt(4, p.getDuree());
            ps.setInt(5, p.getIdPlanning());

            ps.executeUpdate();
            System.out.println("Planning mis à jour avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM `planning`";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}

