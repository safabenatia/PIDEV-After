package services;

import models.voyage;
import java.util.ArrayList;
import java.util.List;

public class FavoritesService {

    private static List<voyage> favorites = new ArrayList<>();

    public void addFavorite(voyage v) {
        if (!isFavorite(v)) {
            favorites.add(v);
        }
    }

    public void removeFavorite(voyage v) {
        favorites.removeIf(f -> f.getIdVoyage() == v.getIdVoyage());
    }

    public boolean isFavorite(voyage v) {
        return favorites.stream().anyMatch(f -> f.getIdVoyage() == v.getIdVoyage());
    }

    public List<voyage> getFavorites() {
        return favorites;
    }
}