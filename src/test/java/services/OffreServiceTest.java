package services;

import models.Offre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

public class OffreServiceTest {

    private OffreService offreService;

    // Initialisation du service avant chaque test
    @BeforeEach
    void setUp() {
        offreService = new OffreService();
    }

    // Test pour ajouter une offre
    @Test
    void testAddOffre() {
        Offre offre = new Offre("Promo été", 30.0, LocalDate.now(), LocalDate.now().plusDays(30));
        offreService.add(offre);

        List<Offre> offres = offreService.getAll();
        assertTrue(offres.stream().anyMatch(o -> o.getTitre().equals("Promo été")));
    }

    // Test pour récupérer toutes les offres
    @Test
    void testGetAllOffres() {
        Offre offre1 = new Offre("Promo été", 30.0, LocalDate.now(), LocalDate.now().plusDays(30));
        Offre offre2 = new Offre("Promo hiver", 25.0, LocalDate.now(), LocalDate.now().plusDays(30));

        offreService.add(offre1);
        offreService.add(offre2);

        List<Offre> offres = offreService.getAll();
        assertTrue(offres.size() > 1);  // Vérifie qu'il y a au moins 2 offres dans la liste
    }

    // Test pour modifier une offre
    @Test
    void testUpdateOffre() {
        Offre offre = new Offre("Promo été", 30.0, LocalDate.now(), LocalDate.now().plusDays(30));
        offreService.add(offre);

        offre.setTitre("Promo automne");
        offreService.update(offre);

        List<Offre> offres = offreService.getAll();
        assertTrue(offres.stream().anyMatch(o -> o.getTitre().equals("Promo automne")));
    }

    // Test pour supprimer une offre
    @Test
    void testDeleteOffre() {
        Offre offre = new Offre("Promo été", 30.0, LocalDate.now(), LocalDate.now().plusDays(30));
        offreService.add(offre);

        offreService.delete(offre);

        List<Offre> offres = offreService.getAll();
        assertFalse(offres.stream().anyMatch(o -> o.getTitre().equals("Promo été")));
    }
}
