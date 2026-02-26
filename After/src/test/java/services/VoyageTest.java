package services;
import models.destination;
import models.voyage;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VoyageTest {
    static ServiceVoyage service;
    @BeforeAll
    static void setup() {
        service = new ServiceVoyage();
    }
    @Test
    @Order(1)
    void testAjouterVoyage() throws SQLException {
        voyage v = new voyage(11,7,10,"voya","descript","stat",Date.valueOf("2026-03-01"),Date.valueOf("2026-03-14"),300);
        service.add(v);
        List<voyage> voy = service.getAll();
        assertFalse(voy.isEmpty());
        assertTrue(
                voy.stream().anyMatch(pers ->
                        pers.getTitre().equals("voya")
                )
        );
    }
    @Test
    @Order(2)
    void testModifierVoyage() throws SQLException {
        voyage voy = new voyage();
        voy.setIdVoyage(11);
        voy.setTitre("france");
        voy.setNbPlaces(10);
        voy.setIdDestination(10);
        voy.setDescription("description");
        voy.setStatut("statut");
        voy.setDateDebut(Date.valueOf("2026-04-01"));
        voy.setDateFin(Date.valueOf("2026-04-14"));
        voy.setPrix(700);
        service.update(voy);
        List<voyage> voyag = service.getAll();
        boolean trouve = voyag.stream()
                .anyMatch(per -> per.getTitre().equals("voya"));
        assertTrue(trouve);
    }
    @Test
    @Order(3)
    void testSupprimerVoyage() throws SQLException {
        voyage vy = new voyage();
        vy.setIdVoyage(11);
        service.delete(vy);
        List<voyage> voy = service.getAll();
        boolean existe = voy.stream()
                .anyMatch(p -> p.getIdVoyage() == 11);
        assertFalse(existe);
    }
    @AfterAll
    static void cleanUp() throws SQLException {
        service.deleteAll();
    }

}

