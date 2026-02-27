package services;

import models.Planning;
import models.Activite;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicePlanningTest {

    static ServicePlanning service;
    static int generatedId; // pour stocker l'ID auto généré
    static int testUserId = 1; // utilisateur fixe pour le test
    static int testActiviteId = 1; // ID d'une activité existante dans la base

    @BeforeAll
    static void setup() {
        service = new ServicePlanning();
    }

    @Test
    @Order(1)
    void testAjouterPlanning() throws SQLException {

        Planning p = new Planning();
        p.setIdUser(testUserId);
        p.setIdActivite(testActiviteId);
        p.setDateActivite(LocalDate.now().plusDays(5));
        p.setHeureDebut(LocalTime.of(9, 30));
        p.setDuree(120);

        service.add(p);

        List<Planning> plannings = service.getAll();

        assertFalse(plannings.isEmpty());

        Planning added = plannings.stream()
                .filter(pl -> pl.getIdUser() == testUserId &&
                        pl.getIdActivite() == testActiviteId)
                .findFirst()
                .orElse(null);

        assertNotNull(added);

        generatedId = added.getIdPlanning();
    }

    @Test
    @Order(2)
    void testModifierPlanning() throws SQLException {

        Planning p = new Planning();
        p.setIdPlanning(generatedId);
        p.setIdUser(testUserId);
        p.setIdActivite(testActiviteId);
        p.setDateActivite(LocalDate.now().plusDays(10));
        p.setHeureDebut(LocalTime.of(14, 0));
        p.setDuree(180);

        service.update(p);

        List<Planning> plannings = service.getAll();

        boolean trouve = plannings.stream()
                .anyMatch(pl ->
                        pl.getIdPlanning() == generatedId &&
                                pl.getDateActivite().equals(LocalDate.now().plusDays(10)) &&
                                pl.getDuree() == 180
                );

        assertTrue(trouve);
    }

    @Test
    @Order(3)
    void testSupprimerPlanning() throws SQLException {

        Planning p = new Planning();
        p.setIdPlanning(generatedId);

        service.delete(p);

        List<Planning> plannings = service.getAll();

        boolean existe = plannings.stream()
                .anyMatch(pl -> pl.getIdPlanning() == generatedId);

        assertFalse(existe);
    }

    @AfterAll
    static void cleanUp() throws SQLException {
        service.deleteAll();
    }
}

