package services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

public class GoogleCalendarService {

    private static final String APPLICATION_NAME = "AFTER Travel";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        System.out.println(
                GoogleCalendarService.class.getResource("/credentials.json")
        );
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                new InputStreamReader(
                        GoogleCalendarService.class.getResourceAsStream("/credentials.json")
                )
        );

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                clientSecrets,
                Collections.singletonList("https://www.googleapis.com/auth/calendar")
        ).setAccessType("offline").build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(0) // port automatique libre
                .build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public static void ajouterRappelExpiration(String nomDocument, LocalDate dateExpiration) {

        try {
            NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

            Calendar service = new Calendar.Builder(
                    HTTP_TRANSPORT,
                    JSON_FACTORY,
                    getCredentials(HTTP_TRANSPORT)
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            Date startDate = Date.from(dateExpiration
                    .atStartOfDay(ZoneId.systemDefault()).toInstant());

            Event event = new Event()
                    .setSummary("Expiration document : " + nomDocument)
                    .setDescription("Document AFTER Travel arrive à expiration");

            EventDateTime start = new EventDateTime()
                    .setDateTime(new com.google.api.client.util.DateTime(startDate));
            event.setStart(start);
            event.setEnd(start);

            EventReminder[] reminders = new EventReminder[] {
                    new EventReminder().setMethod("popup").setMinutes(1440) // 1 jour avant
            };

            Event.Reminders eventReminders = new Event.Reminders()
                    .setUseDefault(false)
                    .setOverrides(java.util.Arrays.asList(reminders));

            event.setReminders(eventReminders);

            service.events().insert("primary", event).execute();

            System.out.println("Rappel Google Calendar créé ✔");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}