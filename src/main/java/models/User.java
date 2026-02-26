package models;
public class User {
    private int id;
    private String email;
    private String type_utilisateur; // pas "role"
    private String nom;
    private String prenom;

    public User() {}

    public User(int id, String email, String type_utilisateur) {
        this.id = id;
        this.email = email;
        this.type_utilisateur = type_utilisateur;
    }

    public String getRole() {
        return type_utilisateur; // retourne type_utilisateur
    }

    public String getEmail() { return email; }
    public int getId() { return id; }
}