package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;

public final class Constants {
    private Constants() {}

    /**
     * Nombre de cartes wagon de chaque couleur.
     */
    public static final int INDIVIDUAL_CAR_CARDS_COUNT = 6;

    /**
     * Nombre de cartes locomotive.
     */
    public static final int INDIVIDUAL_LOCOMOTIVE_CARDS_COUNT = 7;

    /**
     * Toutes les cartes du jeu.
     */
    public static SortedBag<Card> computeAllCards(int playerNb) {
        var cardsBuilder = new SortedBag.Builder<Card>();
        cardsBuilder.add(INDIVIDUAL_LOCOMOTIVE_CARDS_COUNT * playerNb, Card.LOCOMOTIVE);
        for (Card card : Card.CARS)
            cardsBuilder.add(INDIVIDUAL_CAR_CARDS_COUNT * playerNb, card);
        return cardsBuilder.build();
    }

    /**
     * Nombre total de cartes wagon/locomotive.
     */
    public static int computeTotalCardsCount(int playerNb){
        return computeAllCards(playerNb).size();
    }

    /**
     * Numéro d'emplacement fictif désignant la pioche de cartes.
     */
    public static final int DECK_SLOT = -1;

    /**
     * Liste de tous les numéros d'emplacements de cartes face visible.
     */
    public static final List<Integer> FACE_UP_CARD_SLOTS = List.of(0, 1, 2, 3, 4);

    /**
     * Nombre d'emplacements pour les cartes face visible.
     */
    public static final int FACE_UP_CARDS_COUNT = FACE_UP_CARD_SLOTS.size();

    /**
     * Nombre de billets distribués à chaque joueur en début de partie.
     */
    public static final int INITIAL_TICKETS_COUNT = 5;

    /**
     * Nombre de cartes distribuées à chaque joueur en début de partie.
     */
    public static final int INITIAL_CARDS_COUNT = 4;

    /**
     * Nombre de wagons dont dispose chaque joueur en début de partie.
     */
    public static final int INITIAL_CAR_COUNT = 60;

    /**
     * Nombre de billets tirés à la fois en cours de partie.
     */
    public static final int IN_GAME_TICKETS_COUNT = 3;

    /**
     * Nombre maximum de billets qu'un joueur peut défausser lors d'un tirage.
     */
    public static final int DISCARDABLE_TICKETS_COUNT = 2;

    /**
     * Nombre de cartes à tirer lors de la construction d'un tunnel.
     */
    public static final int ADDITIONAL_TUNNEL_CARDS = 3;

    /**
     * Nombre de points obtenus pour la construction de routes de longueur 1 à 6.
     * (L'élément à l'index i correspond à une longueur de route i. Une valeur
     @ -89,24 +44,73 @@ public final class Constants {
     */
    public static final List<Integer> ROUTE_CLAIM_POINTS =
            List.of(Integer.MIN_VALUE, 1, 2, 4, 7, 10, 15);

    /**
     * Longueur minimum d'une route.
     */
    public static final int MIN_ROUTE_LENGTH = 1;

    /**
     * Longueur maximum d'une route.
     */
    public static final int MAX_ROUTE_LENGTH = ROUTE_CLAIM_POINTS.size() - 1;

    /**
     * Nombre de points bonus obtenus par le(s) joueur(s) disposant du plus long chemin.
     */
    public static final int LONGEST_TRAIL_BONUS_POINTS = 10;

    /**
     * Nombre minimal de joueurs pour jouer une partie.
     */
    public static final int MINIMUM_NUMBER_PLAYERS = 2;
}
