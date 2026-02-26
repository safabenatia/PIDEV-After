package services;

import interfaces.Services;
import models.destination;
import utils.Mydatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDestination implements Services<destination> {
    private Connection cnx;
    public ServiceDestination() {
        this.cnx= Mydatabase.getInstance().getCnx();
    }
    @Override
    public void add(destination destination) {
        String req="INSERT INTO `destination`( `pays`, `ville`, `continent`, `description`, `image`) VALUES ('"+destination.getPays()+"','"+destination.getVille()+"','"+destination.getContinent()+"','"+destination.getDescription()+"','"+destination.getImage()+"')";
        try{
            Statement stm= cnx.createStatement();
            stm.executeUpdate(req);
        }catch (SQLException e){
            System.out.println( e.getMessage());
        }
    }

    @Override
    public List<destination> getAll() {
        List<destination> destinations=new ArrayList<>();
        String req="SELECT * FROM destination";

        try {
            Statement stm=cnx.createStatement();
            ResultSet rs=stm.executeQuery(req);
            while (rs.next()) {
                destination d=new destination();
                d.setId_destination(rs.getInt(1));
                d.setPays(rs.getString("pays"));
                d.setVille(rs.getString("ville"));
                d.setContinent(rs.getString("continent"));
                d.setDescription(rs.getString("description"));
                d.setImage(rs.getString("image"));
                destinations.add(d);
            }
      } catch (SQLException e) {
            System.out.println( e.getMessage());
        }
        return destinations;
    }
    @Override
    public void update(destination destination) {
        String req="UPDATE `destination` SET `pays`='"+destination.getPays()+"',`ville`='"+destination.getVille()+"',`continent`='"+destination.getContinent()+"',`description`='"+destination.getDescription()+"',`image`='"+destination.getImage()+"' WHERE id_destination = " + destination.getId_destination();
        try{
            Statement stm= cnx.createStatement();
            stm.executeUpdate(req);
        }catch (SQLException e){
            System.out.println( e.getMessage());
        }
    }

    @Override
    public void delete(destination destination) {
        String req = "DELETE FROM `destination` WHERE id_destination = " + destination.getId_destination();
        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM `destination`";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}
