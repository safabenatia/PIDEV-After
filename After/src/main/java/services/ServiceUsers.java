package services;

import interfaces.Services;
import models.Admin;
import models.Users;
import models.Voyageur;
import utils.Mydatabase;
import utils.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUsers implements Services<Users> {

    private Connection cnx;

    public ServiceUsers() {
        this.cnx = Mydatabase.getInstance().getCnx();
    }

    @Override
    public void add(Users user) {
        String hashedPassword = PasswordUtil.hashPassword(user.getMotDePasse());

        // On récupère le "type" selon la classe réelle
        String type = (user instanceof Admin) ? "ADMIN" : "VOYAGEUR";

        String req = "INSERT INTO users (" +
                "nom, prenom, email, password, type_utilisateur, telephone, photo_profil, " +
                "is_verified, verification_token, verification_expiry" +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            int idx = 1;
            pstmt.setString(idx++, user.getNom());
            pstmt.setString(idx++, user.getPrenom());
            pstmt.setString(idx++, user.getEmail());
            pstmt.setString(idx++, hashedPassword);
            pstmt.setString(idx++, type);
            pstmt.setString(idx++, user.getTelephone());
            pstmt.setString(idx++, user.getPhotoProfilUrl());

            // Nouveaux champs
            pstmt.setBoolean(idx++, false);                          // verified = FALSE
            pstmt.setString(idx++, user.getVerificationToken());     // le token généré
            pstmt.setObject(idx++, user.getVerificationExpiry());    // LocalDateTime → converti auto en Timestamp

            pstmt.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("Utilisateur ajouté avec token : " + user.getVerificationToken());

        } catch (SQLException e) {
            System.err.println("Erreur ajout utilisateur : " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public List<Users> getAll() {
        List<Users> users = new ArrayList<>();
        String req = "SELECT * FROM users";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                String type = rs.getString("type_utilisateur").toUpperCase();
                Users user;

                if ("ADMIN".equals(type)) {
                    user = new Admin();
                } else {
                    user = new Voyageur();
                }

                user.setId(rs.getInt("id"));
                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotDePasse(rs.getString("password"));     // hash
                user.setTelephone(rs.getString("telephone"));
                user.setPhotoProfilUrl(rs.getString("photo_profil"));

                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Erreur getAll : " + e.getMessage());
        }
        return users;
    }

    @Override
    public void update(Users user) {
        String passwordToUse = user.getMotDePasse();
        boolean changePassword = passwordToUse != null && !passwordToUse.isEmpty();

        if (changePassword) {
            passwordToUse = PasswordUtil.hashPassword(passwordToUse);
        }

        String type = (user instanceof Admin) ? "ADMIN" : "VOYAGEUR";

        String req = "UPDATE users SET nom=?, prenom=?, email=?, " +
                (changePassword ? "password=?, " : "") +
                "type_utilisateur=?, telephone=?, photo_profil=? " +
                "WHERE id=?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            int idx = 1;
            pstmt.setString(idx++, user.getNom());
            pstmt.setString(idx++, user.getPrenom());
            pstmt.setString(idx++, user.getEmail());

            if (changePassword) {
                pstmt.setString(idx++, passwordToUse);
            }

            pstmt.setString(idx++, type);
            pstmt.setString(idx++, user.getTelephone());
            pstmt.setString(idx++, user.getPhotoProfilUrl());
            pstmt.setInt(idx, user.getId());

            pstmt.executeUpdate();
            System.out.println("Utilisateur mis à jour");
        } catch (SQLException e) {
            System.err.println("Erreur update : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Users login(String email, String plainPassword) {
        String req = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtil.checkPassword(plainPassword, storedHash)) {
                        boolean isVerified = rs.getBoolean("is_verified");
                        if (!isVerified) {
                            throw new RuntimeException("Compte non vérifié. Veuillez cliquer sur le lien reçu par email.");
                        }

                        String type = rs.getString("type_utilisateur").toUpperCase();
                        Users user = "ADMIN".equals(type) ? new Admin() : new Voyageur();

                        user.setId(rs.getInt("id"));
                        user.setNom(rs.getString("nom"));
                        user.setPrenom(rs.getString("prenom"));
                        user.setEmail(rs.getString("email"));
                        user.setMotDePasse(storedHash);
                        user.setTelephone(rs.getString("telephone"));
                        user.setPhotoProfilUrl(rs.getString("photo_profil"));
                        user.setVerified(true);  // déjà vérifié

                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur login : " + e.getMessage());
        }
        return null;
    }




    @Override
    public void delete(Users users) {
        String req = "DELETE FROM users WHERE id= " + users.getId() ;

        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Utilisateur supprimé avec succès ✅");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public boolean emailExistsSafe(String email) {
        String req = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Erreur vérification email : " + e.getMessage());
        }
        return false;
    }



    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM 'users' ";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
    // Trouver un utilisateur par son token de vérification
    public Users findByVerificationToken(String token) {
        String req = "SELECT * FROM users WHERE verification_token = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, token);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("type_utilisateur").toUpperCase();
                    Users user = "ADMIN".equals(type) ? new Admin() : new Voyageur();

                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setMotDePasse(rs.getString("password"));
                    user.setTelephone(rs.getString("telephone"));
                    user.setPhotoProfilUrl(rs.getString("photo_profil"));
                    user.setVerified(rs.getBoolean("is_verified"));
                    user.setVerificationToken(rs.getString("verification_token"));

                    Timestamp expiryTs = rs.getTimestamp("verification_expiry");
                    if (expiryTs != null) {
                        user.setVerificationExpiry(expiryTs.toLocalDateTime());
                    }

                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur findByVerificationToken : " + e.getMessage());
        }
        return null;
    }

    // Méthode de vérification (met verified = true et nettoie le token)
    public boolean verifyUser(String token) {
        Users user = findByVerificationToken(token);
        if (user == null) {
            return false;
        }

        if (user.getVerificationExpiry() != null &&
                user.getVerificationExpiry().isBefore(java.time.LocalDateTime.now())) {
            // Token expiré → on pourrait supprimer l'utilisateur ici si tu veux être strict
            return false;
        }

        String req = "UPDATE users SET " +
                "is_verified = TRUE, " +
                "verification_token = NULL, " +
                "verification_expiry = NULL " +
                "WHERE id = ?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setInt(1, user.getId());
            int rows = pstmt.executeUpdate();
            return rows == 1;
        } catch (SQLException e) {
            System.err.println("Erreur verifyUser : " + e.getMessage());
            return false;
        }
    }
}
