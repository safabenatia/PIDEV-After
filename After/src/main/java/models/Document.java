package models;

import java.sql.Date;

public class Document {  // ✅ majuscule D

    private int idDocument;
    private String nomDocument;
    private String cheminFichier;
    private Date dateAjout;
    private Date dateExpiration;
    private CategorieDocument categorie;

    // Constructeur principal
    public Document(int idDocument, String nomDocument, String cheminFichier, Date dateAjout, Date dateExpiration, CategorieDocument categorie) {
        this.idDocument = idDocument;
        this.nomDocument = nomDocument;
        this.cheminFichier = cheminFichier;
        this.dateAjout = dateAjout;
        this.dateExpiration = dateExpiration;
        this.categorie = categorie;
    }

    public Document() {

    }


    // Getters et Setters
    public int getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(int idDocument) {
        this.idDocument = idDocument;
    }

    public String getNomDocument() {
        return nomDocument;
    }

    public void setNomDocument(String nomDocument) {
        this.nomDocument = nomDocument;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public CategorieDocument getCategorie() {
        return categorie;
    }

    public void setCategorie(CategorieDocument categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        return "Document{" +
                "idDocument=" + idDocument +
                ", nomDocument='" + nomDocument + '\'' +
                ", cheminFichier='" + cheminFichier + '\'' +
                ", dateAjout=" + dateAjout +
                ", dateExpiration=" + dateExpiration +
                ", categorie=" + categorie +
                '}';
    }

    public void setId(int id) {
    }
}
