package models;

public class destination {
    private int id_destination;
    private String pays,ville,continent,description,image;

    public destination() {
    }

    public destination(int id_destination, String pays, String ville, String continent, String description, String image) {
        this.id_destination = id_destination;
        this.pays = pays;
        this.ville = ville;
        this.continent = continent;
        this.description = description;
        this.image = image;
    }

    public int getId_destination() {
        return id_destination;
    }

    public void setId_destination(int id_destination) {
        this.id_destination = id_destination;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
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

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "destination{" +
                "id_destination=" + id_destination +
                ", pays='" + pays + '\'' +
                ", ville='" + ville + '\'' +
                ", continent='" + continent + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                "}\n";
    }
}
