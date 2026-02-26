package models;

import java.time.LocalDate;  // ✅ IMPORT AJOUTÉ

public class Offre {

    private int id_offre;
    private String titre;
    private double prix;
    private int duree;
    private int serviceId;
    private LocalDate dateCreation;  // ✅ NOUVEAU CHAMP

    public Offre() {
        this.dateCreation = LocalDate.now();  // ✅ Date par défaut
    }

    // constructeur sans id (pour ajout)
    public Offre(String titre, double prix, int duree, int serviceId) {
        this.titre = titre;
        this.prix = prix;
        this.duree = duree;
        this.serviceId = serviceId;
        this.dateCreation = LocalDate.now();  // ✅ Date du jour
    }

    // constructeur complet (pour lecture DB)
    public Offre(int id_offre, String titre, double prix, int duree, int serviceId) {
        this.id_offre = id_offre;
        this.titre = titre;
        this.prix = prix;
        this.duree = duree;
        this.serviceId = serviceId;
        this.dateCreation = LocalDate.now();  // ✅ Date par défaut
    }

    // ✅ NOUVEAU constructeur avec date (pour lecture DB avec date)
    public Offre(int id_offre, String titre, double prix, int duree, int serviceId, LocalDate dateCreation) {
        this.id_offre = id_offre;
        this.titre = titre;
        this.prix = prix;
        this.duree = duree;
        this.serviceId = serviceId;
        this.dateCreation = dateCreation;
    }

    // Getters et Setters existants
    public int getId_offre() {
        return id_offre;
    }

    public void setId_offre(int id_offre) {
        this.id_offre = id_offre;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    // ✅ NOUVEAUX GETTER ET SETTER
    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }
}