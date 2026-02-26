package services;

public class ExchangeRateService {

    // approximate fixed rates - TND to EUR and USD
    private static final double TND_TO_EUR = 0.30;
    private static final double TND_TO_USD = 0.32;

    public double[] convertToEURandUSD(double amountTND) {
        try {
            double eur = Math.round(amountTND * TND_TO_EUR * 100.0) / 100.0;
            double usd = Math.round(amountTND * TND_TO_USD * 100.0) / 100.0;
            return new double[]{eur, usd};
        } catch (Exception e) {
            System.out.println("ExchangeRate error: " + e.getMessage());
            return new double[]{0, 0};
        }
    }
}