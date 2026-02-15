package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServiceTest {

    private Service service;

    @BeforeEach
    void setUp() {
        service = new Service(1, "Transport", "Transport touristique", "Voyage");
    }

    @Test
    void testConstructeur() {
        assertNotNull(service);
        assertEquals(1, service.getId_service());
        assertEquals("Transport", service.getNom_service());
        assertEquals("Transport touristique", service.getDescription());
        assertEquals("Voyage", service.getCategorie());
    }

    @Test
    void testSetters() {
        service.setId_service(2);
        service.setNom_service("Hébergement");
        service.setDescription("Hôtel 5 étoiles");
        service.setCategorie("Logement");

        assertEquals(2, service.getId_service());
        assertEquals("Hébergement", service.getNom_service());
        assertEquals("Hôtel 5 étoiles", service.getDescription());
        assertEquals("Logement", service.getCategorie());
    }

    @Test
    void testToString() {
        String toString = service.toString();
        assertTrue(toString.contains("Transport"));
    }
}