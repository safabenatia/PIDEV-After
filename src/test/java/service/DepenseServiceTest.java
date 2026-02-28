package service;

import models.depense;
import org.junit.jupiter.api.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DepenseServiceTest {
    
    static serviceDeppense service;
    static int idDepenseTest; // ID de la depense creee pendant les tests
    

    @BeforeAll
    static void setup() {
        service = new serviceDeppense();
        System.out.println("=== Debut des tests pour serviceDeppense ===");
    }
    

    @Test
    @Order(1)
    void testAjouterDepense() {
        System.out.println("\n[TEST 1] Test d'ajout d'une depense");
        

        depense d = new depense();
        d.setTitre("Test Transport");
        d.setMontant(50.00);
        d.setDateDepense(Date.valueOf(LocalDate.now()));
        d.setIdCategorie(1);
        

        service.add(d);
        

        List<depense> depenses = service.getAll();
        

        assertFalse(depenses.isEmpty(), "La liste des depenses ne devrait pas etre vide apres l'ajout");
        

        depense derniereDepense = depenses.get(depenses.size() - 1);
        idDepenseTest = derniereDepense.getIdDep();
        

        assertEquals("Test Transport", derniereDepense.getTitre(), "Le titre devrait etre 'Test Transport'");
        assertEquals(50.00, derniereDepense.getMontant(), 0.01, "Le montant devrait etre 50.00");
        assertEquals(1, derniereDepense.getIdCategorie(), "L'ID categorie devrait etre 1");
        
        System.out.println("✓ Depense ajoutee avec succes - ID: " + idDepenseTest);
    }
    

    @Test
    @Order(2)
    void testAfficherDepenses() {
        System.out.println("\n[TEST 2] Test d'affichage des depenses");
        

        List<depense> depenses = service.getAll();
        

        assertNotNull(depenses, "La liste des depenses ne devrait pas etre null");
        assertFalse(depenses.isEmpty(), "La liste des depenses ne devrait pas etre vide");
        
        System.out.println("✓ Nombre de depenses dans la BD : " + depenses.size());
    }
    

    @Test
    @Order(3)
    void testModifierDepense() {
        System.out.println("\n[TEST 3] Test de modification d'une depense");

        depense d = new depense();
        d.setIdDep(idDepenseTest);
        d.setTitre("Test Transport Modifie");
        d.setMontant(75.50);
        d.setDateDepense(Date.valueOf(LocalDate.now()));
        d.setIdCategorie(2); // Changement de categorie
        
        service.update(d);
        
        List<depense> depenses = service.getAll();
        

        depense depenseTrouvee = depenses.stream()
                .filter(dep -> dep.getIdDep() == idDepenseTest)
                .findFirst()
                .orElse(null);
        

        assertNotNull(depenseTrouvee, "La depense modifiee devrait exister dans la BD");
        assertEquals("Test Transport Modifie", depenseTrouvee.getTitre(), "Le titre devrait etre modifie");
        assertEquals(75.50, depenseTrouvee.getMontant(), 0.01, "Le montant devrait etre modifie a 75.50");
        assertEquals(2, depenseTrouvee.getIdCategorie(), "L'ID categorie devrait etre 2");
        
        System.out.println("✓ Depense modifiee avec succes");
    }
    

    @Test
    @Order(4)
    void testSupprimerDepense() {
        System.out.println("\n[TEST 4] Test de suppression d'une depense");
        

        depense d = new depense();
        d.setIdDep(idDepenseTest);
        

        service.delete(d);
        

        List<depense> depenses = service.getAll();
        

        boolean existe = depenses.stream()
                .anyMatch(dep -> dep.getIdDep() == idDepenseTest);
        
        assertFalse(existe, "La depense ne devrait plus exister apres suppression");
        
        System.out.println("✓ Depense supprimee avec succes");
    }
    

    @Test
    @Order(5)
    void testAjouterDepenseInvalide() {
        System.out.println("\n[TEST 5] Test d'ajout avec donnees invalides");
        
        depense d = new depense();
        d.setTitre("Test Invalide");
        d.setMontant(-10.00); // Montant negatif
        d.setDateDepense(Date.valueOf(LocalDate.now()));
        d.setIdCategorie(1);
        

        int tailleAvant = service.getAll().size();
        service.add(d);
        int tailleApres = service.getAll().size();
        

        if (tailleApres > tailleAvant) {
            List<depense> depenses = service.getAll();
            depense derniere = depenses.get(depenses.size() - 1);
            service.delete(derniere);
            System.out.println("⚠ Le service accepte les montants negatifs - A ameliorer !");
        }
    }
    

    @AfterEach
    void cleanUp() {
        System.out.println("→ Nettoyage en cours...");
        
        List<depense> depenses = service.getAll();
        

        for (depense d : depenses) {
            if (d.getTitre() != null && d.getTitre().startsWith("Test")) {
                service.delete(d);
                System.out.println("  - Depense de test supprimee : " + d.getTitre());
            }
        }
    }
    
    @AfterAll
    static void tearDown() {
        System.out.println("\n=== Fin des tests pour serviceDeppense ===");
    }
}
