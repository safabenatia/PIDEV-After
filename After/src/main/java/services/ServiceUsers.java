package services;

import interfaces.Services;
import models.Role;
import models.Users;
import utils.MyDataBase;
import utils.PasswordUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServiceUsers implements Services<Users> {

    private Connection cnx;
    public ServiceUsers() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }
    @Override
    public void add(Users users) {
        // Hasher le mot de passe AVANT l'insertion
        String hashedPassword = PasswordUtil.hashPassword(users.getMotDePasse());

        String req = "INSERT INTO `users`(`nom`, `prenom`, `email`, `password`, `role`, `telephone`, `photo_profil`) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, users.getNom());
            pstmt.setString(2, users.getPrenom());
            pstmt.setString(3, users.getEmail());
            pstmt.setString(4, hashedPassword);          // ← mot de passe hashé
            pstmt.setString(5, users.getRole().toString());
            pstmt.setString(6, users.getTelephone());
            pstmt.setString(7, users.getPhotoProfilUrl());
            pstmt.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override

    public List<Users> getAll() {
        List<Users> users = new ArrayList<>();
        String req="SELECT * FROM `users`";

        try {
            Statement stn = cnx.createStatement();
            ResultSet rs = stn.executeQuery(req);

            while (rs.next()) {
                Users user = new Users();

                user.setId(rs.getInt("id")); // 🔥 OBLIGATOIRE

                user.setNom(rs.getString("nom"));
                user.setPrenom(rs.getString("prenom"));
                user.setEmail(rs.getString("email"));
                user.setMotDePasse(rs.getString("password"));
                user.setRole(Role.valueOf(rs.getString("role").toUpperCase()));
                user.setTelephone(rs.getString("telephone"));
                user.setPhotoProfilUrl(rs.getString("photo_profil"));

                users.add(user);
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return users;
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

    @Override
    public void update(Users users) {
        String passwordToUse = users.getMotDePasse();

        // Si un nouveau mot de passe est fourni, on le hashe
        if (passwordToUse != null && !passwordToUse.isEmpty()) {
            passwordToUse = PasswordUtil.hashPassword(passwordToUse);
        } else {
            // Sinon on garde l'ancien (mais en pratique tu peux demander l'ancien mdp pour vérification)
            // Pour simplifier ici, on ne change pas le mot de passe si vide
            passwordToUse = null; // ou récupérer l'ancien via une requête
        }

        String req = "UPDATE `users` SET nom=?, prenom=?, email=?, " +
                (passwordToUse != null ? "password=?, " : "") +
                "role=?, telephone=?, photo_profil=? WHERE id=?";

        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            int paramIndex = 1;
            pstmt.setString(paramIndex++, users.getNom());
            pstmt.setString(paramIndex++, users.getPrenom());
            pstmt.setString(paramIndex++, users.getEmail());

            if (passwordToUse != null) {
                pstmt.setString(paramIndex++, passwordToUse);
            }

            pstmt.setString(paramIndex++, users.getRole().toString());
            pstmt.setString(paramIndex++, users.getTelephone());
            pstmt.setString(paramIndex++, users.getPhotoProfilUrl());
            pstmt.setInt(paramIndex, users.getId());

            pstmt.executeUpdate();
            System.out.println("Utilisateur mis à jour");
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour : " + e.getMessage());
            e.printStackTrace();
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
    public Users login(String email, String plainPassword) {
        String req = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement pstmt = cnx.prepareStatement(req)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");

                // ← CORRECTION CRUCIALE : on vérifie avec BCrypt
                if (PasswordUtil.checkPassword(plainPassword, storedHash)) {
                    Users user = new Users();
                    user.setId(rs.getInt("id"));
                    user.setNom(rs.getString("nom"));
                    user.setPrenom(rs.getString("prenom"));
                    user.setEmail(rs.getString("email"));
                    user.setMotDePasse(storedHash); // on garde le hash
                    user.setRole(Role.valueOf(rs.getString("role").toUpperCase()));
                    user.setTelephone(rs.getString("telephone"));
                    user.setPhotoProfilUrl(rs.getString("photo_profil"));
                    return user;
                }
            }
            return null; // email inexistant OU mot de passe incorrect
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors du login : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public void deleteAll() throws SQLException {
        String sql = "DELETE FROM 'users' ";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}
