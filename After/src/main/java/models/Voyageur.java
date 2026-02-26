package models;

public class Voyageur extends Users {

    public Voyageur() {
        super();
    }

    public Voyageur(int id, String nom, String prenom, String email,
                    String motDePasse, String photoProfilUrl, String telephone) {
        super(id, nom, prenom, email, motDePasse, photoProfilUrl, telephone);
    }

    @Override
    public String getTypeUtilisateur() {
        return "VOYAGEUR";
    }


}