package gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class NotificationUtil {

    public enum Type {
        SUCCESS, ERROR, INFO, WARNING
    }

    public static void show(StackPane overlay, String message, Type type) {
        String bg, border, icon;
        switch (type) {
            case SUCCESS -> {
                bg = "rgba(39,174,96,0.95)";
                border = "#27ae60";
                icon = "✅  ";
            }
            case ERROR -> {
                bg = "rgba(192,57,43,0.95)";
                border = "#e74c3c";
                icon = "❌  ";
            }
            case WARNING -> {
                bg = "rgba(211,84,0,0.95)";
                border = "#e67e22";
                icon = "⚠️  ";
            }
            default -> {
                bg = "rgba(41,128,185,0.95)";
                border = "#2980b9";
                icon = "ℹ️  ";
            }
        }

        Label toast = new Label(icon + message);
        toast.setStyle(
                "-fx-background-color: " + bg + ";" +
                        "-fx-border-color: " + border + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 14;" +
                        "-fx-background-radius: 14;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 13.5px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 13 26 13 26;" +
                        "-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.45),18,0,0,6);");
        toast.setMaxWidth(480);
        toast.setWrapText(true);
        toast.setOpacity(0);
        toast.setTranslateY(20);

        StackPane.setAlignment(toast, Pos.BOTTOM_CENTER);
        overlay.getChildren().add(toast);

        // slide up + fade in
        FadeTransition fi = new FadeTransition(Duration.millis(280), toast);
        fi.setFromValue(0);
        fi.setToValue(1);
        TranslateTransition ti = new TranslateTransition(Duration.millis(280), toast);
        ti.setFromY(20);
        ti.setToY(0);
        ParallelTransition show = new ParallelTransition(fi, ti);

        PauseTransition pause = new PauseTransition(Duration.millis(2700));

        FadeTransition fo = new FadeTransition(Duration.millis(350), toast);
        fo.setFromValue(1);
        fo.setToValue(0);
        fo.setOnFinished(e -> overlay.getChildren().remove(toast));

        new SequentialTransition(show, pause, fo).play();
    }
}
