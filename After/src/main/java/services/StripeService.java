package services;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.model.checkout.SessionCollection;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListParams;
import model.Paiement;
import model.Reservation;

public class StripeService {

    private static final String SECRET_KEY = "api_key";

    public StripeService() {
        Stripe.apiKey = SECRET_KEY;
    }

    public Session createCheckoutSession(Reservation res, Paiement p) throws Exception {
        String currency = "eur";
        long unitAmount = (long) (p.getMontant() * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("https://example.com/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("https://example.com/cancel")
                .putMetadata("paiement_ref", p.getReference())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData
                                                .builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(unitAmount)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData
                                                                .builder()
                                                                .setName("Réservation "
                                                                        + res.getType()
                                                                        + " #"
                                                                        + res.getId())
                                                                .setDescription("Lieu: "
                                                                        + res.getLieu()
                                                                        + " | Nb: "
                                                                        + res.getNbPersonnes())
                                                                .build())
                                                .build())
                                .build())
                .build();

        return Session.create(params);
    }

    public Session findLatestSessionByPaiementRef(String ref) throws Exception {
        // Fallback to listing sessions and filtering manually to avoid package
        // resolution issues with Search API
        SessionListParams params = SessionListParams.builder()
                .setLimit(100L)
                .build();

        SessionCollection sessions = Session.list(params);
        for (Session s : sessions.getData()) {
            if (s.getMetadata() != null && ref.equals(s.getMetadata().get("paiement_ref"))) {
                return s;
            }
        }
        return null;
    }

    public Session retrieveSession(String sessionId) throws Exception {
        return Session.retrieve(sessionId);
    }
}