package models;

public class Service {
    private int id_service;
    private String nom_service;
    private String description;
    private String categorie;

  public Service () {

  }

    public Service(int id_service, String nom_service, String description, String categorie) {
        this.id_service = id_service;
        this.nom_service = nom_service;
        this.description = description;
        this.categorie = categorie;
    }

    public int getId_service() {
        return id_service;
    }

    public void setId_service(int id_service) {
        this.id_service = id_service;
    }

    public String getNom_service() {
        return nom_service;
    }

    public void setNom_service(String nom_service) {
        this.nom_service = nom_service;
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
}

