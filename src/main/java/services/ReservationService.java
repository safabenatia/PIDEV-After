package services;

import model.Reservation;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IService<Reservation> {

    Connection cnx = MyDatabase.getInstance().getCnx();

    /* ======================= AJOUTER ======================= */
    @Override
    public void ajouter(Reservation r) {
        String sql = "INSERT INTO reservation (id_voyage, id_utilisateur, date_reservation, statut, nb_personnes, prix_total, type, lieu, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, r.getIdVoyage());
            ps.setInt(2, r.getIdUtilisateur());
            ps.setDate(3, Date.valueOf(r.getDateReservation()));
            ps.setString(4, r.getStatut());
            ps.setInt(5, r.getNbPersonnes());
            ps.setDouble(6, r.getPrixTotal());
            ps.setString(7, r.getType());
            ps.setString(8, r.getLieu());
            ps.setString(9, r.getDescription());
            ps.executeUpdate();
            System.out.println("✅ Réservation ajoutée !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur ajout : " + e.getMessage());
        }
    }

    /* ======================= AFFICHER ======================= */
    @Override
    public List<Reservation> afficher() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        try (Statement st = cnx.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Reservation(
                        rs.getInt("id"),
                        rs.getInt("id_voyage"),
                        rs.getInt("id_utilisateur"),
                        rs.getDate("date_reservation").toLocalDate(),
                        rs.getString("statut"),
                        rs.getInt("nb_personnes"),
                        rs.getDouble("prix_total"),
                        rs.getString("type"),
                        rs.getString("lieu"),
                        rs.getString("description")));
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur affichage : " + e.getMessage());
        }
        return list;
    }

    /* ======================= MODIFIER ======================= */
    @Override
    public void modifier(Reservation r) {
        String sql = "UPDATE reservation SET id_voyage=?, id_utilisateur=?, date_reservation=?, statut=?, nb_personnes=?, prix_total=?, type=?, lieu=?, description=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, r.getIdVoyage());
            ps.setInt(2, r.getIdUtilisateur());
            ps.setDate(3, Date.valueOf(r.getDateReservation()));
            ps.setString(4, r.getStatut());
            ps.setInt(5, r.getNbPersonnes());
            ps.setDouble(6, r.getPrixTotal());
            ps.setString(7, r.getType());
            ps.setString(8, r.getLieu());
            ps.setString(9, r.getDescription());
            ps.setInt(10, r.getId());
            ps.executeUpdate();
            System.out.println("✅ Réservation modifiée !");
        } catch (SQLException e) {
            System.out.println("❌ Erreur modification : " + e.getMessage());
        }
    }

    /* ======================= SUPPRIMER ======================= */
    @Override
    public void supprimer(int id) {

        String sql = "DELETE FROM reservation WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            System.out.println("✅ Réservation supprimée avec succès");

        } catch (SQLException e) {
            System.out.println("❌ Erreur suppression : " + e.getMessage());
        }
    }
}
