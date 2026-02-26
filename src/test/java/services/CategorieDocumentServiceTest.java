package services;

import models.CategorieDocument;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategorieDocumentServiceTest {

    static serviceCategorieDocument service;
    static int idCategorieTest;

    @BeforeAll
    static void setup() {
        service = new serviceCategorieDocument();
    }

    // ================== TEST ADD ==================
    @Test
    @Order(1)
    void testAjouterCategorie() {
        CategorieDocument cat = new CategorieDocument();
        cat.setLibelle("CategorieTest");
        cat.setDescription("DescriptionTest");

        service.add(cat);

        // البحث عن الفئة التي تمت إضافتها
        List<CategorieDocument> categories = service.getAll();
        CategorieDocument catAjoute = categories.stream()
                .filter(c -> c.getLibelle().equals("CategorieTest"))
                .findFirst()
                .orElse(null);

        assertNotNull(catAjoute, "Categorie non ajoutée");
        idCategorieTest = catAjoute.getIdCategorie();
        assertTrue(idCategorieTest > 0, "ID de la catégorie incorrect");
    }

    // ================== TEST UPDATE ==================
    @Test
    @Order(2)
    void testUpdateCategorie() {
        CategorieDocument cat = new CategorieDocument();
        cat.setIdCategorie(idCategorieTest);
        cat.setLibelle("CategorieModifie");
        cat.setDescription("DescriptionModifie");

        service.update(cat);

        // الحصول على الفئة بعد التعديل
        CategorieDocument catModifie = service.getAll().stream()
                .filter(c -> c.getIdCategorie() == idCategorieTest)
                .findFirst()
                .orElse(null);

        assertNotNull(catModifie, "Categorie non trouvée après update");
        assertEquals("CategorieModifie", catModifie.getLibelle(), "Libelle non modifié");
        assertEquals("DescriptionModifie", catModifie.getDescription(), "Description non modifiée");
    }

    // ================== TEST DELETE ==================
    @Test
    @Order(3)
    void testDeleteCategorie() {
        CategorieDocument cat = new CategorieDocument();
        cat.setIdCategorie(idCategorieTest);

        service.delete(cat);

        CategorieDocument catSupprime = service.getAll().stream()
                .filter(c -> c.getIdCategorie() == idCategorieTest)
                .findFirst()
                .orElse(null);

        assertNull(catSupprime, "Categorie non supprimée");
    }

    // ================== CLEAN DB ==================
    // @AfterAll
    // static void cleanUp() throws Exception {
    //     service.deleteAll();
    // }
}
