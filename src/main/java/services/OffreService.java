package services;

import interfaces.Services;
import models.Offre;
import utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreService implements Services<Offre> {

    private Connection cnx;

    public OffreService() {
        cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Offre offre) {

        String req = "INSERT INTO offre (titre, prix, duree, id_service) VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setString(1, offre.getTitre());
            ps.setDouble(2, offre.getPrix());
            ps.setInt(3, offre.getDuree());
            ps.setInt(4, offre.getId_service());

            ps.executeUpdate();
            System.out.println("Offre ajoutée avec succès");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Offre> getAll() {

        List<Offre> offres = new ArrayList<>();
        String req = "SELECT * FROM offre";

        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Offre offre = new Offre(
                        rs.getInt("id_offre"),
                        rs.getString("titre"),
                        rs.getDouble("prix"),
                        rs.getInt("duree"),
                        rs.getInt("id_service")
                );

                offres.add(offre);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return offres;
    }

    @Override
    public void update(Offre offre) {

        String req = "UPDATE offre SET titre=?, prix=?, duree=?, id_service=? WHERE id_offre=?";

        try {
            PreparedStatement ps = cnx.prepareStatement(req);

            ps.setString(1, offre.getTitre());
            ps.setDouble(2, offre.getPrix());
            ps.setInt(3, offre.getDuree());
            ps.setInt(4, offre.getId_service());
            ps.setInt(5, offre.getId_offre());

            ps.executeUpdate();
            System.out.println("Offre modifiée avec succès");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(Offre offre) {

        String req = "DELETE FROM offre WHERE id_offre=?";

        try {
            PreparedStatement ps = cnx.prepareStatement(req);
            ps.setInt(1, offre.getId_offre());
            ps.executeUpdate();

            System.out.println("Offre supprimée avec succès");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
