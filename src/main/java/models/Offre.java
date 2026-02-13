package models;

public class Offre {

    private int id_offre;
    private String titre;
    private double prix;
    private int duree;
    private int id_service;

    public Offre() {}

    public Offre(int id_offre, String titre, double prix, int duree, int id_service) {
        this.id_offre = id_offre;
        this.titre = titre;
        this.prix = prix;
        this.duree = duree;
        this.id_service = id_service;
    }

    public int getId_offre() { return id_offre; }
    public void setId_offre(int id_offre) { this.id_offre = id_offre; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }

    public int getDuree() { return duree; }
    public void setDuree(int duree) { this.duree = duree; }

    public int getId_service() { return id_service; }
    public void setId_service(int id_service) { this.id_service = id_service; }
}
