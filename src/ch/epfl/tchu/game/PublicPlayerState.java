package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

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

        this.carCount = Constants.INITIAL_CAR_COUNT - routes.stream()
                .mapToInt(r -> r.length())
                .sum();

        this.claimPoints = routes.stream()
                .mapToInt(r -> r.claimPoints())
                .sum();
    }

    /**
     * @return le nombre de tickets que possède le joueur
     */
    public int ticketCount() {
        return this.ticketCount;
    }

    /**
     * @return le nombre de cartes possède le joueur
     */
    public int cardCount() {
        return this.cardCount;
    }

    /**
     * @return les routes dont le joueur s'est emparé
     */
    public List<Route> routes() {
        return this.routes;
    }

    /**
     * @return le nombre de wagons que possède le joueur
     */
    public int carCount() {
        return this.carCount;
    }

    /**
     * @return le nombre de points de construction qu'à obtenu le joueur
     */
    public int claimPoints() {
        return this.claimPoints;
    }
}
