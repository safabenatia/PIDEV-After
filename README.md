# ✈️ AfterTravel — Desktop Application (JavaFX)

> The desktop counterpart of the AfterTravel platform, built with **JavaFX** and **Maven**. A full-featured travel management application covering trips, reservations, payments, documents, activities, and more — all from a native Java desktop interface.

---

## 📌 Project Overview

AfterTravel Desktop is a JavaFX application developed as part of the PIDEV project at Esprit School of Engineering. It shares the same MySQL database and core modules as the web version, but runs as a standalone desktop app. It supports two roles — **voyageurs** (travellers) and **admins** — each with their own dedicated dashboard.

---

## 🗂️ Modules

| Module | Description |
| 🧳 **Voyages** | Browse, add, edit, delete trips. Smart recommendations by budget, season, and duration. PDF export |
| 🗺️ **Destinations** | Country/city management with REST Countries API integration and country info lookup |
| 🏃 **Activités** | Activity CRUD, list view, editing, planning links |
| 📅 **Plannings** | Planning creation and management linked to activities |
| 🚗 **Réservations** | Full reservation flow for users and admin view with status management. Email confirmation via JavaMail |
| 💳 **Paiements** | Stripe payment integration, manual payment form, PDF receipt generation, admin payment view |
| 📄 **Documents** | Document upload (Cloudinary), OCR-based auto category detection, per-category browsing, add/edit/delete |
| 💸 **Dépenses** | Expense tracker with category management and budget verification dialogs |
| 🎁 **Offres & Services** | Offer and service CRUD with QR code generation per offer |
| 👤 **Utilisateurs** | Login, sign-up, JWT session management, bcrypt password hashing, user dialog |



## 🖥️ Dashboards

### Admin Dashboard (`DashboardAdminController`)
- Full CRUD for all modules in one place
- Statistics with pie charts and bar charts (JFreeChart)
- User management dialog
- Document management with OCR auto-categorisation
- Reservation and payment oversight

### Voyageur Dashboard (`DashboardVoyageurController`)
- Personal reservation and payment history
- Trip browsing and booking
- Weather widget for destinations
- Favourites management

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Java 17+ |
| **UI Framework** | JavaFX (FXML, Controls, Web, Media) |
| **Build Tool** | Maven |
| **Database** | MySQL (JDBC via `mysql-connector-java`) |
| **PDF Generation** | iText 7 (itext7-core, layout, forms, kernel) |
| **Charts** | JFreeChart |
| **QR Codes** | Google ZXing |
| **Payments** | Stripe Java SDK |
| **File Upload** | Cloudinary SDK |
| **OCR** | OCR.space API |
| **Weather** | Open-Meteo API + Nominatim geocoding |
| **Translation** | MyMemory API |
| **Holidays** | PublicHolidays API |
| **Google Calendar** | Google Calendar API (OAuth2) |
| **Email** | JavaMail (`javax.mail`) |
| **HTTP Client** | OkHttp3, Apache HttpClient |
| **JSON** | org.json, Gson |
| **Auth** | jBCrypt (password hashing), JJWT (JWT tokens) |
| **Icons** | Ikonli + FontAwesome 5 |
| **Excel Export** | Apache POI |
| **CSV** | OpenCSV |
| **Logging** | Logback |
| **Testing** | JUnit Jupiter |

---

## ⚙️ Installation & Setup

### Prerequisites

- Java 17 or higher (JDK)
- Maven 3.8+
- MySQL 8+
- IntelliJ IDEA (recommended) or any Java IDE

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/your-org/Esprit-PIDEV-3A24-2526-desktop.git
cd After

# 2. Configure the database connection
# Edit src/main/java/utils/MyDataBase.java
# Set your MySQL host, port, database name, username, and password

# 3. Configure API keys
# Edit the relevant service files:
#   - src/main/java/utils/CloudinaryConfig.java       → Cloudinary credentials
#   - src/main/java/services/StripeService.java        → Stripe secret key
#   - src/main/java/services/OcrService.java           → OCR.space API key
#   - src/main/java/services/EmailService.java         → SMTP credentials
#   - src/main/java/services/GoogleCalendarService.java → Google OAuth credentials

# 4. Build the project
mvn clean install

# 5. Run the application
mvn javafx:run
```

---

## 📁 Project Structure

```
src/
├── main/java/
│   ├── api/              # External API wrappers (Email, Meteo, PDF, QRCode, Stats, Paiement)
│   ├── controllers/      # Main FXML controllers (Dashboard, Voyage, Document, Depense, Stats…)
│   ├── gui/              # Feature-specific GUI controllers (Activite, Planning, Reservation, Paiement…)
│   ├── models/           # Entity/model classes (Users, Voyage, Activite, Document, Paiement…)
│   ├── services/         # Business logic & external integrations
│   ├── utils/            # Utilities (DB connection, JWT, session, password hashing)
│   └── tn/esprit/        # Main entry point
│
├── main/resources/
│   ├── views/            # login.fxml, signup.fxml, DashboardVoyageur.fxml
│   └── *.fxml            # All other FXML layouts (MainView, DashboardAdmin, Offre, Service…)
│
└── test/java/services/   # JUnit tests for services
```

---

## 🔑 Key Configuration Files

| File | Purpose |
|---|---|
| `utils/MyDataBase.java` | MySQL connection settings |
| `utils/CloudinaryConfig.java` | Cloudinary upload credentials |
| `services/StripeService.java` | Stripe API key |
| `services/OcrService.java` | OCR.space API key |
| `services/EmailService.java` | SMTP / JavaMail config |
| `services/GoogleCalendarService.java` | Google Calendar OAuth2 |

---

## 👥 Roles

| Role | Access |
|---|---|
| **Admin** | Full dashboard, all CRUD, user management, statistics, reports |
| **Voyageur** | Personal dashboard, trip browsing, booking, payments, document management |

---

## 🧪 Tests

Unit tests are located in `src/test/java/services/` and cover:

- User service
- Voyage & destination service
- Activité & planning service
- Document & category service
- Depense service

Run tests with:

```bash
mvn test
```

---

## 👨‍💻 Team

Developed by the NovaJourney team   — Esprit School of Engineering, 3rd year, 2025–2026.

---

## 📄 License

This project was built for academic purposes. All rights reserved © Esprit PIDEV 2025–2026.
