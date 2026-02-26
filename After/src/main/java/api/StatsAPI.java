package api;

import models.Offre;
import models.Service;
import services.OffreService;
import services.ServiceService;

import java.util.*;
import java.util.stream.Collectors;

public class StatsAPI {

    private final ServiceService serviceService;
    private final OffreService offreService;

    public StatsAPI() {
        this.serviceService = new ServiceService();
        this.offreService = new OffreService();
    }

    // ==================== STATISTIQUES DE BASE ====================

    public int getTotalServices() {
        return serviceService.getAll().size();
    }

    public int getTotalOffres() {
        return offreService.getAll().size();
    }

    public double getPrixMoyenOffres() {
        return offreService.getAll().stream()
                .mapToDouble(Offre::getPrix)
                .average()
                .orElse(0.0);
    }

    // ==================== STATISTIQUES PAR SERVICE ====================

    public Map<String, Integer> getOffresParService() {
        Map<String, Integer> stats = new HashMap<>();
        for (Service s : serviceService.getAll()) {
            stats.put(s.getNom_service(), 0);
        }
        for (Offre o : offreService.getAll()) {
            Service service = serviceService.getById(o.getServiceId());
            if (service != null) {
                stats.put(service.getNom_service(), stats.get(service.getNom_service()) + 1);
            }
        }
        return stats;
    }

    public Map<String, Integer> getServicesParCategorie() {
        Map<String, Integer> stats = new HashMap<>();
        for (Service s : serviceService.getAll()) {
            stats.put(s.getCategorie(), stats.getOrDefault(s.getCategorie(), 0) + 1);
        }
        return stats;
    }

    public Map<Integer, Double> getPrixOffresParId() {
        return offreService.getAll().stream()
                .collect(Collectors.toMap(
                        Offre::getId_offre,
                        Offre::getPrix,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    public String getServiceLePlusPopulaire() {
        return getOffresParService().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Aucun");
    }

    // ==================== NOUVELLES MÉTHODES POUR ADMIN STATS ====================

    /**
     * Retourne l'offre la plus chère
     */
    public Offre getOffreLaPlusChere() {
        return offreService.getAll().stream()
                .max(Comparator.comparing(Offre::getPrix))
                .orElse(null);
    }

    /**
     * Retourne l'offre la moins chère
     */
    public Offre getOffreLaMoinsChere() {
        return offreService.getAll().stream()
                .min(Comparator.comparing(Offre::getPrix))
                .orElse(null);
    }

    /**
     * Retourne le nombre d'offres pour un service spécifique
     */
    public int getNombreOffresParService(int serviceId) {
        return (int) offreService.getAll().stream()
                .filter(o -> o.getServiceId() == serviceId)
                .count();
    }

    /**
     * Retourne le prix total de toutes les offres
     */
    public double getPrixTotalOffres() {
        return offreService.getAll().stream()
                .mapToDouble(Offre::getPrix)
                .sum();
    }

    /**
     * Retourne la durée moyenne des offres
     */
    public double getDureeMoyenneOffres() {
        return offreService.getAll().stream()
                .mapToInt(Offre::getDuree)
                .average()
                .orElse(0.0);
    }

    /**
     * Retourne les statistiques sous forme de texte formaté
     */
    public String getResumeStatistiques() {
        StringBuilder sb = new StringBuilder();
        sb.append("📊 RÉSUMÉ STATISTIQUES\n");
        sb.append("══════════════════════\n");
        sb.append("🏨 Total services : ").append(getTotalServices()).append("\n");
        sb.append("🏷️ Total offres : ").append(getTotalOffres()).append("\n");
        sb.append("💰 Prix moyen : ").append(String.format("%.2f", getPrixMoyenOffres())).append(" DT\n");
        sb.append("⏱️ Durée moyenne : ").append(String.format("%.1f", getDureeMoyenneOffres())).append(" jours\n");
        sb.append("⭐ Service populaire : ").append(getServiceLePlusPopulaire()).append("\n");

        Offre plusChere = getOffreLaPlusChere();
        if (plusChere != null) {
            sb.append("💎 Offre + chère : ").append(plusChere.getTitre())
                    .append(" (").append(plusChere.getPrix()).append(" DT)\n");
        }

        Offre moinsChere = getOffreLaMoinsChere();
        if (moinsChere != null) {
            sb.append("🪙 Offre - chère : ").append(moinsChere.getTitre())
                    .append(" (").append(moinsChere.getPrix()).append(" DT)\n");
        }

        return sb.toString();
    }

    /**
     * Retourne une map avec la répartition des prix par tranche
     */
    public Map<String, Integer> getRepartitionPrix() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("0-200 DT", 0);
        stats.put("201-400 DT", 0);
        stats.put("401-600 DT", 0);
        stats.put("601-800 DT", 0);
        stats.put("801+ DT", 0);

        for (Offre o : offreService.getAll()) {
            double prix = o.getPrix();
            if (prix <= 200) {
                stats.put("0-200 DT", stats.get("0-200 DT") + 1);
            } else if (prix <= 400) {
                stats.put("201-400 DT", stats.get("201-400 DT") + 1);
            } else if (prix <= 600) {
                stats.put("401-600 DT", stats.get("401-600 DT") + 1);
            } else if (prix <= 800) {
                stats.put("601-800 DT", stats.get("601-800 DT") + 1);
            } else {
                stats.put("801+ DT", stats.get("801+ DT") + 1);
            }
        }

        return stats;
    }
}