package services;

import models.Service;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class ServiceServiceTest {

    private ServiceService serviceService;

    @BeforeEach
    void setUp() {
        serviceService = new ServiceService();
        // Nettoyer la table avant chaque test
        try {
            List<Service> services = serviceService.getAll();
            for (Service s : services) {
                serviceService.delete(s);
            }
        } catch (Exception e) {
            // Ignorer
        }
    }

    @Test
    void testAjouterService() {
        Service service = new Service();
        service.setNom_service("Test Service");
        service.setDescription("Description test");
        service.setCategorie("Test Catégorie");

        serviceService.add(service);

        List<Service> services = serviceService.getAll();
        assertFalse(services.isEmpty());

        Service dernier = services.get(services.size() - 1);
        assertEquals("Test Service", dernier.getNom_service());
        assertEquals("Description test", dernier.getDescription());
        assertEquals("Test Catégorie", dernier.getCategorie());
    }

    @Test
    void testGetAllServices() {
        // Ajouter quelques services
        Service s1 = new Service();
        s1.setNom_service("Service 1");
        s1.setDescription("Desc 1");
        s1.setCategorie("Cat 1");
        serviceService.add(s1);

        Service s2 = new Service();
        s2.setNom_service("Service 2");
        s2.setDescription("Desc 2");
        s2.setCategorie("Cat 2");
        serviceService.add(s2);

        List<Service> services = serviceService.getAll();
        assertTrue(services.size() >= 2);
    }

    @Test
    void testModifierService() {
        // Ajouter un service
        Service service = new Service();
        service.setNom_service("Original");
        service.setDescription("Original Desc");
        service.setCategorie("Original Cat");
        serviceService.add(service);

        // Récupérer l'ID du service ajouté
        List<Service> services = serviceService.getAll();
        Service aModifier = services.get(services.size() - 1);
        int id = aModifier.getId_service();

        // Modifier
        aModifier.setNom_service("Modifié");
        aModifier.setDescription("Modifié Desc");
        aModifier.setCategorie("Modifié Cat");
        serviceService.update(aModifier);

        // Vérifier
        services = serviceService.getAll();
        Service modifie = null;
        for (Service s : services) {
            if (s.getId_service() == id) {
                modifie = s;
                break;
            }
        }

        assertNotNull(modifie);
        assertEquals("Modifié", modifie.getNom_service());
        assertEquals("Modifié Desc", modifie.getDescription());
        assertEquals("Modifié Cat", modifie.getCategorie());
    }

    @Test
    void testSupprimerService() {
        // Ajouter un service
        Service service = new Service();
        service.setNom_service("À supprimer");
        service.setDescription("Description");
        service.setCategorie("Catégorie");
        serviceService.add(service);

        // Récupérer l'ID
        List<Service> avant = serviceService.getAll();
        int tailleAvant = avant.size();

        Service aSupprimer = avant.get(avant.size() - 1);
        serviceService.delete(aSupprimer);

        // Vérifier
        List<Service> apres = serviceService.getAll();
        assertEquals(tailleAvant - 1, apres.size());
    }
}