package services;

import models.Activite;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServiceActiviteTest {

    static ServiceActivite service;
    static int generatedId; // pour récupérer l'id auto généré

    @BeforeAll
    static void setup() {
        service = new ServiceActivite();
    }

    @Test
    @Order(1)
    void testAjouterActivite() throws SQLException {

        Activite a = new Activite();
        a.setNom("Excursion Sahara");
        a.setDescription("Voyage organisé dans le désert");
        a.setCategorie("Aventure");
        a.setLieu("Douz");
        a.setPrix(350);

        service.add(a);

        List<Activite> activites = service.getAll();

        assertFalse(activites.isEmpty());

        Activite added = activites.stream()
                .filter(act -> act.getNom().equals("Excursion Sahara"))
                .findFirst()
                .orElse(null);

        assertNotNull(added);

        generatedId = added.getIdActivite(); // on garde l'id pour les tests suivants
    }

    @Test
    @Order(2)
    void testModifierActivite() throws SQLException {

        Activite a = new Activite();
        a.setIdActivite(generatedId);
        a.setNom("Excursion Sahara VIP");
        a.setDescription("Voyage luxe dans le désert");
        a.setCategorie("Aventure");
        a.setLieu("Douz");
        a.setPrix(500);

        service.update(a);

        List<Activite> activites = service.getAll();

        boolean trouve = activites.stream()
                .anyMatch(act ->
                        act.getIdActivite() == generatedId &&
                                act.getNom().equals("Excursion Sahara VIP")
                );

        assertTrue(trouve);
    }

    @Test
    @Order(3)
    void testSupprimerActivite() throws SQLException {

        Activite a = new Activite();
        a.setIdActivite(generatedId);

        service.delete(a);

        List<Activite> activites = service.getAll();

        boolean existe = activites.stream()
                .anyMatch(act ->
                        act.getIdActivite() == generatedId
                );

        assertFalse(existe);
    }

    @AfterAll
    static void cleanUp() throws SQLException {
        service.deleteAll();
    }
}
