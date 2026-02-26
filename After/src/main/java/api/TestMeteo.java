package api;

public class TestMeteo {
    public static void main(String[] args) {
        System.out.println("🌤️ TEST DE L'API MÉTÉO 🌤️");
        System.out.println("===========================\n");

        MeteoAPI meteo = new MeteoAPI();

        String ville = "Tunis";
        double temp = meteo.getTemperature(ville);
        String desc = meteo.getDescription(ville);
        String icone = meteo.getIcone(ville);
        String offre = meteo.suggererOffre(ville, "");

        System.out.println("📍 " + ville);
        System.out.println(icone + " " + desc);
        System.out.println("🌡️ Température: " + temp + "°C");
        System.out.println("🏷️ Offre suggérée: " + offre);
    }
}