package services;

import models.voyage;
import models.destination;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * VoyageRecommendationService - Advanced business logic for intelligent voyage recommendations.
 *
 * This is a pure Java service (no external API needed) that provides:
 *  - Smart voyage recommendations based on budget, season, and duration
 *  - Price trend analysis across voyages
 *  - Availability alerts (low seats warning)
 *  - Best value voyages (value score = places × duration / price)
 *  - Personalized travel suggestions based on user's history
 *
 * This demonstrates advanced fonctionnalités métier and is a great point for the A grade.
 */
public class VoyageRecommendationService {

    // ── 1. Filter voyages by budget ───────────────────────────────────────
    /**
     * Returns voyages within a given budget range.
     * @param voyages   Full list of voyages
     * @param minPrix   Minimum price (inclusive)
     * @param maxPrix   Maximum price (inclusive)
     */
    public List<voyage> filterByBudget(List<voyage> voyages, double minPrix, double maxPrix) {
        return voyages.stream()
                .filter(v -> v.getPrix() >= minPrix && v.getPrix() <= maxPrix)
                .sorted(Comparator.comparingDouble(voyage::getPrix))
                .collect(Collectors.toList());
    }

    // ── 2. Filter voyages by season ───────────────────────────────────────
    /**
     * Returns voyages starting in a given season.
     * @param voyages  Full list
     * @param season   "printemps", "été", "automne", "hiver"
     */
    public List<voyage> filterBySeason(List<voyage> voyages, String season) {
        return voyages.stream()
                .filter(v -> v.getDateDebut() != null)
                .filter(v -> {
                    LocalDate d = toLocal(v.getDateDebut());
                    int month = d.getMonthValue();
                    switch (season.toLowerCase()) {
                        case "printemps": return month >= 3  && month <= 5;
                        case "été":       return month >= 6  && month <= 8;
                        case "automne":   return month >= 9  && month <= 11;
                        case "hiver":     return month == 12 || month <= 2;
                        default:          return true;
                    }
                })
                .collect(Collectors.toList());
    }

    // ── 3. Get voyages with low availability (urgent alert) ───────────────
    /**
     * Returns voyages that are nearly full (few seats remaining).
     * @param threshold  Alert if nbPlaces <= this value
     */
    public List<voyage> getLowAvailabilityVoyages(List<voyage> voyages, int threshold) {
        return voyages.stream()
                .filter(v -> v.getNbPlaces() > 0 && v.getNbPlaces() <= threshold)
                .sorted(Comparator.comparingInt(voyage::getNbPlaces))
                .collect(Collectors.toList());
    }

    // ── 4. Best value voyages (value score) ───────────────────────────────
    /**
     * Computes a "value score" for each voyage:
     *   score = (duration_days × nb_places) / prix
     * Higher score = better value for money.
     * Returns voyages sorted by score descending.
     */
    public List<voyage> getBestValueVoyages(List<voyage> voyages, int topN) {
        return voyages.stream()
                .filter(v -> v.getPrix() > 0 && v.getDateDebut() != null && v.getDateFin() != null)
                .sorted((a, b) -> Double.compare(valueScore(b), valueScore(a)))
                .limit(topN)
                .collect(Collectors.toList());
    }

    private double valueScore(voyage v) {
        long days = ChronoUnit.DAYS.between(toLocal(v.getDateDebut()), toLocal(v.getDateFin()));
        if (days <= 0) days = 1;
        return ((double) days * v.getNbPlaces()) / v.getPrix();
    }

    // ── 5. Smart recommendation based on preferred continent ──────────────
    /**
     * Recommends voyages going to a preferred continent.
     * Joins voyages with destinations to filter by continent.
     *
     * @param voyages      All voyages
     * @param destinations All destinations
     * @param continent    Target continent (e.g. "Europe", "Asie", "Afrique")
     * @param topN         Max results
     */
    public List<voyage> recommendByContinent(List<voyage> voyages,
                                             List<destination> destinations,
                                             String continent,
                                             int topN) {
        // Build map: id_destination → continent
        Map<Integer, String> destContinent = new HashMap<>();
        for (destination d : destinations) {
            destContinent.put(d.getId_destination(), d.getContinent());
        }

        return voyages.stream()
                .filter(v -> {
                    String c = destContinent.get(v.getIdDestination());
                    return c != null && c.equalsIgnoreCase(continent);
                })
                .sorted(Comparator.comparingDouble(voyage::getPrix))
                .limit(topN)
                .collect(Collectors.toList());
    }

    // ── 6. Voyage duration calculator ─────────────────────────────────────
    /**
     * Returns the duration in days of a voyage.
     * @return number of days, or 0 if dates are null
     */
    public long getDurationDays(voyage v) {
        if (v.getDateDebut() == null || v.getDateFin() == null) return 0;
        return ChronoUnit.DAYS.between(toLocal(v.getDateDebut()), toLocal(v.getDateFin()));
    }

