package services;

import model.Paiement;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;

public class PaiementService {

    private Connection cnx;

    public PaiementService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    // ================== AJOUTER ==================
    public void ajouter(Paiement p) {
        String req = "INSERT INTO paiement(reference, montant, methode, id_reservation) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, p.getReference());
            pst.setDouble(2, p.getMontant());
            pst.setString(3, p.getMethode());
            pst.setInt(4, p.getIdReservation());
            pst.executeUpdate();
            System.out.println("✅ Paiement ajouté !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================== MODIFIER ==================
    // ================== MODIFIER ==================
    public void modifier(Paiement p) {
        // Changement : idReservation -> id_reservation
        String req = "UPDATE paiement SET reference=?, montant=?, methode=?, id_reservation=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, p.getReference());
            pst.setDouble(2, p.getMontant());
            pst.setString(3, p.getMethode());
            pst.setInt(4, p.getIdReservation());
            pst.setInt(5, p.getId());
            pst.executeUpdate();
            System.out.println("✅ Paiement modifié !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // ================== SUPPRIMER PAR ID ==================
    public void supprimer(int id) {
        String req = "DELETE FROM paiement WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Paiement supprimé par ID !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================== SUPPRIMER PAR REFERENCE ==================
    public void supprimerParReference(String reference) {
        String req = "DELETE FROM paiement WHERE reference=?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, reference);
            pst.executeUpdate();
            System.out.println("✅ Paiement supprimé par référence !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // ================== AFFICHER ==================
    public ArrayList<Paiement> afficher() {
        ArrayList<Paiement> liste = new ArrayList<>();
        String req = "SELECT * FROM paiement";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                Paiement p = new Paiement(
                        rs.getInt("id"),
                        rs.getString("reference"),
                        rs.getDouble("montant"),
                        rs.getString("methode"),
                        rs.getInt("id_reservation")
                );
                liste.add(p);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return liste;
    }
}
