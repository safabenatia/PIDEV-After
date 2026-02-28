package services;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * PublicHolidayService - Gets official public holidays for any country.
 * Uses Nager.Date API (https://date.nager.at/) — free, no API key required.
 * Supports 100+ countries.
 *
 * Very useful for voyage planning: warn users if their trip dates overlap
 * with public holidays at the destination (banks closed, crowded, etc.)
 */
public class PublicHolidayService {

    private final OkHttpClient client = new OkHttpClient();
    private static final String BASE_URL = "https://date.nager.at/api/v3";

    /**
     * Returns all public holidays for a given country and year.
     * @param countryCode ISO 3166-1 alpha-2 code (e.g. "FR", "DE", "TN", "JP", "IT")
     * @param year        4-digit year (e.g. 2025)
     * @return List of JSONObjects each containing: date, name, localName, types
     */
    public List<JSONObject> getHolidays(String countryCode, int year) {
        List<JSONObject> holidays = new ArrayList<>();
        try {
            String url = BASE_URL + "/PublicHolidays/" + year + "/" + countryCode.toUpperCase();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JSONArray array = new JSONArray(body);
            for (int i = 0; i < array.length(); i++) {
                holidays.add(array.getJSONObject(i));
            }
        } catch (Exception e) {
            System.out.println("PublicHolidayService error: " + e.getMessage());
        }
        return holidays;
    }

    /**
     * Returns a formatted list of holidays for a country/year.
     * Example output:
     *   "Jours fériés en FR (2025):\n  • 2025-01-01 - New Year's Day\n  • ..."
     */
    public String getHolidaysSummary(String countryCode, int year) {
        List<JSONObject> holidays = getHolidays(countryCode, year);
        if (holidays.isEmpty()) return "Aucun jour férié trouvé pour " + countryCode + " en " + year;

        StringBuilder sb = new StringBuilder("Jours fériés en " + countryCode.toUpperCase() + " (" + year + "):\n");
        for (JSONObject h : holidays) {
            String date = h.getString("date");
            String name = h.getString("name");
            String localName = h.optString("localName", "");
            sb.append("  • ").append(date).append(" - ").append(name);
            if (!localName.equals(name) && !localName.isEmpty()) {
                sb.append(" (").append(localName).append(")");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Checks if a specific date is a public holiday in the given country.
     * @param countryCode ISO country code (e.g. "FR")
     * @param date        Date string in format "YYYY-MM-DD"
     * @return Holiday name if it is a holiday, null otherwise
     */
    public String isHoliday(String countryCode, String date) {
        try {
            int year = Integer.parseInt(date.substring(0, 4));
            List<JSONObject> holidays = getHolidays(countryCode, year);
            for (JSONObject h : holidays) {
                if (h.getString("date").equals(date)) {
                    return h.getString("name");
                }
            }
        } catch (Exception e) {
            System.out.println("PublicHolidayService isHoliday error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Checks if any date in a date range overlaps with a public holiday.
     * Perfect for alerting users when their voyage dates fall on holidays.
     *
     * @param countryCode ISO country code
     * @param startDate   "YYYY-MM-DD"
     * @param endDate     "YYYY-MM-DD"
     * @return List of holiday names that overlap with the range
     */
    public List<String> checkVoyageDatesForHolidays(String countryCode, String startDate, String endDate) {
        List<String> conflicts = new ArrayList<>();
        try {
            int startYear = Integer.parseInt(startDate.substring(0, 4));
            int endYear = Integer.parseInt(endDate.substring(0, 4));

            for (int year = startYear; year <= endYear; year++) {
                List<JSONObject> holidays = getHolidays(countryCode, year);
                for (JSONObject h : holidays) {
                    String holidayDate = h.getString("date");
                    if (holidayDate.compareTo(startDate) >= 0 && holidayDate.compareTo(endDate) <= 0) {
                        conflicts.add(holidayDate + " - " + h.getString("name"));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("PublicHolidayService checkDates error: " + e.getMessage());
        }
        return conflicts;
    }

    /**
     * Returns the next upcoming holiday for a country from today.
     * @param countryCode ISO country code
     * @return JSONObject of the next holiday or null
     */
    public JSONObject getNextHoliday(String countryCode) {
        try {
            String url = BASE_URL + "/NextPublicHolidays/" + countryCode.toUpperCase();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            JSONArray array = new JSONArray(body);
            if (array.length() > 0) return array.getJSONObject(0);
        } catch (Exception e) {
            System.out.println("PublicHolidayService nextHoliday error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Returns all supported country codes from Nager.Date.
     * Useful for validation or building a country picker.
     */
    public List<String> getSupportedCountries() {
        List<String> codes = new ArrayList<>();
        try {
            String url = BASE_URL + "/AvailableCountries";
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            JSONArray array = new JSONArray(body);
            for (int i = 0; i < array.length(); i++) {
                codes.add(array.getJSONObject(i).getString("countryCode"));
            }
        } catch (Exception e) {
            System.out.println("PublicHolidayService getSupportedCountries error: " + e.getMessage());
        }
        return codes;
    }
}
