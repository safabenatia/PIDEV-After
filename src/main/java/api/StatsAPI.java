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
}