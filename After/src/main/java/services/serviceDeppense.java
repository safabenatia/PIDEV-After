package service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import interfaces.Services;
import models.depense;
import utils.MyDataBase;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class serviceDeppense implements Services<depense> {

    private Connection cnx;

    public serviceDeppense() {
        this.cnx = MyDataBase.getInstance().getCnx();
    }

    @Override
    public void add(depense depense) {
        String req = "INSERT INTO `depense`(`titre`, `montant`, `date_depense`, `id_categorie`) VALUES ('"
                + depense.getTitre() + "', "
                + depense.getMontant() + ", '"
                + depense.getDateDepense() + "', "
                + depense.getIdCategorie() + ")";

        try {
            Statement stm = cnx.createStatement();
            stm.executeUpdate(req);
            System.out.println("Depense ajoutee !");
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
            System.out.println("Depense mise a jour !");
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
            System.out.println("Depense supprimee !");
        } catch (SQLException e) {
            System.out.println("Erreur delete: " + e.getMessage());
        }
    }

    // ========== VERIFICATION BUDGET ==========

    public boolean verifierDepassementBudget(int idCategorie, double budgetMax) {
        double totalDepenses = getTotalParCategorie(idCategorie);
        System.out.println("Total depenses categorie #" + idCategorie + ": " + totalDepenses + " DT");
        System.out.println("Budget max: " + budgetMax + " DT");
        return totalDepenses > budgetMax;
    }

    public double getTotalParCategorie(int idCategorie) {
        double total = 0;
        String req = "SELECT SUM(montant) as total FROM depense WHERE id_categorie = " + idCategorie;

        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery(req);

            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Erreur getTotalParCategorie: " + e.getMessage());
        }

        return total;
    }

    // ========== TRI ==========

    public List<depense> trier(String critere, String ordre) {
        List<depense> depenses = new ArrayList<>();

        if (!critere.equals("date") && !critere.equals("montant")) {
            critere = "date";
        }

        if (!ordre.equals("ASC") && !ordre.equals("DESC")) {
            ordre = "ASC";
        }

        String colonneSQL = critere.equals("date") ? "date_depense" : "montant";
        String req = "SELECT * FROM depense ORDER BY " + colonneSQL + " " + ordre;

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

            System.out.println("Tri effectue: " + critere + " " + ordre + " - " + depenses.size() + " depenses");

        } catch (SQLException e) {
            System.out.println("Erreur tri: " + e.getMessage());
        }

        return depenses;
    }

    public List<depense> trierParDate(boolean croissant) {
        return trier("date", croissant ? "ASC" : "DESC");
    }

    public List<depense> trierParMontant(boolean croissant) {
        return trier("montant", croissant ? "ASC" : "DESC");
    }
}