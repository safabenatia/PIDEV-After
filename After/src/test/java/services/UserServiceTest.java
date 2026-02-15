package services;

import models.Role;
import models.Users;
import org.junit.jupiter.api.*;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {

    static ServiceUsers service;
    static int idUserTest;

    @BeforeAll
    static void setup() {
        service = new ServiceUsers();
    }

    @Test
    @Order(1)
    void testAjouterUser() throws SQLException {
        Users u = new Users();
        u.setNom("TestNom");
        u.setPrenom("TestPrenom");
        u.setEmail("test.ajout@example.com");
        u.setMotDePasse("123456");           // sera hashé par le service
        u.setRole(Role.ADMIN);
        u.setTelephone("55123456");
        u.setPhotoProfilUrl("default.jpg");

        service.add(u);

        List<Users> users = service.getAll();

        assertFalse(users.isEmpty());
        assertTrue(users.stream().anyMatch(pers -> pers.getEmail().equals("test.ajout@example.com")));

        // on garde l'id pour les tests suivants
        for (Users user : users) {
            if (user.getEmail().equals("test.ajout@example.com")) {
                idUserTest = user.getId();
                break;
            }
        }
    }

    @Test
    @Order(2)
    void testModifierUser() throws SQLException {
        // on suppose que le test 1 a passé et qu'on a un id
        if (idUserTest == 0) {
            return; // évite erreur si premier test échoue
        }

        Users u = new Users();
        u.setId(idUserTest);
        u.setNom("NouveauNom");
        u.setPrenom("NouveauPrenom");
        u.setEmail("test.modif@example.com");
        u.setMotDePasse("nouveau123");  // sera hashé
        u.setRole(Role.ADMIN);
        u.setTelephone("98765432");
        u.setPhotoProfilUrl("newphoto.jpg");

        service.update(u);

        List<Users> users = service.getAll();

        assertTrue(users.stream()
                .anyMatch(pers -> pers.getId() == idUserTest
                        && pers.getNom().equals("NouveauNom")
                        && pers.getEmail().equals("test.modif@example.com")));
    }

    @Test
    @Order(3)
    void testSupprimerUser() throws SQLException {
       if (idUserTest == 0) {
           return;
        }

        Users u = new Users();
        u.setId(idUserTest);

        service.delete(u);

        List<Users> users = service.getAll();

        assertFalse(users.stream().anyMatch(pers -> pers.getId() == idUserTest));
    }

    @AfterAll
    static void cleanUp() throws SQLException {
        service.deleteAll();
    }
}