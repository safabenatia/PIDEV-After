package services;

import models.voyage;
import models.destination;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsService {

    // ── helper: safely convert java.util.Date (which is actually java.sql.Date) to LocalDate ──
    // java.sql.Date.toInstant() throws UnsupportedOperationException — use toLocalDate() directly
    private LocalDate toLocal(java.util.Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
    }

    // ── Voyages per month (by dateDebut) ──────────────────────────────────
    public Map<String, Long> voyagesParMois(List<voyage> voyages) {
        String[] months = {"Jan","Fév","Mar","Avr","Mai","Jun",
                "Jul","Aoû","Sep","Oct","Nov","Déc"};
        Map<String, Long> result = new LinkedHashMap<>();
        for (String m : months) result.put(m, 0L);

        for (voyage v : voyages) {
            if (v.getDateDebut() == null) continue;
            LocalDate d = toLocal(v.getDateDebut());
            String key = months[d.getMonthValue() - 1];
            result.put(key, result.get(key) + 1);
        }
        return result;
    }

    // ── Voyages per price bracket ─────────────────────────────────────────
    public Map<String, Long> voyagesParTranchePrix(List<voyage> voyages) {
        Map<String, Long> result = new LinkedHashMap<>();
        result.put("< 500",     0L);
        result.put("500-1000",  0L);
        result.put("1000-2000", 0L);
        result.put("2000-3000", 0L);
        result.put("> 3000",    0L);

        for (voyage v : voyages) {
            double p = v.getPrix();
            if      (p < 500)  result.merge("< 500",     1L, Long::sum);
            else if (p < 1000) result.merge("500-1000",  1L, Long::sum);
            else if (p < 2000) result.merge("1000-2000", 1L, Long::sum);
            else if (p < 3000) result.merge("2000-3000", 1L, Long::sum);
            else               result.merge("> 3000",    1L, Long::sum);
        }
        return result;
    }

    // ── Voyages per destination (top N) ──────────────────────────────────
    public Map<String, Long> voyagesParDestination(List<voyage> voyages,
                                                   List<destination> destinations,
                                                   int topN) {
        Map<Integer, String> idToName = new HashMap<>();
        for (destination d : destinations)
            idToName.put(d.getId_destination(), d.getPays() + " / " + d.getVille());

        Map<String, Long> raw = new LinkedHashMap<>();
        for (voyage v : voyages) {
            String name = idToName.getOrDefault(v.getIdDestination(),
                    "Destination #" + v.getIdDestination());
            raw.merge(name, 1L, Long::sum);
        }

        return raw.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new));
    }

    // ── Destinations per continent ────────────────────────────────────────
    public Map<String, Long> destinationsParContinent(List<destination> destinations) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (destination d : destinations) {
            String c = (d.getContinent() == null || d.getContinent().isBlank())
                    ? "Inconnu" : d.getContinent();
            result.merge(c, 1L, Long::sum);
        }
        return result.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> a, LinkedHashMap::new));
    }

    // ── KPI helpers ───────────────────────────────────────────────────────
    public double prixMoyen(List<voyage> voyages) {
        return voyages.stream().mapToDouble(voyage::getPrix).average().orElse(0);
    }

    public double prixMin(List<voyage> voyages) {
        return voyages.stream().mapToDouble(voyage::getPrix).min().orElse(0);
    }

    public double prixMax(List<voyage> voyages) {
        return voyages.stream().mapToDouble(voyage::getPrix).max().orElse(0);
    }

    public int totalPlaces(List<voyage> voyages) {
        return voyages.stream().mapToInt(voyage::getNbPlaces).sum();
    }

    public long voyagesAVenir(List<voyage> voyages) {
        LocalDate today = LocalDate.now();
        return voyages.stream()
                .filter(v -> v.getDateDebut() != null)
                .filter(v -> toLocal(v.getDateDebut()).isAfter(today))
                .count();
    }

    public long voyagesUrgents(List<voyage> voyages) {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(7);
        return voyages.stream()
                .filter(v -> v.getDateDebut() != null)
                .filter(v -> {
                    LocalDate d = toLocal(v.getDateDebut());
                    return !d.isBefore(today) && !d.isAfter(limit);
                })
                .count();
    }
}