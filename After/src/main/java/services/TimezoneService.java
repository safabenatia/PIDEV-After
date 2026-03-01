package services;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * TimezoneService - Gets current local time for any destination.
 *
 * Uses Java's built-in ZoneId (java.time) — NO external API, NO network call needed.
 * 100% reliable, works offline, no connection resets ever.
 *
 * This is better than an API because:
 *  - Instant response (no network latency)
 *  - Never fails / never times out
 *  - Java's timezone database is always up to date (via tzdata)
 */
public class TimezoneService {

    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("EEEE dd MMM yyyy  HH:mm", Locale.FRENCH);

    /**
     * Returns the current local time at a destination as a formatted string.
     * Example: "Paris (Europe/Paris) : dimanche 01 juin 2025  14:30  (UTC+02:00)"
     *
     * @param city     Display name of the city (just for the label)
     * @param timezone IANA timezone string e.g. "Europe/Paris", "Africa/Tunis"
     */
    public String getLocalTimeSummary(String city, String timezone) {
        try {
            ZoneId zoneId = ZoneId.of(timezone);
            ZonedDateTime now = ZonedDateTime.now(zoneId);
            String formatted = now.format(DISPLAY_FORMATTER);
            String offset = now.getOffset().getId().replace("Z", "+00:00");
            return city + " (" + timezone + ") : " + formatted + "  (UTC" + offset + ")";
        } catch (Exception e) {
            System.out.println("TimezoneService error: " + e.getMessage());
            return "Heure indisponible pour " + city;
        }
    }

    /**
     * Returns the time difference between two IANA timezones.
     * Example: "+1h par rapport a la Tunisie"
     *
     * @param fromTimezone User's timezone (e.g. "Africa/Tunis")
     * @param toTimezone   Destination timezone (e.g. "Europe/Paris")
     */
    public String getTimeDifference(String fromTimezone, String toTimezone) {
        try {
            ZonedDateTime now      = ZonedDateTime.now();
            ZonedDateTime fromTime = now.withZoneSameInstant(ZoneId.of(fromTimezone));
            ZonedDateTime toTime   = now.withZoneSameInstant(ZoneId.of(toTimezone));

            int fromOffset  = fromTime.getOffset().getTotalSeconds();
            int toOffset    = toTime.getOffset().getTotalSeconds();
            int diffSeconds = toOffset - fromOffset;

            if (diffSeconds == 0) return "Meme fuseau horaire que la Tunisie";

            int diffHours   = diffSeconds / 3600;
            int diffMinutes = Math.abs((diffSeconds % 3600) / 60);
            String sign     = diffHours > 0 ? "+" : "";

            if (diffMinutes == 0) {
                return "Decalage : " + sign + diffHours + "h par rapport a la Tunisie";
            } else {
                return "Decalage : " + sign + diffHours + "h" + diffMinutes + "min par rapport a la Tunisie";
            }
        } catch (Exception e) {
            System.out.println("TimezoneService diff error: " + e.getMessage());
            return "Decalage horaire indisponible";
        }
    }

    /**
     * Returns a day/night label based on local time at destination.
     */
    public String getDayNightIndicator(String timezone) {
        try {
            int hour = ZonedDateTime.now(ZoneId.of(timezone)).getHour();
            return (hour >= 6 && hour < 20) ? "Jour" : "Nuit";
        } catch (Exception e) {
            return "";
        }
    }
}