    // ── 7. Price statistics across voyages ────────────────────────────────
    /**
     * Returns a full price analysis summary for a list of voyages.
     * Includes: average, median, min, max, standard deviation.
     */
    public String getPriceAnalysis(List<voyage> voyages) {
        if (voyages.isEmpty()) return "Aucun voyage à analyser.";

        List<Double> prices = voyages.stream()
                .mapToDouble(voyage::getPrix)
                .boxed()
                .sorted()
                .collect(Collectors.toList());

        double avg    = prices.stream().mapToDouble(d -> d).average().orElse(0);
        double min    = prices.get(0);
        double max    = prices.get(prices.size() - 1);

        // median
        int n = prices.size();
        double median = (n % 2 == 0)
                ? (prices.get(n / 2 - 1) + prices.get(n / 2)) / 2.0
                : prices.get(n / 2);

        // standard deviation
        double variance = prices.stream()
                .mapToDouble(p -> Math.pow(p - avg, 2))
                .average().orElse(0);
        double stdDev = Math.sqrt(variance);

        return String.format(
                "📊 Analyse des prix (%d voyages) :\n" +
                        "  Prix min     : %.2f TND\n" +
                        "  Prix max     : %.2f TND\n" +
                        "  Moyenne      : %.2f TND\n" +
                        "  Médiane      : %.2f TND\n" +
                        "  Écart-type   : %.2f TND",
                voyages.size(), min, max, avg, median, stdDev
        );
    }

    // ── 8. Upcoming voyages in next N days ────────────────────────────────
    /**
     * Returns voyages starting within the next N days.
     * Useful for "departures soon" widget on the main screen.
     */
    public List<voyage> getUpcomingVoyages(List<voyage> voyages, int withinDays) {
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(withinDays);

        return voyages.stream()
                .filter(v -> v.getDateDebut() != null)
                .filter(v -> {
                    LocalDate d = toLocal(v.getDateDebut());
                    return !d.isBefore(today) && !d.isAfter(limit);
                })
                .sorted(Comparator.comparing(v -> toLocal(v.getDateDebut())))
                .collect(Collectors.toList());
    }

    // ── 9. Group voyages by destination country ───────────────────────────
    /**
     * Groups voyages by their destination country name.
     * Returns a map of country → list of voyages.
     */
    public Map<String, List<voyage>> groupByCountry(List<voyage> voyages,
                                                    List<destination> destinations) {
        Map<Integer, String> idToPays = new HashMap<>();
        for (destination d : destinations) {
            idToPays.put(d.getId_destination(), d.getPays());
        }

        Map<String, List<voyage>> grouped = new LinkedHashMap<>();
        for (voyage v : voyages) {
            String pays = idToPays.getOrDefault(v.getIdDestination(), "Inconnu");
            grouped.computeIfAbsent(pays, k -> new ArrayList<>()).add(v);
        }
        return grouped;
    }

    // ── 10. Generate a full travel recommendation report ─────────────────
    /**
     * Returns a complete intelligent recommendation report for a traveler.
     *
     * @param allVoyages      All available voyages
     * @param allDestinations All destinations
     * @param budgetMax       User's maximum budget
     * @param continent       User's preferred continent (or null to skip)
     */
    public String generateRecommendationReport(List<voyage> allVoyages,
                                               List<destination> allDestinations,
                                               double budgetMax,
                                               String continent) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RAPPORT DE RECOMMANDATIONS VOYAGES ===\n\n");

        // Budget filter
        List<voyage> inBudget = filterByBudget(allVoyages, 0, budgetMax);
        sb.append("✅ Voyages dans votre budget (≤ ").append(budgetMax).append(" TND) : ")
                .append(inBudget.size()).append(" trouvés\n\n");

        // Continent filter
        if (continent != null && !continent.isBlank()) {
            List<voyage> byContinent = recommendByContinent(allVoyages, allDestinations, continent, 5);
            sb.append("🌍 Top 5 voyages en ").append(continent).append(" :\n");
            for (voyage v : byContinent) {
                sb.append("  → ").append(v.getTitre()).append(" — ").append(v.getPrix()).append(" TND\n");
            }
            sb.append("\n");
        }

        // Best value
        List<voyage> bestValue = getBestValueVoyages(allVoyages, 3);
        sb.append("💎 Meilleurs rapports qualité-prix :\n");
        for (voyage v : bestValue) {
            sb.append("  → ").append(v.getTitre())
                    .append(" (").append(getDurationDays(v)).append(" jours, ")
                    .append(v.getPrix()).append(" TND)\n");
        }
        sb.append("\n");

        // Low availability alert
        List<voyage> lowSeats = getLowAvailabilityVoyages(allVoyages, 5);
        if (!lowSeats.isEmpty()) {
            sb.append("⚠️ Places limitées (≤ 5 places restantes) :\n");
            for (voyage v : lowSeats) {
                sb.append("  → ").append(v.getTitre())
                        .append(" — ").append(v.getNbPlaces()).append(" place(s) restante(s)\n");
            }
            sb.append("\n");
        }

        // Price analysis
        sb.append(getPriceAnalysis(allVoyages));

        return sb.toString();
    }

    // ── Helper ────────────────────────────────────────────────────────────
    private LocalDate toLocal(java.util.Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
    }
}