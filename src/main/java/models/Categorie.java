package models;

import java.util.Date;

public class Categorie {
    private int idCat;
    private String nomCategorie;
    private String description;
    private String iconeUrl;

    public Categorie() {
    }

    public Categorie(int idCat, String nomCategorie, String description, String iconeUrl) {
        this.idCat = idCat;
        this.nomCategorie = nomCategorie;
        this.description = description;
        this.iconeUrl = iconeUrl;
    }

    public Categorie(String nomCategorie, String description, String iconeUrl) {
        this.nomCategorie = nomCategorie;
        this.description = description;
        this.iconeUrl = iconeUrl;
    }

    public int getIdCat() {
        return idCat;
    }

    public void setIdCat(int idCat) {
        this.idCat = idCat;
    }

    public String getNomCategorie() {
        return nomCategorie;
    }

    public void setNomCategorie(String nomCategorie) {
        this.nomCategorie = nomCategorie;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconeUrl() {
        return iconeUrl;
    }

    public void setIconeUrl(String iconeUrl) {
        this.iconeUrl = iconeUrl;
    }

    @Override
    public String toString() {
        return nomCategorie; // Pour afficher dans le ComboBox
    }
}
