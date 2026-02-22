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

public class ServiceService implements Services<Service> {  // ✅ Nom correct

    private Connection cnx;  // ✅ Une seule déclaration, private

    public ServiceService() {  // ✅ Constructeur sans @Override
        this.cnx = MyDataBase.getInstance().getCnx();  // ✅ CORRECT
    }

    @Override
    public void add(Service service) {
        // ✅ SQL COMPLET et correct
        String req = "INSERT INTO `service`(`nom_service`, `description`, `categorie`) VALUES ('"
                + service.getNom_service() + "', '"
                + service.getDescription() + "', '"
                + service.getCategorie() + "')";

        try {
            Statement stn = cnx.createStatement();
            stn.executeUpdate(req);
            System.out.println("✅ Service ajouté avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    @Override
    public List<Service> getAll() {
        List<Service> services = new ArrayList<>();
        String req = "SELECT * FROM `service`";

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
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération : " + e.getMessage());
        }

        return services;
    }

    // ✅ Méthode getById utilisée par StatsAPI
    public Service getById(int id) {
        List<Service> services = getAll();
        for (Service s : services) {
            if (s.getId_service() == id) {
                return s;
            }
        }
        return null;
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
            System.out.println("✅ Service modifié avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la modification : " + e.getMessage());
        }
    }

    @Override
    public void delete(Service service) {
        String req = "DELETE FROM service WHERE id_service=" + service.getId_service();

        try {
            Statement stn = cnx.createStatement();
            stn.executeUpdate(req);
            System.out.println("✅ Service supprimé avec succès");
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
        }
    }
}