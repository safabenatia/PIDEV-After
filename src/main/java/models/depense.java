package models;

import java.util.Date;

public class depense {
    private int idDep;
    private String titre;
    private double montant;
    private Date dateDepense;
    private int idCategorie;

    public depense() {
    }


    public depense(int idDep, String titre, double montant, Date dateDepense, int idCategorie) {
        this.idDep = idDep;
        this.titre = titre;
        this.montant = montant;
        this.dateDepense = dateDepense;
        this.idCategorie = idCategorie;
    }


    public int getIdDep() { return idDep; }
    public void setIdDep(int idDep) { this.idDep = idDep; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public double getMontant() { return montant; }
    public void setMontant(double montant) { this.montant = montant; }

    public Date getDateDepense() { return dateDepense; }
    public void setDateDepense(Date dateDepense) { this.dateDepense = dateDepense; }

    public int getIdCategorie() { return idCategorie; }
    public void setIdCategorie(int idCategorie) { this.idCategorie = idCategorie; }

    @Override
    public String toString() {
        return "Depense [" +
                "ID=" + idDep +
                ", Titre='" + titre + '\'' +
                ", Montant=" + montant +
                ", Date=" + dateDepense +
                ", ID_Categorie=" + idCategorie +
                ']';
    }
}
