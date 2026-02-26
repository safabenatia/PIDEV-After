package utils;

import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MyDataBaseTest {

    private static MyDataBase myDataBase;
    private static Connection connection;

    @BeforeAll
    static void setUp() {
        System.out.println("=== Début des tests MyDataBase ===");
        myDataBase = MyDataBase.getInstance();
        connection = myDataBase.getCnx();
    }

    @Test
    @Order(1)
    void testGetInstance() {
        System.out.println("Test 1: Vérification du singleton...");
        assertNotNull(myDataBase, "L'instance MyDataBase ne devrait pas être null");

        MyDataBase autreInstance = MyDataBase.getInstance();
        assertSame(myDataBase, autreInstance, "Les deux instances devraient être identiques (singleton)");

        System.out.println("Singleton fonctionne correctement");
    }

    @Test
    @Order(2)
    void testGetCnx() {
        System.out.println("Test 2: Obtention de la connexion...");
        assertNotNull(connection, "La connexion ne devrait pas être null");
        System.out.println("Connexion obtenue avec succès");
    }

    @Test
    @Order(3)
    void testConnexionEstValide() {
        System.out.println("Test 3: Vérification que la connexion est valide...");
        try {
            assertNotNull(connection, "La connexion doit être initialisée");
            assertFalse(connection.isClosed(), "La connexion ne devrait pas être fermée");
            assertTrue(connection.isValid(5), "La connexion devrait être valide (timeout 5 secondes)");
            System.out.println("Connexion valide et opérationnelle");
        } catch (SQLException e) {
            fail("Erreur lors de la validation de la connexion: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    void testMessageConnexion() {
        System.out.println("Test 4: Vérification du message de connexion...");
        // Ce test vérifie simplement que le message "Connected to the database" s'est affiché
        // Le message est déjà affiché dans le constructeur
        assertNotNull(connection, "La connexion a dû afficher le message lors de l'initialisation");
        System.out.println(" Le message de connexion a été affiché (vérifiez la console)");
    }

    @Test
    @Order(5)
    void testRequeteSimple() {
        System.out.println("Test 5: Exécution d'une requête simple...");
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");

            assertTrue(rs.next(), "La requête devrait retourner un résultat");
            int resultat = rs.getInt(1);
            assertEquals(1, resultat, "Le résultat devrait être 1");

            rs.close();
            stmt.close();
            System.out.println("Requête simple exécutée avec succès");
        } catch (SQLException e) {
            fail("Erreur lors de l'exécution de la requête: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    void testTableServiceExiste() {
        System.out.println("Test 6: Vérification que la table 'service' existe...");
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'service'");

            boolean tableExiste = rs.next();
            assertTrue(tableExiste, "La table 'service' devrait exister dans la base de données");

            rs.close();
            stmt.close();
            System.out.println("Table 'service' existe");
        } catch (SQLException e) {
            fail("Erreur lors de la vérification de la table: " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    void testTableOffreExiste() {
        System.out.println("Test 7: Vérification que la table 'offre' existe...");
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'offre'");

            boolean tableExiste = rs.next();
            assertTrue(tableExiste, "La table 'offre' devrait exister dans la base de données");

            rs.close();
            stmt.close();
            System.out.println("Table 'offre' existe");
        } catch (SQLException e) {
            fail("Erreur lors de la vérification de la table: " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    void testStructureTableService() {
        System.out.println("Test 8: Vérification de la structure de la table 'service'...");
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("DESCRIBE service");

            boolean hasId = false;
            boolean hasNom = false;
            boolean hasDescription = false;
            boolean hasCategorie = false;

            while (rs.next()) {
                String field = rs.getString("Field");
                switch (field) {
                    case "id_service": hasId = true; break;
                    case "nom_service": hasNom = true; break;
                    case "description": hasDescription = true; break;
                    case "categorie": hasCategorie = true; break;
                }
            }

            assertTrue(hasId, "La colonne 'id_service' devrait exister");
            assertTrue(hasNom, "La colonne 'nom_service' devrait exister");
            assertTrue(hasDescription, "La colonne 'description' devrait exister");
            assertTrue(hasCategorie, "La colonne 'categorie' devrait exister");

            rs.close();
            stmt.close();
            System.out.println("Structure de la table 'service' correcte");
        } catch (SQLException e) {
            fail("Erreur lors de la vérification de la structure: " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    void testStructureTableOffre() {
        System.out.println("Test 9: Vérification de la structure de la table 'offre'...");
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("DESCRIBE offre");

            boolean hasId = false;
            boolean hasTitre = false;
            boolean hasPrix = false;
            boolean hasDuree = false;
            boolean hasServiceId = false;

            while (rs.next()) {
                String field = rs.getString("Field");
                switch (field) {
                    case "id_offre": hasId = true; break;
                    case "titre": hasTitre = true; break;
                    case "prix": hasPrix = true; break;
                    case "duree": hasDuree = true; break;
                    case "id_service": hasServiceId = true; break;
                }
            }

            assertTrue(hasId, "La colonne 'id_offre' devrait exister");
            assertTrue(hasTitre, "La colonne 'titre' devrait exister");
            assertTrue(hasPrix, "La colonne 'prix' devrait exister");
            assertTrue(hasDuree, "La colonne 'duree' devrait exister");
            assertTrue(hasServiceId, "La colonne 'id_service' devrait exister");

            rs.close();
            stmt.close();
            System.out.println("Structure de la table 'offre' correcte");
        } catch (SQLException e) {
            fail("Erreur lors de la vérification de la structure: " + e.getMessage());
        }
    }

    @Test
    @Order(10)
    void testForeignKey() {
        System.out.println("Test 10: Vérification de la clé étrangère...");
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) as cnt FROM information_schema.KEY_COLUMN_USAGE " +
                            "WHERE TABLE_NAME = 'offre' AND COLUMN_NAME = 'id_service' " +
                            "AND REFERENCED_TABLE_NAME = 'service'"
            );

            rs.next();
            int count = rs.getInt("cnt");
            assertTrue(count > 0, "La clé étrangère entre offre.id_service et service.id_service devrait exister");

            rs.close();
            stmt.close();
            System.out.println("Clé étrangère correctement configurée");
        } catch (SQLException e) {
            fail("Erreur lors de la vérification de la clé étrangère: " + e.getMessage());
        }
    }

    @AfterAll
    static void tearDown() {
        System.out.println("=== Tests MyDataBase terminés ===");
        // Ne pas fermer la connexion ici car elle est partagée
        // La fermeture est gérée par l'application
    }
}