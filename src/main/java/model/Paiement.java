package model;

import java.util.Objects;

public class Paiement {

    private int id;
    private String reference;
    private double montant;
    private String methode;
    private int idReservation;

    // Constructeur INSERT
    public Paiement(String reference, double montant, String methode, int idReservation) {
        this.reference = reference;
        this.montant = montant;
        this.methode = methode;
        this.idReservation = idReservation;
    }


    public Paiement() {

    }


    // Constructeur SELECT
    public Paiement(int id, String reference, double montant, String methode, int idReservation) {
        this.id = id;
        this.reference = reference;
        this.montant = montant;
        this.methode = methode;
        this.idReservation = idReservation;
    }

    // Getters
    public int getId() { return id; }
    public String getReference() { return reference; }
    public double getMontant() { return montant; }
    public String getMethode() { return methode; }
    public int getIdReservation() { return idReservation; }

    public void setId(int id) {
        this.id = id;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public void setMethode(String methode) {
        this.methode = methode;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Paiement paiement = (Paiement) o;
        return id == paiement.id && Double.compare(montant, paiement.montant) == 0 && idReservation == paiement.idReservation && Objects.equals(reference, paiement.reference) && Objects.equals(methode, paiement.methode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, reference, montant, methode, idReservation);
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", montant=" + montant +
                ", methode='" + methode + '\'' +
                ", idReservation=" + idReservation +
                '}';
    }
}
