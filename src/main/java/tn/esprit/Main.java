package tn.esprit;

import models.CategorieDocument;
import models.Document;
import services.serviceCategorieDocument;
import services.serviceDocument;

import java.sql.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        serviceCategorieDocument sc = new serviceCategorieDocument();
        serviceDocument sd = new serviceDocument();

        // ================== ADD CATEGORIE ==================
        System.out.println("===== ADD CATEGORIE =====");

        CategorieDocument cat = new CategorieDocument();
        cat.setLibelle("Administratif");
        cat.setDescription("Documents administratifs");

        sc.add(cat);

        // ================== GET ALL CATEGORIES ==================
        System.out.println("\n===== LISTE DES CATEGORIES =====");
        List<CategorieDocument> categories = sc.getAll();
        categories.forEach(System.out::println);

        // ⚠️ نفترضو آخر catégorie هي اللي باش نستعملوها
        int idCategorie = categories.get(categories.size() - 1).getIdCategorie();

        // ================== ADD DOCUMENT ==================
        System.out.println("\n===== ADD DOCUMENT =====");

        CategorieDocument catDoc = new CategorieDocument();
        catDoc.setIdCategorie(idCategorie);

        Document doc = new Document(
                0,
                "Contrat Travail",
                "docs/contrat.pdf",
                new Date(System.currentTimeMillis()),
                Date.valueOf("2030-12-31"),
                catDoc
        );

       // sd.add(doc);

        // ================== GET ALL DOCUMENTS ==================
        System.out.println("\n===== LISTE DES DOCUMENTS =====");
        List<Document> documents = sd.getAll();
        documents.forEach(System.out::println);

        // ⚠️ آخر document مضاف
        int idDocument = documents.get(documents.size() - 1).getIdDocument();

        // ================== UPDATE DOCUMENT ==================
        System.out.println("\n===== UPDATE DOCUMENT =====");

        doc.setIdDocument(idDocument);
        doc.setNomDocument("Contrat Travail Modifié");
        doc.setCheminFichier("docs/contrat_v2.pdf");

        sd.update(doc);

        // ================== GET ALL DOCUMENTS (AFTER UPDATE) ==================
        System.out.println("\n===== DOCUMENTS APRÈS UPDATE =====");
        sd.getAll().forEach(System.out::println);

        // ================== DELETE DOCUMENT ==================
        System.out.println("\n===== DELETE DOCUMENT =====");

        Document docToDelete = new Document();
        docToDelete.setIdDocument(idDocument);
        //sd.delete(docToDelete);

        System.out.println("\n===== DOCUMENTS APRÈS DELETE =====");
        sd.getAll().forEach(System.out::println);

        // ================== DELETE CATEGORIE ==================
        System.out.println("\n===== DELETE CATEGORIE =====");

        CategorieDocument catToDelete = new CategorieDocument();
        catToDelete.setIdCategorie(idCategorie);
        //sc.delete(catToDelete);

        System.out.println("\n===== CATEGORIES APRÈS DELETE =====");
        sc.getAll().forEach(System.out::println);
    }
}
