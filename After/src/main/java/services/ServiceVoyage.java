package services;

import interfaces.Services;
import models.voyage;
import utils.Mydatabase;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class ServiceVoyage implements Services <voyage>{
    private Connection cnx;
    public ServiceVoyage() {
        this.cnx = Mydatabase.getInstance().getCnx();
    }
    @Override
    public void add(voyage voyage) {
        String req = "INSERT INTO `voyage`(`titre`, `description`, `date_debut`, `date_fin`, `prix`, `nb_places`, `image`, `id_destination`) VALUES ('"+voyage.getTitre()+"','"+voyage.getDescription()+"','"+voyage.getDateDebut()+"','"+voyage.getDateFin()+"'," + voyage.getPrix() + ","+voyage.getNbPlaces()+",'"+voyage.getImage()+"',"+voyage.getIdDestination()+")";
        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
        } catch (SQLException e) {
            System.out.println( e.getMessage());
        }
    }

    @Override
    public List<voyage> getAll() {
        List<voyage> voyages = new ArrayList<>();
        String req = "SELECT * FROM `voyage`";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            while (rs.next()) {
                voyage v = new voyage();
                v.setIdVoyage(rs.getInt("id_voyage"));
                v.setTitre(rs.getString("titre"));
                v.setDescription(rs.getString("description"));
                v.setDateDebut(rs.getDate("date_debut"));
                v.setDateFin(rs.getDate("date_fin"));
                v.setPrix(rs.getDouble("prix"));
                v.setNbPlaces(rs.getInt("nb_places"));
                v.setImage(rs.getString("image"));
                v.setIdDestination(rs.getInt("id_destination"));

                voyages.add(v);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return voyages;
    }

    @Override
    public void update(voyage voyage) {
        String req="UPDATE `voyage` SET `titre`='"+voyage.getTitre()+"',`description`='"+voyage.getDescription()+"',`date_debut`='"+voyage.getDateDebut()+"',`date_fin`='"+voyage.getDateFin()+"',`prix`='"+voyage.getPrix()+"',`nb_places`='"+voyage.getNbPlaces()+"',`image`='"+voyage.getImage()+"',`id_destination`='"+ voyage.getIdDestination()+"' WHERE id_voyage=" + voyage.getIdVoyage();
        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(voyage voyage) {
        String req = "DELETE FROM `voyage` WHERE id_voyage = " + voyage.getIdVoyage();
        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM `voyage`";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}
