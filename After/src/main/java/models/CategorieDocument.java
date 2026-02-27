package models;

public class CategorieDocument {

    private int idCategorie;
    private String libelle;
    private String description;

    // Constructeurs
    public CategorieDocument() { }

    public CategorieDocument(int idCategorie, String libelle, String description) {
        this.idCategorie = idCategorie;
        this.libelle = libelle;
        this.description = description;
    }

    public CategorieDocument(int idCategorie, String libelle) {
        this.idCategorie = idCategorie;
        this.libelle = libelle;
    }

    // ===== Getters =====
    public int getIdCategorie() {
        return idCategorie;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getDescription() {
        return description;
    }

    // ===== Setters =====
    public void setIdCategorie(int idCategorie) {
        this.idCategorie = idCategorie;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Pour afficher dans ComboBox ou TableView
    @Override
    public String toString() {
        return libelle;
    }
}
