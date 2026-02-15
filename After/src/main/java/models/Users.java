package models;

public class Users {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Role role; // ADMIN ou VOYAGEUR
    private String photoProfilUrl; // URL de la photo
    private String telephone;

    public Users (){

    }

    public Users(int id, String nom, String prenom, String email, String motDePasse, String photoProfilUrl) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.photoProfilUrl = photoProfilUrl;
        this.telephone = telephone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhotoProfilUrl() {
        return photoProfilUrl;
    }

    public void setPhotoProfilUrl(String photoProfilUrl) {
        this.photoProfilUrl = photoProfilUrl;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }


}
