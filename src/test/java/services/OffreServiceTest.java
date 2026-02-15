package services;

import models.Offre;
import models.Service;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class OffreServiceTest {

    private OffreService offreService;
    private ServiceService serviceService;

    @BeforeEach
    void setUp() {
        offreService = new OffreService();
        serviceService = new ServiceService();

        // Nettoyer
        try {
            List<Offre> offres = offreService.getAll();
            for (Offre o : offres) {
                offreService.delete(o);
            }
        } catch (Exception e) {
            // Ignorer
        }
    }

    @Test
    void testAjouterOffre() {
        // Créer un service d'abord
        Service service = new Service();
        service.setNom_service("Test Service");
        service.setDescription("Desc");
        service.setCategorie("Cat");
        serviceService.add(service);

        List<Service> services = serviceService.getAll();
        int serviceId = services.get(services.size() - 1).getId_service();

        Offre offre = new Offre("Offre Test", 250.0, 5, serviceId);
        offreService.add(offre);

        List<Offre> offres = offreService.getAll();
        assertFalse(offres.isEmpty());

        Offre derniere = offres.get(offres.size() - 1);
        assertEquals("Offre Test", derniere.getTitre());
        assertEquals(250.0, derniere.getPrix());
        assertEquals(5, derniere.getDuree());
        assertEquals(serviceId, derniere.getServiceId());
    }

    @Test
    void testGetAllOffres() {
        // Créer un service
        Service service = new Service();
        service.setNom_service("Service Test");
        serviceService.add(service);
        int serviceId = serviceService.getAll().get(0).getId_service();

        // Ajouter des offres
        offreService.add(new Offre("Offre 1", 100.0, 3, serviceId));
        offreService.add(new Offre("Offre 2", 200.0, 5, serviceId));

        List<Offre> offres = offreService.getAll();
        assertTrue(offres.size() >= 2);
    }

    @Test
    void testModifierOffre() {
        // Créer service
        Service service = new Service();
        service.setNom_service("Service Test");
        serviceService.add(service);
        int serviceId = serviceService.getAll().get(0).getId_service();

        // Ajouter offre
        Offre offre = new Offre("Original", 150.0, 4, serviceId);
        offreService.add(offre);

        // Récupérer ID
        List<Offre> offres = offreService.getAll();
        Offre aModifier = offres.get(offres.size() - 1);
        int id = aModifier.getId_offre();

        // Modifier
        aModifier.setTitre("Modifié");
        aModifier.setPrix(300.0);
        aModifier.setDuree(10);
        offreService.update(aModifier);

        // Vérifier
        offres = offreService.getAll();
        Offre modifie = null;
        for (Offre o : offres) {
            if (o.getId_offre() == id) {
                modifie = o;
                break;
            }
        }

        assertNotNull(modifie);
        assertEquals("Modifié", modifie.getTitre());
        assertEquals(300.0, modifie.getPrix());
        assertEquals(10, modifie.getDuree());
    }

    @Test
    void testSupprimerOffre() {
        // Créer service
        Service service = new Service();
        service.setNom_service("Service Test");
        serviceService.add(service);
        int serviceId = serviceService.getAll().get(0).getId_service();

        // Ajouter offre
        offreService.add(new Offre("À supprimer", 100.0, 2, serviceId));

        List<Offre> avant = offreService.getAll();
        int tailleAvant = avant.size();

        Offre aSupprimer = avant.get(avant.size() - 1);
        offreService.delete(aSupprimer);

        List<Offre> apres = offreService.getAll();
        assertEquals(tailleAvant - 1, apres.size());
    }
}