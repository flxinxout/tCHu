package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 *  Partie publique de l'état d'un joueur, à savoir le nombre de billets, de cartes et de wagons qu'il possède,
 *  les routes dont il s'est emparé, et le nombre de points de construction qu'il a ainsi obtenu.
 *
 *  @author Dylan Vairoli (326603)
 *  @author Giovanni Ranieri (326870)
 */
public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final List<Route> routes;
    private final int carCount;
    private final int claimPoints;

    /**
     * Construit l'état public d'un joueur possédant le nombre de billets et de cartes donnés,
     * et s'étant emparé des routes données
     * @param ticketCount
     *          le nombre de tickets du joueur
     * @param cardCount
     *          le nombre de cartes du joueur
     * @param routes
     *          les routes dont le joueur s'est emparées
     * @throws IllegalArgumentException
     *          si le nombre de billets ou le nombre de cartes < 0
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);

        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = List.copyOf(routes);

        int tempCars = 0;
        int tempPoints = 0;
        for (Route route: routes) {
            tempPoints += route.claimPoints();
            tempCars += route.length();
        }

        this.carCount = Constants.INITIAL_CAR_COUNT - tempCars;
        this.claimPoints = tempPoints;
    }

    /**
     * Retourne le nombre de tickets que possède le joueur.
     * @return le nombre de tickets que possède le joueur
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * Retourne le nombre de cartes possède le joueur.
     * @return le nombre de cartes possède le joueur
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * Retourne les routes dont le joueur s'est emparé.
     * @return les routes dont le joueur s'est emparé
     */
    public List<Route> routes() {
        return routes;
    }

    /**
     * Retourne le nombre de wagon que possède le joueur.
     * @return le nombre de wagon que possède le joueur
     */
    public int carCount() {
        return carCount;
    }

    /**
     * Retourne le nombre de points de construction qu'à obtenu le joueur.
     * @return le nombre de points de construction qu'à obtenu le joueur
     */
    public int claimPoints() {
        return claimPoints;
    }
}
