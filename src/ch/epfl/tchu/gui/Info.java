package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Trail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ch.epfl.tchu.game.Constants.MINIMUM_NUMBER_PLAYERS;
import static ch.epfl.tchu.gui.StringsFr.*;

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
     *
     * @param playerName le nom du joueur
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Retourne le nom (français) de {@code card}, au singulier ssi la valeur absolue de {@code count} > 1.
     *
     * @param card  la carte donnée
     * @param count le nombre déterminant le pluriel/singulier
     * @return le nom (français) de la carte donnée
     */
    public static String cardName(Card card, int count) {
        String plural = plural(count);

        switch (card) {
            case BLACK:
                return BLACK_CARD + plural;
            case VIOLET:
                return VIOLET_CARD + plural;
            case BLUE:
                return BLUE_CARD + plural;
            case GREEN:
                return GREEN_CARD + plural;
            case YELLOW:
                return YELLOW_CARD + plural;
            case ORANGE:
                return ORANGE_CARD + plural;
            case RED:
                return RED_CARD + plural;
            case WHITE:
                return WHITE_CARD + plural;
            case LOCOMOTIVE:
                return LOCOMOTIVE_CARD + plural;
            default:
                throw new IllegalArgumentException("Type de carte invalide");
        }
    }

    /**
     * Retourne le message déclarant que les joueurs, dont les noms sont {@code playerNames},
     * ont terminé la partie ex æqo en ayant chacun remporté {@code points} points.
     *
     * @param playerNames la liste des noms des joueurs
     * @param points      les points remportés par les joueurs
     * @return le message déclarant que les joueurs ont terminé la partie ex æqo
     */
    public static String draw(List<String> playerNames, int points) {
        String playersNamesTogether = String.join(AND_SEPARATOR, playerNames);
        return String.format(DRAW, playersNamesTogether, points);
    }

    /**
     * Retourne le message déclarant que plusieurs joueurs (mais pas tous), dont les noms sont {@code winnerNames},
     * ont terminé la partie ex æqo en ayant chacun remporté {@code points} points et le joueur restant a perdu avec {@code loserPoints}.
     *
     * @param winnerNames la liste des noms des gagnants
     * @param points      les points remportés par les gagnants
     * @param loserPoints les points remportés par le perdant
     * @return le message déclarant que deux joueurs ont terminé la partie ex æqo
     */
    public static String drawMultiplePlayers(List<String> winnerNames, int points, int loserPoints) {
        Preconditions.checkArgument(winnerNames.size() <= PlayerId.COUNT && winnerNames.size() >= MINIMUM_NUMBER_PLAYERS);
        return String.format(DRAW_2_PLAYERS,
                joinListToString(winnerNames),
                points,
                plural(points),
                loserPoints,
                plural(loserPoints));
    }

    /**
     * Retourne la représentation textuelle de {@code route}.
     */
    private static String nameOf(Route route) {
        return (route.station1().name() + EN_DASH_SEPARATOR + route.station2().name());
    }

    /**
     * Retourne la représentation textuelle de la route donnée.
     */
    private static String nameOf(Trail trail) {
        return (trail.station1().name() + EN_DASH_SEPARATOR + trail.station2().name());
    }

    /**
     * Retourne la description de l'ensemble de cartes donné.
     */
    private static String descriptionOf(SortedBag<Card> cards) {
        List<String> singleCardNames = new ArrayList<>();

        for (Card c : cards.toSet()) {
            int n = cards.countOf(c);
            singleCardNames.add(n + " " + cardName(c, n));
        }

        return joinListToString(singleCardNames);
    }

    /**
     * Joint une liste de chaînes de caractères de la manière suivante: e_1, e_2, ..., e_s-1 et e_s où s est la taille
     * de la liste donnée.
     *
     * @param strings la liste dont les chaînes de caractères doivent être jointes
     * @return la représentation textuelle des éléments de la liste donnée joints
     */
    private static String joinListToString(List<String> strings) {
        String joined;
        if (strings.size() == 1) {
            joined = strings.get(0);
        } else {
            joined = String.join(AND_SEPARATOR,
                    String.join(", ", strings.subList(0, strings.size() - 1)),
                    strings.get(strings.size() - 1));
        }

        return joined;
    }

    /**
     * @return le message déclarant que ce joueur jouera en premier
     */
    public String willPlayFirst() {
        return String.format(WILL_PLAY_FIRST, playerName);
    }

    /**
     * Retourne le message déclarant que ce joueur a gardé {@code count} billets.
     *
     * @param count le nombre de billets gardés
     * @return le message déclarant que ce joueur a gardé {@code count} billets
     */
    public String keptTickets(int count) {
        String plural = plural(count);
        return String.format(KEPT_N_TICKETS, playerName, count, plural);
    }

    /**
     * @return le message déclarant que ce joueur peut jouer
     */
    public String canPlay() {
        return String.format(CAN_PLAY, playerName);
    }

    /**
     * Retourne le message déclarant que ce joueur a tiré {@code count} billets.
     *
     * @param count le nombre de billets tirés
     * @return le message déclarant que ce joueur a tiré {@code count} billets
     */
    public String drewTickets(int count) {
        String plural = plural(count);
        return String.format(DREW_TICKETS, playerName, count, plural);
    }

    /**
     * Retourne le message déclarant que ce joueur a tiré une carte "à l'aveugle", c-à-d du sommet de la pioche.
     *
     * @return le message déclarant que ce joueur a tiré une carte "à l'aveugle"
     */
    public String drewBlindCard() {
        return String.format(DREW_BLIND_CARD, playerName);
    }

    /**
     * Retourne le message déclarant que ce joueur a tiré la carte disposée face visible {@code card}.
     *
     * @param card la carte tirée
     * @return le message déclarant que ce joueur a tiré la carte disposée face visible {@code card}
     */
    public String drewVisibleCard(Card card) {
        return String.format(DREW_VISIBLE_CARD, playerName, cardName(card, 1));
    }

    /**
     * Retourne le message déclarant que ce joueur s'est emparé de {@code route} au moyen de {@code cards}.
     *
     * @param route la route dont il s'est emparée
     * @param cards les cartes ayant permis de s'emparer de {@code route}
     * @return le message déclarant que ce joueur s'est emparé de {@code route} au moyen de {@code cards}
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(CLAIMED_ROUTE,
                playerName,
                nameOf(route),
                descriptionOf(cards));
    }

    /**
     * Retourne le message déclarant que ce joueur désire s'emparer de {@code route} (en tunnel)
     * en utilisant initialement {@code initialCards}.
     *
     * @param route        la route en tunnel voulant être prise
     * @param initialCards les cartes initiales pour prendre le tunnel
     * @return le message déclarant que ce joueur désire s'emparer de {@code route} (en tunnel)
     * en utilisant initialement {@code initialCards}.
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        return String.format(ATTEMPTS_TUNNEL_CLAIM,
                playerName,
                nameOf(route),
                descriptionOf(initialCards));
    }

    /**
     * Retourne le message déclarant que ce joueur a tiré les trois cartes additionnelles {@code drawnCards},
     * et qu'elles impliquent un coût additionnel de {@code additionalCost} cartes.
     *
     * @param drawnCards     les cartes additionnelles tirées
     * @param additionalCost le coût additionnel impliqué
     * @return le message déclarant que ce joueur a tiré les trois cartes additionnelles {@code drawnCards},
     * et qu'elles impliquent un coût additionnel de {@code additionalCost} cartes.
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {
        String additionalCardsAre = String.format(ADDITIONAL_CARDS_ARE, descriptionOf(drawnCards));

        String additionalCosts = additionalCost != 0 ?
                String.format(SOME_ADDITIONAL_COST, additionalCost, plural(additionalCost)) :
                NO_ADDITIONAL_COST;

        return additionalCardsAre + additionalCosts;
    }

    /**
     * Retourne le message déclarant que ce joueur n'a pas pu (ou voulu) s'emparer du tunnel {@code route}.
     *
     * @param route le tunnel
     * @return le message déclarant que ce joueur n'a pas pu (ou voulu) s'emparer du tunnel {@code route}.
     */
    public String didNotClaimRoute(Route route) {
        return String.format(DID_NOT_CLAIM_ROUTE, playerName, nameOf(route));
    }

    /**
     * Retourne le message déclarant que ce joueur n'a plus que {@code carCount} (<= 2) de wagons,
     * et que le dernier tour commence donc.
     *
     * @param carCount le nombre de wagons de ce joueur
     * @return le message déclarant que le dernier tour commence
     */
    public String lastTurnBegins(int carCount) {
        return String.format(LAST_TURN_BEGINS, playerName, carCount, plural(carCount));
    }

    /**
     * Retourne le message déclarant que ce joueur obtient le bonus de fin de partie grâce à {@code longestTrail},
     * qui est le plus long, ou l'un des plus longs.
     *
     * @param longestTrail un des plus longs chemins
     * @return le message déclarant que ce joueur obtient le bonus de fin de partie grâce à {@code longestTrail}
     */
    public String getsLongestTrailBonus(Trail longestTrail) {
        return String.format(GETS_BONUS, playerName, nameOf(longestTrail));
    }

    /**
     * Retourne le message déclarant que le joueur remporte la partie avec {@code points} points
     * et ses adversaires n'en ayant obtenu que {@code loserPoints}. Si plus de 3 éléments sont donnés en tant
     * que loserPoints, seuls les 3 donnés sont comptés
     *
     * @param points      les points du vainqueur
     * @param loserPoints les points des perdants
     * @return le message déclarant que le joueur remporte la partie avec {@code points} points
     * @throws IllegalArgumentException si {@code loserPoints} est vide ou plus grand que 3
     */
    public String won(int points, int... loserPoints) {
        Preconditions.checkArgument(loserPoints.length > 0 && loserPoints.length <= 3);
        List<String> pointsStr = Stream.of(loserPoints)
                .map(String::valueOf)
                .collect(Collectors.toList());

        return String.format(WINS,
                playerName,
                points,
                plural(points),
                pointsStr);
    }
}
















