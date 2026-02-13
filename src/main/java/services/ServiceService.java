package services;

import interfaces.Services;
import models.Service;
import utils.MyDataBase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceService implements Services<Service> {

    private Connection cnx;

    public ServiceService() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(Service service) {
        String req ="INSERT INTO `service`(`nom_service`, `description`, `categorie`) VALUES ('"+service.getNom_service()+"','"+service.getDescription()+"','"+service.getCategorie()+"')";

        try{
        Statement stn = cnx.createStatement();
        stn.executeUpdate(req);
        }catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Service> getAll() {
        List<Service> services = new ArrayList<>();

        String req ="SELECT * FROM `service` ";


       try {
           Statement stn = cnx.createStatement();
           ResultSet rs = stn.executeQuery(req);
       while (rs.next()) {
           Service service = new Service();
           service.setId_service(rs.getInt("id_service"));
           service.setNom_service(rs.getString("nom_service"));
           service.setDescription(rs.getString("description"));
           service.setCategorie(rs.getString("categorie"));
           services.add(service);
       }
       }catch (SQLException e) {
           System.out.println(e.getMessage());
       }


        return services;
    }

    @Override
    public void update(Service service) {

        String req = "UPDATE service SET "
                + "nom_service='" + service.getNom_service() + "', "
                + "description='" + service.getDescription() + "', "
                + "categorie='" + service.getCategorie() + "' "
                + "WHERE id_service=" + service.getId_service();

        try {
            Statement stn = cnx.createStatement();
            stn.executeUpdate(req);
            System.out.println("Service modifié avec succès");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    @Override
    public void delete(Service service) {

        String req = "DELETE FROM service WHERE id_service=" + service.getId_service();

        try {
            Statement stn = cnx.createStatement();
            stn.executeUpdate(req);
            System.out.println("Service supprimé avec succès");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}
