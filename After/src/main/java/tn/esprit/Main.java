package tn.esprit;


import models.Users;

import services.ServiceUsers;


import java.util.List;

public class Main {

    public static void main(String[] args) {




        ServiceUsers us = new ServiceUsers();

        Users u = new Users();
        //u.setNom("amine");
        //u.setPrenom("ahmed");
        u.setEmail("aye@gmail.com");
        //u.setMotDePasse("shy123");
        //u.setRole(Role.ADMIN); // ENUM
       // u.setTelephone("1234567890");
       // u.setPhotoProfilUrl("shipa.png");

        //us.add(u);
        //System.out.println("Utilisateur ajouté avec succès !");




        //us.update(u);
        //System.out.println("Utilisateur modifié ✅");

        us.delete(u);
        System.out.println("Utilisateur supprimé ✅");


        List<Users> utilisateurs = us.getAll();
        System.out.println("===== LISTE DES UTILISATEURS =====");
        for (Users user : utilisateurs) {
            System.out.println(
                    "ID: " + user.getId() +
                            " | Nom: " + user.getNom() +
                            " | Prénom: " + user.getPrenom() +
                            " | Email: " + user.getEmail() +
                            " | Role: " + user.getRole()+
                            " | Téléphone: " + user.getTelephone() +
                            " | Photo: " + user.getPhotoProfilUrl()
            );
        }
    }
}
