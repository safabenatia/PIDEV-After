package tn.esprit;

import models.Service;
import models.Offre;
import services.ServiceService;
import services.OffreService;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        ServiceService ss = new ServiceService();
        OffreService os = new OffreService();

        // =====================================================
        // ================== SERVICE CRUD =====================
        // =====================================================

        System.out.println("========== TEST CRUD SERVICE ==========");

        // -------- ADD SERVICE --------
        Service s = new Service();
        s.setNom_service("Transport");
        s.setDescription("Transport touristique");
        s.setCategorie("Voyage");

        ss.add(s);
        System.out.println("Service ajouté ✔");

        // -------- UPDATE SERVICE --------
        Service sUpdate = new Service();
        sUpdate.setId_service(1); // ⚠ mettre un ID existant
        sUpdate.setNom_service("Transport VIP");
        sUpdate.setDescription("Transport luxe");
        sUpdate.setCategorie("Voyage");

        ss.update(sUpdate);
        System.out.println("Service modifié ✔");

        // -------- GET ALL SERVICES --------
        List<Service> services = ss.getAll();
        System.out.println("\n===== LISTE DES SERVICES =====");

        for (Service service : services) {
            System.out.println(
                    "ID: " + service.getId_service() +
                            " | Nom: " + service.getNom_service() +
                            " | Description: " + service.getDescription() +
                            " | Catégorie: " + service.getCategorie()
            );
        }

        // =====================================================
        // ================== OFFRE CRUD =======================
        // =====================================================

        System.out.println("\n========== TEST CRUD OFFRE ==========");

        // -------- ADD OFFRE --------
        /*Offre o = new Offre();
        o.setTitre("Offre Premium");
        o.setPrix(1500);
        o.setDuree(7);
        o.setServiceId(1); // ⚠ doit correspondre à un service existant

        os.add(o);
        System.out.println("Offre ajoutée ✔");

        // -------- UPDATE OFFRE --------
        Offre oUpdate = new Offre();
        oUpdate.setServiceId(1); // ⚠ mettre un ID existant
        oUpdate.setTitre("Offre VIP");
        oUpdate.setPrix(2000);
        oUpdate.setDuree(10);


        os.update(oUpdate);
        System.out.println("Offre modifiée ✔");

        // -------- GET ALL OFFRES --------
        List<Offre> offres = os.getAll();
        System.out.println("\n===== LISTE DES OFFRES =====");

        for (Offre offre : offres) {
            System.out.println(
                    "ID: " + offre.getId_service() +
                            " | Titre: " + offre.getTitre() +
                            " | Prix: " + offre.getPrix() +
                            " | Durée: " + offre.getDuree() +
                            " | ID Service: " + offre.getId_service()
            );
        }

        // -------- DELETE OFFRE --------
        /*Offre oDelete = new Offre();
        oDelete.setServiceId(2); // ID existant
        os.delete(oDelete);
        System.out.println("Offre supprimée ✔");

        // -------- DELETE SERVICE --------
        Service sDelete = new Service();
        sDelete.setId_service(2); // ID existant
        ss.delete(sDelete);
        System.out.println("Service supprimé ✔");

        System.out.println("\n========== FIN TEST ==========");*/
    }
}
