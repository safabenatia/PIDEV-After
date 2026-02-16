package services;

import model.Reservation;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IService<Reservation> {

    Connection cnx = MyDatabase.getInstance().getCnx();

    /* ======================= AJOUTER ======================= */
    @Override
    public void ajouter(Reservation r) {

        String sql = "INSERT INTO reservation (date_reservation, statut, nb_personnes) VALUES (?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(r.getDateReservation()));
            ps.setString(2, r.getStatut());
            ps.setInt(3, r.getNbPersonnes());

            ps.executeUpdate();
            System.out.println("✅ Réservation ajoutée avec succès");

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

                Reservation r = new Reservation(
                        rs.getInt("id"),
                        rs.getDate("date_reservation").toLocalDate(),
                        rs.getString("statut"),
                        rs.getInt("nb_personnes")
                );

                list.add(r);
            }

        } catch (SQLException e) {
            System.out.println("❌ Erreur affichage : " + e.getMessage());
        }

        return list;
    }

    /* ======================= MODIFIER ======================= */
    @Override
    public void modifier(Reservation r) {

        String sql = "UPDATE reservation SET date_reservation=?, statut=?, nb_personnes=? WHERE id=?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(r.getDateReservation()));
            ps.setString(2, r.getStatut());
            ps.setInt(3, r.getNbPersonnes());
            ps.setInt(4, r.getId());

            ps.executeUpdate();
            System.out.println("✅ Réservation modifiée avec succès");

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
