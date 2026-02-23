package models;

public class Admin extends Users {

    public Admin() {
        super();
    }

    public Admin(int id, String nom, String prenom, String email,
                 String motDePasse, String photoProfilUrl, String telephone) {
        super(id, nom, prenom, email, motDePasse, photoProfilUrl, telephone);
    }

    @Override
    public String getTypeUtilisateur() {
        return "ADMIN";
    }


}