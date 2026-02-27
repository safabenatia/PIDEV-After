package services;

public class DocumentClassifier {

    public String detectCategory(String texte) {

        if (texte == null) return "Autre";

        texte = texte.toLowerCase();

        if (texte.contains("billet") || texte.contains("boarding"))
            return "BILLET";

        if (texte.contains("assurance"))
            return "ASSURANCE";

        if (texte.contains("hotel") || texte.contains("reservation"))
            return "HEBERGEMENT";

        if (texte.contains("visa"))
            return "Autorisation d'entrée";

        if (texte.contains("passport") || texte.contains("identite"))
            return "Documents d'identité";

        return "Autre";
    }
}