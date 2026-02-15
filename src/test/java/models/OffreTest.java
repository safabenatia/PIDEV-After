package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class OffreTest {

    private Offre offre;

    @BeforeEach
    void setUp() {
        offre = new Offre(1, "Offre Été", 500.0, 7, 1);
    }

    @Test
    void testConstructeur() {
        assertNotNull(offre);
        assertEquals(1, offre.getId_offre());
        assertEquals("Offre Été", offre.getTitre());
        assertEquals(500.0, offre.getPrix());
        assertEquals(7, offre.getDuree());
        assertEquals(1, offre.getServiceId());
    }

    @Test
    void testConstructeurSansId() {
        Offre nouvelleOffre = new Offre("Offre Hiver", 300.0, 5, 2);
        assertEquals("Offre Hiver", nouvelleOffre.getTitre());
        assertEquals(300.0, nouvelleOffre.getPrix());
        assertEquals(5, nouvelleOffre.getDuree());
        assertEquals(2, nouvelleOffre.getServiceId());
    }

    @Test
    void testSetters() {
        offre.setTitre("Nouveau titre");
        offre.setPrix(600.0);
        offre.setDuree(10);
        offre.setServiceId(3);

        assertEquals("Nouveau titre", offre.getTitre());
        assertEquals(600.0, offre.getPrix());
        assertEquals(10, offre.getDuree());
        assertEquals(3, offre.getServiceId());
    }
}