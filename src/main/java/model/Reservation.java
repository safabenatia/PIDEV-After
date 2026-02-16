package model;

import java.time.LocalDate;


public class Reservation {

    private int id;
    private LocalDate dateReservation;
    private String statut;
    private int nbPersonnes;

    // Constructeur INSERT
    public Reservation(LocalDate dateReservation, String statut, int nbPersonnes) {
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.nbPersonnes = nbPersonnes;
    }

    // Constructeur SELECT
    public Reservation(int id, LocalDate dateReservation, String statut, int nbPersonnes) {
        this.id = id;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.nbPersonnes = nbPersonnes;
    }

    // Getters
    public int getId() { return id; }
    public LocalDate getDateReservation() { return dateReservation; }
    public String getStatut() { return statut; }
    public int getNbPersonnes() { return nbPersonnes; }

    public void setId(int id) {
        this.id = id;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public void setNbPersonnes(int nbPersonnes) {
        this.nbPersonnes = nbPersonnes;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", date=" + dateReservation +
                ", statut='" + statut + '\'' +
                ", personnes=" + nbPersonnes +
                '}';
    }
}
