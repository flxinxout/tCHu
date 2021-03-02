package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;

import javax.management.DescriptorRead;
import javax.print.DocFlavor;
import java.util.List;

public final class Info {

    private final String playerName;

    /**
     * Constructeur d'un générateur de message spécifique à un joueur
     * @param playerName
     *                  le nom du joueur
     */
    public Info(String playerName) {
        this.playerName = playerName;
    }

    /**
     * Retourne le nom (français) de la carte donnée, au singulier ssi la valeur absolue
     * du second argument vaut 1
     * @param card la carte donnée
     * @param count le nombre à check si sa valeur absolue est de 1
     * @return le nom (français) de la carte donnée, au singulier ssi la valeur absolue du second argument vaut 1
     */
    public static String cardName(Card card, int count) {
        if(Math.abs(count) == 1) {
            return "";//TODO GET LA BONNE CARTE AVEC CARD ?
        }
        return null;
    }

    /**
     * Retourne le message déclarant que les joueurs, dont les noms sont ceux donnés,
     * ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     * @param playerNames la listes des noms des players
     * @param points les points remportés par les joueurs
     * @return le message déclarant que les joueurs, dont les noms sont ceux donnés,
     * ont terminé la partie ex æqo en ayant chacun remporté les points donnés
     */
    public static String draw(List<String> playerNames, int points) {
        String playersNameTogether = String.join(" et ", playerNames);
        return String.format(StringsFr.DRAW, playersNameTogether, points);
    }

    /**
     * Retourne le message déclarant que le joueur jouera en premier
     * @return le message déclarant que le joueur jouera en premier
     */
    public String willPlayFirst() {
        return String.format(StringsFr.WILL_PLAY_FIRST, playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a gardé le nombre de billets donné
     * @param count le nombre de billets donné
     * @return le message déclarant que le joueur a gardé le nombre de billets donné
     */
    public String keptTickets(int count) {
        return String.format(StringsFr.KEPT_N_TICKETS, playerName, count);
    }

    /**
     * Retourne le message déclarant que le joueur peut jouer
     * @return le message déclarant que le joueur peut jouer
     */
    public String canPlay() {
        return String.format(StringsFr.CAN_PLAY, playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a tiré le nombre donné de billets
     * @param count le nombre de billets tirés
     * @return le message déclarant que le joueur a tiré le nombre donné de billets
     */
    public String drewTickets(int count) {
        return String.format(StringsFr.DREW_TICKETS, playerName, count);
    }

    /**
     * retourne le message déclarant que le joueur a tiré une carte « à l'aveugle », c-à-d du sommet de la pioche
     * @return le message déclarant que le joueur a tiré une carte « à l'aveugle », c-à-d du sommet de la pioche
     */
    public String drewBlindCard() {
        return String.format(StringsFr.DREW_BLIND_CARD, playerName);
    }

    /**
     * Retourne le message déclarant que le joueur a tiré la carte disposée face visible donnée
     * @param card la carte tirée
     * @return le message déclarant que le joueur a tiré la carte disposée face visible donnée
     */
    public String drewVisibleCard(Card card) {
        return String.format(StringsFr.DREW_VISIBLE_CARD, playerName, card.name());
    }

    /**
     * Retourne le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     * @param route la route donnée
     * @param cards les cartes ayant permis de s'emparer de la {@note route}
     * @return le message déclarant que le joueur s'est emparé de la route donnée au moyen des cartes données
     */
    public String claimedRoute(Route route, SortedBag<Card> cards) {
        return String.format(StringsFr.CLAIMED_ROUTE,
                playerName,
                route.station1().name() + " - " + route.station2().name(),
                cards.toList());

        //TODO LE CARDS.TOLIST FAUDRA LE FAIRE PAR UN MOYEN SIMPLE OU BIEN ON DOIT TOUT COMPUTE ?
    }

    /**
     * retourne le message déclarant que le joueur désire s'emparer de la route en tunnel
     * donnée en utilisant initialement les cartes données
     * @param route la route voulant être prise
     * @param initialCards les cartes initiales pour prendre le tunnel
     * @return le message déclarant que le joueur désire s'emparer de la route en tunnel
     * donnée en utilisant initialement les cartes données
     */
    public String attemptsTunnelClaim(Route route, SortedBag<Card> initialCards) {
        List<Card> initialCardsList = initialCards.toList();
        String textCards = ""; //TODO JSAIS PAS DU COUP C'EST COMME POUR LA METHODE EN HAUT
        return String.format(StringsFr.ATTEMPTS_TUNNEL_CLAIM,
                playerName,
                route.station1().name() + " - " + route.station2().name(),
                initialCards.toList());
    }


    /**
     * Retourne le message déclarant que le joueur a tiré les trois cartes additionnelles données,
     * et qu'elles impliquent un coût additionel du nombre de cartes donné
     * @param drawnCards les cartes additionnelles tirées
     * @param additionalCost le coût additionnel
     * @return retourne le message
     */
    public String drewAdditionalCards(SortedBag<Card> drawnCards, int additionalCost) {

    }


}















