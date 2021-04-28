package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

import static ch.epfl.tchu.game.ChMap.*;
import static ch.epfl.tchu.game.Constants.*;

public final class ObservableGameState {

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
        this.faceUpCards = new ArrayList<>(FACE_UP_CARDS_COUNT);

        for (int i = 0; i < FACE_UP_CARDS_COUNT; i++)
            faceUpCards.add(new SimpleObjectProperty<>());

        this.routesOwner = new HashMap<>(routes().size());

        //2.
        this.ticketCount = new HashMap<>(PlayerId.COUNT);
        this.cardCount = new HashMap<>(PlayerId.COUNT);
        this.carCount = new HashMap<>(PlayerId.COUNT);
        this.claimPoints = new HashMap<>(PlayerId.COUNT);
        for(PlayerId pId : PlayerId.ALL) {
            ticketCount.put(pId, new SimpleIntegerProperty());
            cardCount.put(pId, new SimpleIntegerProperty());
            carCount.put(pId, new SimpleIntegerProperty());
            claimPoints.put(pId, new SimpleIntegerProperty());
        }

        //3.
        this.tickets = FXCollections.observableArrayList();

        this.cardOccurrences = new HashMap<>(Card.COUNT);
        for (Card card : Card.ALL)
            cardOccurrences.put(card, new SimpleIntegerProperty());

        this.routesClaimable = new HashMap<>(routes().size());
        for (Route route : routes()) {
            routesOwner.put(route, new SimpleObjectProperty<>());
            routesClaimable.put(route, new SimpleBooleanProperty());
        }
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
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketsPercentage);
    }

    public ReadOnlyIntegerProperty cardsPercentage() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardsPercentage);
    }

    public ReadOnlyObjectProperty<Card> faceUpCardAt(int slot) {
        return faceUpCards.get(slot);
    }


    public ReadOnlyObjectProperty<PlayerId> ownerOf(Route route) {
        return routesOwner.get(route);
    }

    public ReadOnlyIntegerProperty ticketsCountOf(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketCount.get(id));
    }

    public ReadOnlyIntegerProperty cardsCountOf(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardCount.get(id));
    }

    public ReadOnlyIntegerProperty carsCountOf(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(carCount.get(id));
    }

    public ReadOnlyIntegerProperty claimPointsOf(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(claimPoints.get(id));
    }

    public ObservableList<Ticket> tickets() {
        return FXCollections.unmodifiableObservableList(tickets);
    }

    public ReadOnlyIntegerProperty occurrencesOf(Card card) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardOccurrences.get(card));
    }

    public ReadOnlyBooleanProperty claimable(Route route) {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(routesClaimable.get(route));
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
