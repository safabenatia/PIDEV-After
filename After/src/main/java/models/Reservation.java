package model;

import java.time.LocalDate;

public class Reservation {

    private int id;
    private int idVoyage;
    private int idUtilisateur;
    private LocalDate dateReservation;
    private String statut;
    private int nbPersonnes;
    private double prixTotal;
    private String type;
    private String lieu;
    private String description;

    // Constructeur complet (SELECT)
    public Reservation(int id, int idVoyage, int idUtilisateur, LocalDate dateReservation, String statut,
            int nbPersonnes, double prixTotal, String type, String lieu, String description) {
        this.id = id;
        this.idVoyage = idVoyage;
        this.idUtilisateur = idUtilisateur;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.nbPersonnes = nbPersonnes;
        this.prixTotal = prixTotal;
        this.type = type;
        this.lieu = lieu;
        this.description = description;
    }

    // Constructeur (INSERT)
    public Reservation(int idVoyage, int idUtilisateur, LocalDate dateReservation, String statut, int nbPersonnes,
            double prixTotal, String type, String lieu, String description) {
        this.idVoyage = idVoyage;
        this.idUtilisateur = idUtilisateur;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.nbPersonnes = nbPersonnes;
        this.prixTotal = prixTotal;
        this.type = type;
        this.lieu = lieu;
        this.description = description;
    }

    // Existing "simpler" constructor for compatibility with current forms if needed
    public Reservation(LocalDate dateReservation, String statut, int nbPersonnes) {
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.nbPersonnes = nbPersonnes;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVoyage() {
        return idVoyage;
    }

    public void setIdVoyage(int idVoyage) {
        this.idVoyage = idVoyage;
    }

    public int getIdUtilisateur() {
        return idUtilisateur;
    }

    public void setIdUtilisateur(int idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getNbPersonnes() {
        return nbPersonnes;
    }

    public void setNbPersonnes(int nbPersonnes) {
        this.nbPersonnes = nbPersonnes;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", voyage=" + idVoyage +
                ", user=" + idUtilisateur +
                ", date=" + dateReservation +
                ", statut='" + statut + '\'' +
                ", nb=" + nbPersonnes +
                ", prix=" + prixTotal +
                ", type='" + type + '\'' +
                ", lieu='" + lieu + '\'' +
                ", desc='" + description + '\'' +
                '}';
    }
}
