package services;

import models.CategorieDocument;
import models.Document;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentServiceTest {

    static serviceDocument service;
    static int idDocumentTest;

    @BeforeAll
    static void setup() {
        service = new serviceDocument();
    }

    @Test
    @Order(1)
    void testAjouterDocument() {
        CategorieDocument cat = new CategorieDocument();
        cat.setIdCategorie(1);

        Document d = new Document(
                0,
                "DocumentTest",
                "test/document.pdf",
                new Date(System.currentTimeMillis()),
                Date.valueOf("2030-01-01"),
                cat
        );

        service.add(d);

        Document docAjoute = service.getAll().stream()
                .filter(doc -> doc.getNomDocument().equals("DocumentTest"))
                .findFirst()
                .orElse(null);

        assertNotNull(docAjoute);
        idDocumentTest = docAjoute.getIdDocument();
        assertTrue(idDocumentTest > 0);
    }

    @Test
    @Order(2)
    void testUpdateDocument() {
        CategorieDocument cat = new CategorieDocument();
        cat.setIdCategorie(1);

        Document d = new Document(
                idDocumentTest,
                "DocumentModifie",
                "test/modifie.pdf",
                new Date(System.currentTimeMillis()),
                Date.valueOf("2031-01-01"),
                cat
        );

        service.update(d);

        // هنا نتحقق مباشرة من ID
        Document docUpdate = service.getById(idDocumentTest);

        assertNotNull(docUpdate, "Document non trouvé après update");
        assertEquals("DocumentModifie", docUpdate.getNomDocument(), "Document non modifié");
    }

    @Test
    @Order(3)
    void testDeleteDocument() {
        Document d = new Document();
        d.setIdDocument(idDocumentTest);

        service.delete(d);

        Document docSupprime = service.getById(idDocumentTest);
        assertNull(docSupprime, "Document non supprimé");
    }


// ================== CLEAN DB ==================
    //  @AfterAll
    // static void cleanUp() throws Exception {
    //     service.deleteAll();
    //  }
}
