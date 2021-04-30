package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

import static ch.epfl.tchu.game.ChMap.*;
import static ch.epfl.tchu.game.Constants.*;

/**
 * Représente l'état observable d'une partie de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class ObservableGameState {

    private static List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> faceUpCards = new ArrayList<>(FACE_UP_CARDS_COUNT);
        for (int i = 0; i < FACE_UP_CARDS_COUNT; i++)
            faceUpCards.add(new SimpleObjectProperty<>());
        return faceUpCards;
    }

    private static Map<Route, ObjectProperty<PlayerId>> createRoutesOwner() {
        Map<Route, ObjectProperty<PlayerId>> routesMap = new HashMap<>();
        for (Route route : routes())
            routesMap.put(route, new SimpleObjectProperty<>());
        return routesMap;
    }

    private static Map<Route, BooleanProperty> createRoutesClaimable() {
        Map<Route, BooleanProperty> routesMap = new HashMap<>();
        for (Route route : routes())
            routesMap.put(route, new SimpleBooleanProperty());
        return routesMap;
    }

    private static Map<PlayerId, IntegerProperty> createPlayerIdMap() {
        Map<PlayerId, IntegerProperty> map = new EnumMap<>(PlayerId.class);
        for (PlayerId id : PlayerId.ALL)
            map.put(id, new SimpleIntegerProperty());
        return map;
    }

    private static Map<Card, IntegerProperty> createCardOccurences() {
        Map<Card, IntegerProperty> cardOccurrences = new EnumMap<>(Card.class);
        for (Card card: Card.ALL)
            cardOccurrences.put(card, new SimpleIntegerProperty());
        return cardOccurrences;
    }

    private final PlayerId id;
    private PublicGameState gameState;
    private PlayerState playerState;

    //Groupe 1: propriétés publiques
    private final IntegerProperty ticketsPercentage, cardsPercentage;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routesOwner;

    //Groupe 2: propriétés propres à chacun des joueurs
    private final Map<PlayerId, IntegerProperty> ticketCount, cardCount, carCount, claimPoints;

    //Groupe 3: propriétés propre au joueur associé à cet état de jeu
    private final ObservableList<Ticket> tickets;
    private final Map<Card, IntegerProperty> cardOccurrences;
    private final Map<Route, BooleanProperty> routesClaimable;

    /**
     * Construit un état de jeu observable correspondant à l'identité du joueur donnée. À la création, la totalité des
     * propriétés de l'état ont leur valeur par défaut.
     *
     * @param id l'identité du joueur attaché à cet état de jeu observable
     */
    public ObservableGameState(PlayerId id) {
        this.id = id;

        this.gameState = null;
        this.playerState = null;

        //1.
        this.ticketsPercentage = new SimpleIntegerProperty(0);
        this.cardsPercentage = new SimpleIntegerProperty(0);
        this.faceUpCards = createFaceUpCards();
        this.routesOwner = createRoutesOwner();

        //2.
        this.ticketCount = createPlayerIdMap();
        this.cardCount = createPlayerIdMap();
        this.carCount = createPlayerIdMap();
        this.claimPoints = createPlayerIdMap();

        //3.
        this.tickets = FXCollections.observableArrayList();
        this.cardOccurrences = createCardOccurences();
        this.routesClaimable = createRoutesClaimable();
    }

    /**
     * Met à jour la totalité des propriétés de l'état en fonction des deux états donnés.
     *
     * @param newGameState le nouvel état de jeu
     * @param playerState  le nouvel état du joueur associé à cet état de jeu
     */
    public void setState(PublicGameState newGameState, PlayerState playerState) {
        gameState = newGameState;
        this.playerState = playerState;

        //1.
        ticketsPercentage.setValue(100 * newGameState.ticketsCount() / ChMap.tickets().size());
        cardsPercentage.setValue(100 * newGameState.cardState().deckSize() / TOTAL_CARDS_COUNT);

        for (int slot : FACE_UP_CARD_SLOTS)
            faceUpCards.get(slot).setValue(newGameState.cardState().faceUpCard(slot));

        for (Route claimedRoute : newGameState.claimedRoutes()) {
            for (PlayerId pId : PlayerId.ALL) {
                if (newGameState.playerState(pId).routes().contains(claimedRoute))
                    routesOwner.get(claimedRoute).setValue(pId);
            }
        }

        //2.
        for (PlayerId pId : PlayerId.ALL) {
            ticketCount.get(pId).setValue(newGameState.playerState(pId).ticketCount());
            cardCount.get(pId).setValue(newGameState.playerState(pId).cardCount());
            carCount.get(pId).setValue(newGameState.playerState(pId).carCount());
            claimPoints.get(pId).setValue(newGameState.playerState(pId).claimPoints());
        }

        //3.
        tickets.setAll(playerState.tickets().toList());

        for (Card card : Card.ALL)
            cardOccurrences.get(card).setValue(playerState.cards().countOf(card));

        Set<List<Station>> stations = new HashSet<>();
        for (Route claimedRoute : newGameState.claimedRoutes())
            stations.add(claimedRoute.stations());
        for (Route route : routes())
            routesClaimable.get(route).setValue(newGameState.currentPlayerId() == id &&
                    !newGameState.claimedRoutes().contains(route) &&
                    !stations.contains(route.stations()) &&
                    playerState.canClaimRoute(route));
    }

    public ReadOnlyIntegerProperty ticketsPercentage() {
        return ticketsPercentage;
    }

    public ReadOnlyIntegerProperty cardsPercentage() {
        return cardsPercentage;
    }

    public ReadOnlyObjectProperty<Card> faceUpCardAt(int slot) {
        return faceUpCards.get(slot);
    }


    public ReadOnlyObjectProperty<PlayerId> ownerOf(Route route) {
        return routesOwner.get(route);
    }

    public ReadOnlyIntegerProperty ticketsCountOf(PlayerId id) {
        return ticketCount.get(id);
    }

    public ReadOnlyIntegerProperty cardsCountOf(PlayerId id) {
        return cardCount.get(id);
    }

    public ReadOnlyIntegerProperty carsCountOf(PlayerId id) {
        return carCount.get(id);
    }

    public ReadOnlyIntegerProperty claimPointsOf(PlayerId id) {
        return claimPoints.get(id);
    }

    public ObservableList<Ticket> tickets() {
        return FXCollections.unmodifiableObservableList(tickets);
    }

    public ReadOnlyIntegerProperty occurrencesOf(Card card) {
        return cardOccurrences.get(card);
    }

    public ReadOnlyBooleanProperty claimable(Route route) {
        return routesClaimable.get(route);
    }

    public boolean canDrawTickets() {
        return (gameState != null && gameState.canDrawTickets());
    }

    public boolean canDrawCards() {
        return (gameState != null && gameState.canDrawCards());
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        if (playerState != null)
            return playerState.possibleClaimCards(route);
        else
            return List.of();
    }
}
