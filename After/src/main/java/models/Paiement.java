package model;

public class Paiement {

    private int id;
    private String reference;
    private double montant;
    private String methode;
    private int idReservation;
    private String statut;
    private java.time.LocalDateTime datePaiement;
    private String devise;

    public Paiement() {
    }

    // Constructeur complet (SELECT)
    public Paiement(int id, String reference, double montant, String methode, int idReservation, String statut,
            java.time.LocalDateTime datePaiement, String devise) {
        this.id = id;
        this.reference = reference;
        this.montant = montant;
        this.methode = methode;
        this.idReservation = idReservation;
        this.statut = statut;
        this.datePaiement = datePaiement;
        this.devise = devise;
    }

    // Constructeur (INSERT)
    public Paiement(String reference, double montant, String methode, int idReservation, String statut,
            java.time.LocalDateTime datePaiement, String devise) {
        this.reference = reference;
        this.montant = montant;
        this.methode = methode;
        this.idReservation = idReservation;
        this.statut = statut;
        this.datePaiement = datePaiement;
        this.devise = devise;
    }

    // Constructeur partiel (pour compatibilité)
    public Paiement(String reference, double montant, String methode, int idReservation) {
        this.reference = reference;
        this.montant = montant;
        this.methode = methode;
        this.idReservation = idReservation;
        this.statut = "en attente";
        this.datePaiement = java.time.LocalDateTime.now();
        this.devise = "TND";
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public double getMontant() {
        return montant;
    }

    public void setMontant(double montant) {
        this.montant = montant;
    }

    public String getMethode() {
        return methode;
    }

    public void setMethode(String methode) {
        this.methode = methode;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public java.time.LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(java.time.LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getDevise() {
        return devise;
    }

    public void setDevise(String devise) {
        this.devise = devise;
    }

    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + id +
                ", ref='" + reference + '\'' +
                ", montant=" + montant + " " + devise +
                ", methode='" + methode + '\'' +
                ", res=" + idReservation +
                ", statut='" + statut + '\'' +
                ", date=" + datePaiement +
                '}';
    }
}
