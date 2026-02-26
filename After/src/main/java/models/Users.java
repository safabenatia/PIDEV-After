package models;

// Classe mère abstraite
public abstract class Users {

    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String photoProfilUrl;
    private String telephone;
    private boolean verified = false;               // is_verified
    private String verificationToken;               // verification_token
    private java.time.LocalDateTime verificationExpiry; // verification_expiry

    // Constructeur vide
    public Users() {
    }

    // Constructeur commun (appelé par les sous-classes avec super())
    public Users(int id, String nom, String prenom, String email,
                 String motDePasse, String photoProfilUrl, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.photoProfilUrl = photoProfilUrl;
        this.telephone = telephone;
    }

    // Getters et setters inchangés
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getPhotoProfilUrl() {
        return photoProfilUrl;
    }

    public void setPhotoProfilUrl(String photoProfilUrl) {
        this.photoProfilUrl = photoProfilUrl;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    // Méthode abstraite (exemple) → les sous-classes devront l'implémenter
    public abstract String getTypeUtilisateur();

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public java.time.LocalDateTime getVerificationExpiry() {
        return verificationExpiry;
    }

    public void setVerificationExpiry(java.time.LocalDateTime verificationExpiry) {
        this.verificationExpiry = verificationExpiry;
    }
}