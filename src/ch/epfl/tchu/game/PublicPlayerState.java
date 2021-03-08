package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;

/**
 *  partie publique de l'état d'un joueur, à savoir le nombre de billets, de cartes et de wagons qu'il possède,
 *  les routes dont il s'est emparé, et le nombre de points de construction qu'il a ainsi obtenu
 *
 *  @author Dylan Vairoli (326603)
 *  @author Giovanni Ranieri (326870)
 */
public class PublicPlayerState {

    private final int ticketCount;
    private final int cardCount;
    private final int carCount;
    private final List<Route> routes;
    private final int claimPoints;

    /**
     * Constructeur d'un état de joueur
     * @param ticketCount
     *                  le nombre de ticket du joueur
     * @param cardCount
     *                  le nombre de cartes du joueur
     * @param routes
     *              les routes dont le players s'est emparé
     */
    public PublicPlayerState(int ticketCount, int cardCount, List<Route> routes) {
        Preconditions.checkArgument(ticketCount >= 0 && cardCount >= 0);

        this.ticketCount = ticketCount;
        this.cardCount = cardCount;
        this.routes = routes;

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
     * @return le nombre de ticket que possède le joueur
     */
    public int ticketCount() {
        return ticketCount;
    }

    /**
     * @return le nombre de cartes possède le joueur
     */
    public int cardCount() {
        return cardCount;
    }

    /**
     * @return les routes dont le joueur s'est emparé
     */
    public List<Route> routes() {
        return List.copyOf(routes);
    }

    /**
     * @return le nombre de wagon que possède le joueur
     */
    public int carCount() {
        return carCount;
    }

    /**
     * @return le nombre de points de construction qu'à obtenu le joueur
     */
    public int claimPoints() {
        return claimPoints;
    }


}
