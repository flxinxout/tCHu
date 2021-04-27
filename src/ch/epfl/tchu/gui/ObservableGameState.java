package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Constants.*;

public final class ObservableGameState {

    //TODO: mettre en property?
    private final PlayerId id;

    //TODO: faut-il créer des accesseurs pour ces 2?
    private final ObjectProperty<PublicGameState> gameState;
    private final ObjectProperty<PlayerState> playerState;

    private final IntegerProperty ticketsPercentage, cardsPercentage;
    private final List<ObjectProperty<Card>> faceUpCards;
    //TODO: bien Map?
    private final Map<String, ObjectProperty<PlayerId>> routeOwner;

    private final Map<PlayerId, IntegerProperty> ticketCount, cardCount, carCount, claimPoints;

    private final ObjectProperty<SortedBag<Ticket>> tickets;
    //TODO: bien Map?
    private final Map<Card, IntegerProperty> cardOccurences;
    private final Map<String, BooleanProperty> routesClaimed;

    /**
     * Construit un état de jeu observable correspondant à l'identité du joueur donnée. À la création, la totalité des
     * propriétés de l'état ont leur valeur par défaut.
     *
     * @param id l'identité du joueur attaché à cet état de jeu
     */
    public ObservableGameState(PlayerId id) {
        this.id = id;

        this.gameState = new SimpleObjectProperty<>(null);
        this.playerState = new SimpleObjectProperty<>(null);

        this.ticketsPercentage = new SimpleIntegerProperty(0);
        this.cardsPercentage = new SimpleIntegerProperty(0);
        this.faceUpCards = new ArrayList<>(FACE_UP_CARDS_COUNT);
        this.routeOwner = new HashMap<>(ChMap.routes().size());

        this.ticketCount = new HashMap<>(PlayerId.COUNT);
        this.cardCount = new HashMap<>(PlayerId.COUNT);
        this.carCount = new HashMap<>(PlayerId.COUNT);
        this.claimPoints = new HashMap<>(PlayerId.COUNT);

        this.tickets = new SimpleObjectProperty<>(null);
        this.cardOccurences = new HashMap<>(Card.COUNT);
        this.routesClaimed = new HashMap<>(ChMap.routes().size());
    }

    /**
     * Met à jour la totalité des propriétés de l'état en fonction des deux états donnés.
     *
     * @param newGameState le nouvel état de jeu
     * @param playerState  le nouvel état du joueur associé à cet état de jeu
     */
    public void setState(PublicGameState newGameState, PlayerState playerState) {
        gameState.setValue(newGameState);

        ticketsPercentage.setValue(100 * newGameState.ticketsCount() / ChMap.tickets().size());
        cardsPercentage.setValue(100 * newGameState.cardState().deckSize() / TOTAL_CARDS_COUNT);
        for (int slot : FACE_UP_CARD_SLOTS)
            faceUpCards.get(slot).setValue(newGameState.cardState().faceUpCard(slot));
        for (Route claimedRoute : newGameState.claimedRoutes()) {
            for (PlayerId pId : PlayerId.ALL) {
                if (newGameState.playerState(pId).routes().contains(claimedRoute))
                    routeOwner.get(claimedRoute.id()).setValue(pId);
            }
        }

        for (PlayerId pId : PlayerId.ALL) {
            ticketCount.get(pId).setValue(newGameState.playerState(pId).ticketCount());
            cardCount.get(pId).setValue(newGameState.playerState(pId).cardCount());
            carCount.get(pId).setValue(newGameState.playerState(pId).carCount());
            claimPoints.get(pId).setValue(newGameState.playerState(pId).claimPoints());
        }

        tickets.setValue(playerState.tickets());
        for (Card card : Card.ALL)
            cardOccurences.get(card).setValue(playerState.cards().countOf(card));
        for (Route route : ChMap.routes())
            //TODO: check les routes doubles
            routesClaimed.get(route.id()).setValue(newGameState.currentPlayerId() == id &&
                    !newGameState.claimedRoutes().contains(route) &&
                    playerState.canClaimRoute(route));
    }

    public ReadOnlyIntegerProperty ticketsPercentageProperty() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketsPercentage);
    }

    //TODO faut-il faire les getters du contenu des propriétés comme précisé §4.3 du cours sur JAVAFX?
    public int getTicketsPercentage(){
        return ticketsPercentage.getValue();
    }

    public ReadOnlyIntegerProperty cardsPercentageProperty() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardsPercentage);
    }

    public ReadOnlyObjectProperty<Card> faceUpCardPropertyAt(int slot) {
        return faceUpCards.get(slot);
    }


    public ReadOnlyObjectProperty<PlayerId> routesOwnerProperty(String routeId) {
        return routeOwner.get(routeId);
    }

    public ReadOnlyIntegerProperty ticketsCountProperty(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketCount.get(id));
    }

    public ReadOnlyIntegerProperty cardsCountProperty(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardCount.get(id));
    }

    public ReadOnlyIntegerProperty carsCountProperty(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(carCount.get(id));
    }

    public ReadOnlyIntegerProperty claimPointsProperty(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(claimPoints.get(id));
    }

    public ReadOnlyObjectProperty<SortedBag<Ticket>> ticketsProperty() {
        return tickets;
    }

    public ReadOnlyIntegerProperty cardOccurrencesProperty(Card card) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardOccurences.get(card));
    }

    public ReadOnlyBooleanProperty routesClaimedProperty(String routeId) {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(routesClaimed.get(routeId));
    }

    public boolean canDrawTickets() {
        return (gameState.getValue() != null && gameState.getValue().canDrawTickets());
    }

    public boolean canDrawCards() {
        return (gameState.getValue() != null && gameState.getValue().canDrawCards());
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        if (playerState.getValue() != null)
            return playerState.getValue().possibleClaimCards(route);
        else
            return List.of();
    }

    public ReadOnlyBooleanProperty claimable(Route route) {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(new SimpleBooleanProperty(
                playerState.getValue() != null && playerState.getValue().canClaimRoute(route)));
    }
}
