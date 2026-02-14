package tn.esprit;

import models.destination;
import models.voyage;
import services.ServiceDestination;
import services.ServiceVoyage;
import utils.Mydatabase;

import java.sql.Date;
import java.util.Scanner;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        ServiceDestination sd = new ServiceDestination();
        ServiceVoyage sv = new ServiceVoyage();
        Scanner sc = new Scanner(System.in);
        int choice;
        do {
            System.out.println("\n=====menu pour destination et voyage =====");
            System.out.println("1. ajouter destination");
            System.out.println("2. afficher les destinations");
            System.out.println("3. modifier une destination");
            System.out.println("4. supprimer une destination");
            System.out.println("5. ajouter un voyage");
            System.out.println("6. afficher les voyages");
            System.out.println("7. modifier un voyage");
            System.out.println("8. supprimer un voyage");
            System.out.println("0. exit");
            System.out.print("votre choix : ");
            choice = sc.nextInt();
            sc.nextLine();
            switch (choice) {

                case 1:
                    destination d = new destination();
                    System.out.print("Pays: ");
                    d.setPays(sc.nextLine());
                    System.out.print("Ville: ");
                    d.setVille(sc.nextLine());
                    System.out.print("Continent: ");
                    d.setContinent(sc.nextLine());
                    System.out.print("Description: ");
                    d.setDescription(sc.nextLine());
                    System.out.print("Image: ");
                    d.setImage(sc.nextLine());
                    sd.add(d);
                    break;

                case 2:
                    List<destination> list = sd.getAll();
                    System.out.println("\n---- les destinations ----");
                    for (destination dest : list) {
                        System.out.println(
                                dest.getId_destination() + " | " +
                                        dest.getPays() + " | " +
                                        dest.getVille() + " | " +
                                        dest.getContinent()
                        );
                    }
                    break;

                case 3:
                    destination du = new destination();
                    System.out.print("l'id de destination a modifier : ");
                    du.setId_destination(sc.nextInt());
                    sc.nextLine();
                    System.out.print("nouvelle pays: ");
                    du.setPays(sc.nextLine());
                    System.out.print("nouvelle ville: ");
                    du.setVille(sc.nextLine());
                    System.out.print("nouvel continent: ");
                    du.setContinent(sc.nextLine());
                    System.out.print("nouvel description: ");
                    du.setDescription(sc.nextLine());
                    System.out.print("nouvelle image: ");
                    du.setImage(sc.nextLine());
                    sd.update(du);
                    break;

                case 4:
                    destination dd = new destination();

                    System.out.print("l'id destination a supprimer: ");
                    dd.setId_destination(sc.nextInt());

                    sd.delete(dd);
                    break;
                case 5:
                    voyage v = new voyage();
                    System.out.print("titre: ");
                    v.setTitre(sc.nextLine());
                    System.out.print("description: ");
                    v.setDescription(sc.nextLine());
                    System.out.print("statut: ");
                    v.setStatut(sc.nextLine());
                    System.out.print("nombre de place : ");
                    v.setNbPlaces(Integer.parseInt(sc.nextLine()));
                    System.out.print("id destination : ");
                    v.setIdDestination(Integer.parseInt(sc.nextLine()));
                    System.out.print("date debut: ");
                    v.setDateDebut(Date.valueOf(sc.nextLine()));
                    System.out.print("date fin: ");
                    v.setDateFin(Date.valueOf(sc.nextLine()));
                    System.out.print("prix: ");
                    v.setPrix(Double.parseDouble(sc.nextLine()));
                    sv.add(v);
                    break;

                case 6:
                    List<voyage> list1 = sv.getAll();
                    System.out.println("\n---- les destinations ----");
                    for (voyage voy : list1) {
                        System.out.println(
                                voy.getIdVoyage() + " | " +
                                        voy.getNbPlaces() + " | " +
                                        voy.getTitre() + " | " +
                                        voy.getPrix()  + " | " +
                                        voy.getDateDebut()  + " | " +
                                        voy.getDateFin()  + " | " +
                                        voy.getIdDestination()  + " | " +
                                        voy.getStatut()  + " | " +
                                        voy.getDescription()
                        );
                    }
                    break;

                case 7:
                    voyage vo = new voyage();
                    System.out.print("l'id de voyage a modifier : ");
                    vo.setIdVoyage(sc.nextInt());
                    sc.nextLine();
                    System.out.print("titre: ");
                    vo.setTitre(sc.nextLine());
                    System.out.print("description: ");
                    vo.setDescription(sc.nextLine());
                    System.out.print("statut: ");
                    vo.setStatut(sc.nextLine());
                    System.out.print("nombre de place : ");
                    vo.setNbPlaces(Integer.parseInt(sc.nextLine()));
                    System.out.print("id destination : ");
                    vo.setIdDestination(Integer.parseInt(sc.nextLine()));
                    System.out.print("date debut: ");
                    vo.setDateDebut(Date.valueOf(sc.nextLine()));
                    System.out.print("date fin: ");
                    vo.setDateFin(Date.valueOf(sc.nextLine()));
                    System.out.print("prix: ");
                    vo.setPrix(Double.parseDouble(sc.nextLine()));
                    sv.update(vo);
                    break;

                case 8:
                    voyage voya = new voyage();

                    System.out.print("l'id voyage a supprimer: ");
                    voya.setIdVoyage(sc.nextInt());

                    sv.delete(voya);
                    break;

                case 0:
                    System.out.println("au revoir ");
                    break;

                default:
                    System.out.println("choix invalide");
            }

        } while (choice != 0);

        sc.close();
    }
}
