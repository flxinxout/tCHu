package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

import static ch.epfl.tchu.game.Constants.*;

/**
 * Partie publique (visible par tous les joueurs) de l'état d'un joueur, à savoir le nombre de billets, de cartes et de
 * wagons qu'il possède, les routes dont il s'est emparé, et le nombre de points de construction qu'il a ainsi obtenu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    /**
     * Construit l'état public d'un joueur possédant {@code ticketCount} billets et {@code cardCount} cartes,
     * et s'étant emparé des routes {@code routes}.
     *
     * @param ticketCount le nombre de tickets du joueur
     * @param cardCount   le nombre de cartes du joueur
     * @param routes      les routes dont le joueur s'est emparées
     * @throws IllegalArgumentException si {@code ticketCount} ou {@code cardCount} < 0
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);

        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);

        this.carCount = INITIAL_CAR_COUNT - routes.stream()
                .mapToInt(Route::length)
                .sum();

        this.claimPoints = routes.stream()
                .mapToInt(Route::claimPoints)
                .sum();
    }

    /**
     * Retourne le nombre de tickets que possède ce joueur.
     *
     * @return le nombre de tickets que possède ce joueur
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * Retourne le nombre de cartes possède ce joueur.
     *
     * @return le nombre de cartes possède ce joueur
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * Retourne les routes dont ce joueur s'est emparé.
     *
     * @return les routes dont ce joueur s'est emparé
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * Retourne le nombre de wagons que possède ce joueur
     *
     * @return le nombre de wagons que possède ce joueur
     */
    public int carCount() {
        return carCount;
    }

    /**
     * Retourne le nombre de points de construction qu'à obtenu ce joueur.
     *
     * @return le nombre de points de construction qu'à obtenu ce joueur
     */
    public int claimPoints() {
        return claimPoints;
    }
}
