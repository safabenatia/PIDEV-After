package service;

import interfaces.service;
import models.depense;
import utils.mydb;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList; 
import java.util.List;

public class serviceDeppense implements service<depense> {

    private Connection cnx;

    public serviceDeppense() {
        this.cnx = mydb.getInstance().getCnx();
    }

    @Override
    public void add(depense depense) {
        // CORRECTION : Pas de guillemets simples autour des nombres (montant et id_categorie)
        String req = "INSERT INTO `depense`(`titre`, `montant`, `date_depense`, `id_categorie`) VALUES ('"
                + depense.getTitre() + "', "
                + depense.getMontant() + ", '"
                + depense.getDateDepense() + "', "
                + depense.getIdCategorie() + ")";

        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Dépense ajoutée !");
        } catch (SQLException e) {
            System.out.println("Erreur add: " + e.getMessage());
        }
    }

    @Override
    public List<depense> getAll() {
        List<depense> depenses = new ArrayList<>();
        String req = "SELECT * FROM depense";

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);
            while (rs.next()) {
                depense d = new depense();
                d.setIdDep(rs.getInt("id_dep"));
                d.setTitre(rs.getString("titre"));
                d.setMontant(rs.getDouble("montant"));
                d.setDateDepense(rs.getDate("date_depense"));
                d.setIdCategorie(rs.getInt("id_categorie"));
                depenses.add(d);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll: " + e.getMessage());
        }
        return depenses;
    }

    @Override
    public void update(depense depense) {
        String req = "UPDATE `depense` SET `titre`='" + depense.getTitre()
                + "', `montant`=" + depense.getMontant()
                + ", `date_depense`='" + depense.getDateDepense()
                + "', `id_categorie`=" + depense.getIdCategorie()
                + " WHERE `id_dep`=" + depense.getIdDep();
        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Dépense mise à jour !");
        } catch (SQLException e) {
            System.out.println("Erreur update: " + e.getMessage());
        }
    }

    @Override
    public void delete(depense depense) {
        String req = "DELETE FROM `depense` WHERE `id_dep`=" + depense.getIdDep();
        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Dépense supprimée !");
        } catch (SQLException e) {
            System.out.println("Erreur delete: " + e.getMessage());
        }
    }
}