package models;
public class Activite {

    private int idActivite;
    private String nom;
    private String description;
    private String categorie;
    private String lieu;
    private double prix;

    // Constructeur sans id (pour ajout d'une nouvelle activité)
    public Activite(String nom, String description, String categorie, String lieu, double prix) {
        this.nom = nom;
        this.description = description;
        this.categorie = categorie;
        this.lieu = lieu;
        this.prix = prix;
    }

    // Constructeur avec id (pour récupérer depuis la base de données)
    public Activite(int idActivite, String nom, String description, String categorie, String lieu, double prix) {
        this.idActivite = idActivite;
        this.nom = nom;
        this.description = description;
        this.categorie = categorie;
        this.lieu = lieu;
        this.prix = prix;
    }

    // Constructeur vide
    public Activite() {}

    // Getters et setters
    public int getIdActivite() {
        return idActivite;
    }

    public void setIdActivite(int idActivite) {
        this.idActivite = idActivite;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Activite{" +
                "idActivite=" + idActivite +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", categorie='" + categorie + '\'' +
                ", lieu='" + lieu + '\'' +
                ", prix=" + prix +
                '}';
    }
}

