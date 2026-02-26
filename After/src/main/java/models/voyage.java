package models;

import java.util.Date;

public class voyage {
    private int idVoyage,nbPlaces,idDestination;
    private String titre,description,image;
    private Date dateDebut,dateFin;
    private double prix;

    public voyage() {
    }

    public voyage(int idVoyage, int nbPlaces, int idDestination, String titre, String description, String statut, Date dateDebut, Date dateFin, double prix) {
        this.idVoyage = idVoyage;
        this.nbPlaces = nbPlaces;
        this.idDestination = idDestination;
        this.titre = titre;
        this.description = description;
        this.image = statut;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.prix = prix;
    }

    public int getIdVoyage() {
        return idVoyage;
    }

    public void setIdVoyage(int idVoyage) {
        this.idVoyage = idVoyage;
    }

    public int getNbPlaces() {
        return nbPlaces;
    }

    public void setNbPlaces(int nbPlaces) {
        this.nbPlaces = nbPlaces;
    }

    public int getIdDestination() {
        return idDestination;
    }

    public void setIdDestination(int idDestination) {
        this.idDestination = idDestination;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String statut) {
        this.image = statut;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin() {
        return dateFin;
    }

    public void setDateFin(Date dateFin) {
        this.dateFin = dateFin;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "voyage{" +
                "idVoyage=" + idVoyage +
                ", nbPlaces=" + nbPlaces +
                ", idDestination=" + idDestination +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", prix=" + prix +
                "}\n";
    }
}
