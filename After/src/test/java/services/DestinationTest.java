package services;
import models.destination;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DestinationTest {
        static ServiceDestination service;
        @BeforeAll
        static void setup() {
            service = new ServiceDestination();
        }
        @Test
        @Order(1)
        void testAjouterDestination() throws SQLException {
            destination d = new destination(10,"saudia arabia","dammam","asia","descript","img");
            service.add(d);
            List<destination> dest = service.getAll();
            assertFalse(dest.isEmpty());
            assertTrue(
                dest.stream().anyMatch(pers ->
                        pers.getPays().equals("saudia arabia")
                )
            );
        }
        @Test
        @Order(2)
        void testModifierDestination() throws SQLException {
            destination des = new destination();
            des.setId_destination(10);
            des.setPays("france");
            des.setVille("paris");
            des.setContinent("europe");
            des.setDescription("description");
            des.setImage("image");
            service.update(des);
            List<destination> desti = service.getAll();
            boolean trouve = desti.stream()
                    .anyMatch(per -> per.getPays().equals("france"));
            assertTrue(trouve);
        }
        @Test
        @Order(3)
        void testSupprimerDestination() throws SQLException {
            destination des = new destination();
            des.setId_destination(10);
            service.delete(des);
            List<destination> dest = service.getAll();
            boolean existe = dest.stream()
                    .anyMatch(p -> p.getId_destination() == 10);
            assertFalse(existe);
        }
        @AfterAll
        static void cleanUp() throws SQLException {
            service.deleteAll();
        }

}

