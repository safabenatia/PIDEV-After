package tn.esprit;

import models.Admin;
import models.Users;
import models.Voyageur;
import services.ServiceUsers;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        ServiceUsers service = new ServiceUsers();

        System.out.println("=== TEST CRUD UTILISATEURS (avec héritage) ===\n");

        // --------------------------------------------------------------------
        // 1. CREATE - Ajout de quelques utilisateurs
        // --------------------------------------------------------------------
        System.out.println("Ajout de 2 utilisateurs...");

        Admin admin = new Admin(
                0,                  // id sera généré
                "Karim",
                "Ben Ali",
                "karim.admin@tunisie.com",
                "admin2025",        // sera hashé dans le service
                "https://example.com/karim.jpg",
                "+216 98 123 456"
        );

        Voyageur voy = new Voyageur(
                0,
                "Nour",
                "Jabri",
                "nour.voyageuse@gmail.com",
                "secret123",
                null,               // pas de photo
                "+216 55 987 654"
        );

        service.add(admin);
        service.add(voy);

        System.out.println("→ Admin ajouté  → ID = " + admin.getId());
        System.out.println("→ Voyageur ajouté → ID = " + voy.getId());


        // --------------------------------------------------------------------
        // 2. READ - Afficher tous les utilisateurs
        // --------------------------------------------------------------------
        System.out.println("\nListe de tous les utilisateurs :");
        List<Users> tous = service.getAll();

        for (Users u : tous) {
            String type = (u instanceof Admin) ? "ADMIN" : "VOYAGEUR";
            System.out.printf("ID: %d | %s | %s %s | %s | %s%n",
                    u.getId(),
                    type,
                    u.getNom(),
                    u.getPrenom(),
                    u.getEmail(),
                    u.getTelephone());
        }


        // --------------------------------------------------------------------
        // 3. UPDATE - Modifier un utilisateur (ex: changer téléphone + mot de passe)
        // --------------------------------------------------------------------
        if (!tous.isEmpty()) {
            Users aModifier = tous.get(0);  // prenons le premier (souvent l'admin)

            System.out.println("\nAvant modification : " + aModifier.getNom() + " " + aModifier.getTelephone());

            aModifier.setTelephone("+216 99 000 111");
            aModifier.setMotDePasse("nouveauMotDePasse2025");  // sera hashé

            service.update(aModifier);

            System.out.println("Après modification : " + aModifier.getNom() + " " + aModifier.getTelephone());
        }


        // --------------------------------------------------------------------
        // 4. LOGIN - Test de connexion
        // --------------------------------------------------------------------
        System.out.println("\nTest de connexion :");

        Users connected = service.login("karim.admin@tunisie.com", "admin2025");
        if (connected != null) {
            String type = (connected instanceof Admin) ? "ADMIN" : "VOYAGEUR";
            System.out.println("Connexion réussie ! → " + type + " : " + connected.getEmail());
        } else {
            System.out.println("Échec connexion (mauvais mot de passe ou email inexistant)");
        }


        // --------------------------------------------------------------------
        // 5. DELETE - Supprimer un utilisateur (ex: le voyageur)
        // --------------------------------------------------------------------
        if (voy.getId() > 0) {
            System.out.println("\nSuppression du voyageur ID=" + voy.getId());
            service.delete(voy);
        }


        // --------------------------------------------------------------------
        // 6. Vérification finale
        // --------------------------------------------------------------------
        System.out.println("\nListe finale après suppression :");
        for (Users u : service.getAll()) {
            String type = (u instanceof Admin) ? "ADMIN" : "VOYAGEUR";
            System.out.println(type + " → " + u.getEmail());
        }

        System.out.println("\n=== FIN DU TEST CRUD ===");
    }
}