package tests;

import model.Reservation;
import model.Paiement;
import services.ReservationService;
import services.PaiementService;

import java.time.LocalDate;
import java.util.List;

public class MainTest {

    public static void main(String[] args) {

        ReservationService rs = new ReservationService();
        PaiementService ps = new PaiementService();

        /* =====================================================
                         1️⃣ AJOUT RESERVATION
        ===================================================== */

        Reservation r = new Reservation(LocalDate.now(), "Confirmée", 4);
        rs.ajouter(r);

        // récupérer la dernière réservation
        List<Reservation> reservations = rs.afficher();
        Reservation derniereReservation =
                reservations.get(reservations.size() - 1);

        System.out.println("\n--- LISTE RESERVATIONS APRES AJOUT ---");
        System.out.println(reservations);

        /* =====================================================
                         2️⃣ MODIFIER RESERVATION
        ===================================================== */

        derniereReservation.setStatut("Annulée");
        derniereReservation.setNbPersonnes(2);
        rs.modifier(derniereReservation);

        System.out.println("\n--- LISTE RESERVATIONS APRES MODIFICATION ---");
        System.out.println(rs.afficher());

        /* =====================================================
                         3️⃣ AJOUT PAIEMENT
        ===================================================== */

        Paiement p = new Paiement(
                "PAY-001",
                750.0,
                "Carte",
                derniereReservation.getId()
        );

        ps.ajouter(p);

        System.out.println("\n--- LISTE PAIEMENTS APRES AJOUT ---");
        System.out.println(ps.afficher());

        /* =====================================================
                         4️⃣ MODIFIER PAIEMENT
        ===================================================== */

        List<Paiement> paiements = ps.afficher();
        Paiement dernierPaiement =
                paiements.get(paiements.size() - 1);

        dernierPaiement.setMontant(900.0);
        dernierPaiement.setMethode("Carte");

        ps.modifier(dernierPaiement);

        System.out.println("\n--- LISTE PAIEMENTS APRES MODIFICATION ---");
        System.out.println(ps.afficher());

        /* =====================================================
                         5️⃣ SUPPRESSION PAIEMENT
        ===================================================== */

        ps.supprimer(dernierPaiement.getId());

        System.out.println("\n--- LISTE PAIEMENTS APRES SUPPRESSION ---");
        System.out.println(ps.afficher());

        /* =====================================================
                         6️⃣ SUPPRESSION RESERVATION
        ===================================================== */

        rs.supprimer(derniereReservation.getId());

        System.out.println("\n--- LISTE RESERVATIONS APRES SUPPRESSION ---");
        System.out.println(rs.afficher());

        System.out.println("\n🎯 TEST CRUD COMPLET TERMINE !");
    }
}
