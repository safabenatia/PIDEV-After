package services;

import interfaces.Services;
import models.Service;
import utils.Mydatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServiceService implements Services<Service> {

    private Connection cnx;

    public ServiceService() {
        this.cnx = Mydatabase.getInstance().getCnx();
    }

    @Override
    public void add(Service service) {
        // ✅ Utilisation de PreparedStatement (plus sûr)
        String req = "INSERT INTO service (nom_service, description, categorie) VALUES (?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, service.getNom_service());
            ps.setString(2, service.getDescription());
            ps.setString(3, service.getCategorie());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Service ajouté avec succès : " + service.getNom_service());
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Service> getAll() {
        List<Service> services = new ArrayList<>();
        String req = "SELECT * FROM service";

        try (Statement stn = cnx.createStatement();
             ResultSet rs = stn.executeQuery(req)) {

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
            e.printStackTrace();
        }

        return services;
    }

    public Service getById(int id) {
        String req = "SELECT * FROM service WHERE id_service = ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Service service = new Service();
                    service.setId_service(rs.getInt("id_service"));
                    service.setNom_service(rs.getString("nom_service"));
                    service.setDescription(rs.getString("description"));
                    service.setCategorie(rs.getString("categorie"));
                    return service;
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la récupération par ID : " + e.getMessage());
        }
        return null;
    }

    @Override
    public void update(Service service) {
        String req = "UPDATE service SET nom_service = ?, description = ?, categorie = ? WHERE id_service = ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, service.getNom_service());
            ps.setString(2, service.getDescription());
            ps.setString(3, service.getCategorie());
            ps.setInt(4, service.getId_service());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Service modifié avec succès : " + service.getNom_service());
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Service service) {
        String req = "DELETE FROM service WHERE id_service = ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, service.getId_service());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Service supprimé avec succès : ID " + service.getId_service());
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur lors de la suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }
}