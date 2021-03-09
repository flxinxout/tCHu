package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;

/**
 * Permet de générer les textes décrivant le déroulement de la partie.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Info {

    private final String playerName;

    /**
     * Construit un générateur de messages liés au joueur ayant le nom donné.
     * @param playerName
     *          le nom du joueur
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Retourne le nom (français) de la carte donnée, au singulier ssi la valeur absolue
     * du second argument vaut 1.
     * @param card
     *          la carte donnée
     * @param count
     *          le nombre déterminant le pluriel/singulier
     * @return le nom (français) de la carte donnée
     */
    public static String cardName(Card card, int count) {
        String plural = StringsFr.plural(count);

        switch (card){
            case BLACK: return String.format("%s%s", StringsFr.BLACK_CARD, plural);
            case VIOLET: return String.format("%s%s", StringsFr.VIOLET_CARD, plural);
            case BLUE: return String.format("%s%s", StringsFr.BLUE_CARD, plural);
            case GREEN: return String.format("%s%s", StringsFr.GREEN_CARD, plural);
            case YELLOW: return String.format("%s%s", StringsFr.YELLOW_CARD, plural);
            case ORANGE: return String.format("%s%s", StringsFr.ORANGE_CARD, plural);
            case RED: return String.format("%s%s", StringsFr.RED_CARD, plural);
            case WHITE: return String.format("%s%s", StringsFr.WHITE_CARD, plural);
            default: return String.format("%s%s", StringsFr.LOCOMOTIVE_CARD, plural);
        }
    }

    /**
     * Retourne le message déclarant que les joueurs, dont les noms sont ceux donnés,
     * ont terminé la partie ex æqo en ayant chacun remporté les points donnés.
     * @param playerNames
     *          la liste des noms des joueurs
     * @param points
     *          les points remportés par les joueurs
     * @return le message décrit ci-dessus
     */
    public static String draw(List<String> playerNames, int points) {
        String playersNamesTogether = String.join(StringsFr.AND_SEPARATOR, playerNames);
        return String.format(StringsFr.DRAW, playersNamesTogether, points);
    }

    /**
     * Retourne le message déclarant que le joueur jouera en premier.
     * @return le message déclarant que le joueur jouera en premier
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a gardé le nombre de billets donné.
     * @param count
     *          le nombre de billets donné
     * @return le message déclarant que le joueur a gardé le nombre de billets donné
     */
    public String keptTickets(int count) {
        String plural = StringsFr.plural(count);
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count, plural);
    }

    /**
     * Retourne le message déclarant que le joueur peut jouer.
     * @return le message déclarant que le joueur peut jouer
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a tiré le nombre donné de billets.
     * @param count
     *          le nombre de billets tirés
     * @return le message déclarant que le joueur a tiré le nombre donné de billets
     */
    public String drewTickets(int count) {
        String plural = StringsFr.plural(count);
        return String.format(StringsFr.DREW_TICKETS, playerName, count, plural);
    }

    /**
     * Retourne le message déclarant que le joueur a tiré une carte "à l'aveugle", c-à-d du sommet de la pioche.
     * @return le message déclarant que le joueur a tiré une carte "à l'aveugle"
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a tiré la carte disposée face visible donnée.
     * @param card
     *          la carte tirée
     * @return le message déclarant que le joueur a tiré la carte disposée face visible donnée
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * Retourne le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     * @param route
     *          la route donnée
     * @param cards
     *          les cartes ayant permis de s'emparer de {@code route}
     * @return le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE,
                             playerName,
                             nameOf(route),
                             descriptionOf(cards));
    }

    /**
     * Retourne la représentation textuelle de la route donnée.
     */
    private static String nameOf(Route route){
        return (route.station1().name() + StringsFr.EN_DASH_SEPARATOR + route.station2().name());
    }

    /**
     * Retourne la description de l'ensemble de cartes donné.
     */
    private static String descriptionOf(SortedBag<Card> cards){
        List<String> singleCardNames = new ArrayList<>();

        for (Card c: cards.toSet()) {
            int n = cards.countOf(c);
            singleCardNames.add(n + " " + cardName(c, n));
        }

        int lastCardIndex = singleCardNames.size() - 1;
        String joined;
        if(singleCardNames.size() == 1) {
            joined = singleCardNames.get(0);
        } else {
            joined = String.join(StringsFr.AND_SEPARATOR,
                    String.join(", ", singleCardNames.subList(0, lastCardIndex)),
                    singleCardNames.get(lastCardIndex));
        }

        return joined;
    }

    /**
     * Retourne le message déclarant que le joueur désire s'emparer de la route en tunnel
     * donnée en utilisant initialement les cartes données.
     * @param route
     *          la route voulant être prise
     * @param initialCards
     *          les cartes initiales pour prendre le tunnel
     * @return le message décrit ci-dessus
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM,
                             playerName,
                             nameOf(route),
                             descriptionOf(initialCards));
    }

    /**
     * Retourne le message déclarant que le joueur a tiré les trois cartes additionnelles données,
     * et qu'elles impliquent un coût additionnel du nombre de cartes donné.
     * @param drawnCards
     *          les cartes additionnelles tirées
     * @param additionalCost
     *          le coût additionnel
     * @return retourne le message décrit ci-dessus
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String additionalCardsAre = String.format(StringsFr.ADDITIONAL_CARDS_ARE, descriptionOf(drawnCards));
        String additionalCosts = additionalCost != 0 ?
                String.format(StringsFr.SOME_ADDITIONAL_COST, additionalCost, StringsFr.plural(additionalCost)):
                String.format(StringsFr.NO_ADDITIONAL_COST);

        return String.format("%s%s", additionalCardsAre, additionalCosts);
    }

    /**
     * Retourne le message déclarant que le joueur n'a pas pu (ou voulu) s'emparer du tunnel donné
     * @param route
     *          le tunnel
     * @return retourne le message décrit ci-dessus
     */
    public String didNotClaimRoute(Route route){
        return String.format(StringsFr.DID_NOT_CLAIM_ROUTE, playerName, nameOf(route));
    }

    /**
     * Retourne le message déclarant que le joueur n'a plus que le nombre donné (et inférieur ou égale à 2) de wagons,
     * et que le dernier tour commence donc.
     * @param carCount
     *          le nombre de wagons
     * @return retourne le message décrit ci-dessus
     */
    public String lastTurnBegins(int carCount){
        return String.format(StringsFr.LAST_TURN_BEGINS, playerName, carCount, StringsFr.plural(carCount));
    }

    /**
     * Retourne le message déclarant que le joueur obtient le bonus de fin de partie grâce au chemin donné,
     * qui est le plus long, ou l'un des plus longs.
     * @param longestTrail
     *          le chemin
     * @return retourne le message décrit ci-dessus
     */
    public String getsLongestTrailBonus(Trail longestTrail){
        return String.format(StringsFr.GETS_BONUS, playerName, longestTrail);
    }

    /**
     * Retourne le message déclarant que le joueur remporte la partie avec le nombre de points donnés,
     * son adversaire n'en ayant obtenu que {@code loserPoints}.
     * @param points
     *          les points du vainqueur
     * @param loserPoints
     *          les points du perdant
     * @return retourne le message décrit ci-dessus
     */
    public String won(int points, int loserPoints){
        return String.format(StringsFr.WINS,
                             playerName,
                             points,
                             StringsFr.plural(points),
                             loserPoints,
                             StringsFr.plural(loserPoints));
    }
}















