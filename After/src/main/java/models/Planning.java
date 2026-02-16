package models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Planning {

    private int idPlanning;
    private int idUser;       // temporaire = 1
    private int idActivite;
    private LocalDate dateActivite;
    private LocalTime heureDebut;
    private int duree;        // en minutes

    // Constructeur sans id (ajout)
    public Planning(int idUser, int idActivite, LocalDate dateActivite, LocalTime heureDebut, int duree) {
        this.idUser = idUser;
        this.idActivite = idActivite;
        this.dateActivite = dateActivite;
        this.heureDebut = heureDebut;
        this.duree = duree;
    }

    // Constructeur avec id (récup depuis DB)
    public Planning(int idPlanning, int idUser, int idActivite, LocalDate dateActivite, LocalTime heureDebut, int duree) {
        this.idPlanning = idPlanning;
        this.idUser = idUser;
        this.idActivite = idActivite;
        this.dateActivite = dateActivite;
        this.heureDebut = heureDebut;
        this.duree = duree;
    }

    // Constructeur vide
    public Planning() {}

    // Getters & Setters
    public int getIdPlanning() { return idPlanning; }
    public void setIdPlanning(int idPlanning) { this.idPlanning = idPlanning; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public int getIdActivite() { return idActivite; }
    public void setIdActivite(int idActivite) { this.idActivite = idActivite; }

    public LocalDate getDateActivite() { return dateActivite; }
    public void setDateActivite(LocalDate dateActivite) { this.dateActivite = dateActivite; }

    public LocalTime getHeureDebut() { return heureDebut; }
    public void setHeureDebut(LocalTime heureDebut) { this.heureDebut = heureDebut; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    @Override
    public String toString() {
        return "Planning{" +
                "idPlanning=" + idPlanning +
                ", idUser=" + idUser +
                ", idActivite=" + idActivite +
                ", dateActivite=" + dateActivite +
                ", heureDebut=" + heureDebut +
                ", duree=" + duree +
                '}';
    }
}


