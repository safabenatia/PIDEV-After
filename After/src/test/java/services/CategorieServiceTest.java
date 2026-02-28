package service;

import models.Categorie;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategorieServiceTest {

    static CategorieService service;
    static int idCategorieTest;

    @BeforeAll
    static void setup() {
        service = new CategorieService();
    }

    @Test
    @Order(1)
    void testAjouterCategorie() {
        Categorie c = new Categorie();
        c.setNomCategorie("Test Categorie");
        c.setDescription("Description de test pour categorie");
        c.setIconeUrl("test_icon.png");

        service.add(c);

        List<Categorie> categories = service.getAll();

        assertFalse(categories.isEmpty());

        Categorie derniereCategorie = categories.get(categories.size() - 1);
        idCategorieTest = derniereCategorie.getIdCat();

        assertEquals("Test Categorie", derniereCategorie.getNomCategorie());
        assertEquals("Description de test pour categorie", derniereCategorie.getDescription());
        assertEquals("test_icon.png", derniereCategorie.getIconeUrl());
    }

    @Test
    @Order(2)
    void testAfficherCategories() {
        List<Categorie> categories = service.getAll();

        assertNotNull(categories);
        assertFalse(categories.isEmpty());
    }

    @Test
    @Order(3)
    void testRecupererCategorieParId() {
        Categorie categorie = service.getById(idCategorieTest);

        assertNotNull(categorie);
        assertEquals(idCategorieTest, categorie.getIdCat());
        assertEquals("Test Categorie", categorie.getNomCategorie());
    }

    @Test
    @Order(4)
    void testModifierCategorie() {
        Categorie c = new Categorie();
        c.setIdCat(idCategorieTest);
        c.setNomCategorie("Test Categorie Modifiee");
        c.setDescription("Description modifiee");
        c.setIconeUrl("test_icon_modified.png");

        service.update(c);

        Categorie categorieModifiee = service.getById(idCategorieTest);

        assertNotNull(categorieModifiee);
        assertEquals("Test Categorie Modifiee", categorieModifiee.getNomCategorie());
        assertEquals("Description modifiee", categorieModifiee.getDescription());
        assertEquals("test_icon_modified.png", categorieModifiee.getIconeUrl());
    }

    @Test
    @Order(5)
    void testSupprimerCategorie() {
        Categorie c = new Categorie();
        c.setIdCat(idCategorieTest);

        service.delete(c);

        Categorie categorieSupprimee = service.getById(idCategorieTest);

        assertNull(categorieSupprimee);
    }

    @Test
    @Order(6)
    void testAjouterCategorieInvalide() {
        Categorie c = new Categorie();
        c.setNomCategorie("");
        c.setDescription("Test invalide");
        c.setIconeUrl("");

        int tailleAvant = service.getAll().size();
        service.add(c);
        int tailleApres = service.getAll().size();

        if (tailleApres > tailleAvant) {
            List<Categorie> categories = service.getAll();
            Categorie derniere = categories.get(categories.size() - 1);
            service.delete(derniere);
        }
    }

    @Test
    @Order(7)
    void testRecupererCategorieInexistante() {
        Categorie categorie = service.getById(999999);
        assertNull(categorie);
    }

    @AfterEach
    void cleanUp() {
        List<Categorie> categories = service.getAll();

        for (Categorie c : categories) {
            if (c.getNomCategorie() != null && c.getNomCategorie().startsWith("Test")) {
                service.delete(c);
            }
        }
    }

    @AfterAll
    static void tearDown() {
    }
}