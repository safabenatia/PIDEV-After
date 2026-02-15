package services;

import models.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

public class ServiceServiceTest {

    private ServiceService serviceService;

    // Initialisation du service avant chaque test
    @BeforeEach
    void setUp() {
        serviceService = new ServiceService();
    }

    // Test pour ajouter un service
    @Test
    void testAddService() {
        Service service = new Service("Location voiture", "Location de voiture de luxe", "Transport", 150.0, true, LocalDate.now());
        serviceService.add(service);

        List<Service> services = serviceService.getAll();
        assertTrue(services.stream().anyMatch(s -> s.getNom_service().equals("Location voiture")));
    }

    // Test pour récupérer tous les services
    @Test
    void testGetAllServices() {
        Service service1 = new Service("Location voiture", "Description", "Transport", 100.0, true, LocalDate.now());
        Service service2 = new Service("Transport VIP", "Transport de luxe", "Transport", 200.0, true, LocalDate.now());

        serviceService.add(service1);
        serviceService.add(service2);

        List<Service> services = serviceService.getAll();
        assertTrue(services.size() > 1);  // Vérifie qu'il y a au moins 2 services dans la liste
    }

    // Test pour modifier un service
    @Test
    void testUpdateService() {
        Service service = new Service("Location voiture", "Location de voiture de luxe", "Transport", 150.0, true, LocalDate.now());
        serviceService.add(service);

        service.setNom_service("Location VIP");
        serviceService.update(service);

        List<Service> services = serviceService.getAll();
        assertTrue(services.stream().anyMatch(s -> s.getNom_service().equals("Location VIP")));
    }

    // Test pour supprimer un service
    @Test
    void testDeleteService() {
        Service service = new Service("Location voiture", "Description", "Transport", 100.0, true, LocalDate.now());
        serviceService.add(service);

        serviceService.delete(service);

        List<Service> services = serviceService.getAll();
        assertFalse(services.stream().anyMatch(s -> s.getNom_service().equals("Location voiture")));
    }
}
