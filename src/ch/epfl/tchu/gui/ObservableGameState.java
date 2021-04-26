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

    private final ObjectProperty<Integer> ticketsPercentage, cardsPercentage;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final List<ObjectProperty<PlayerId>> routes;

    private final Map<PlayerId, ObjectProperty<Integer>> ticketsCount, cardsCount, carsCount, claimPoints;

    private final ObjectProperty<SortedBag<Ticket>> tickets;
    private final List<ObjectProperty<Integer>> cards;
    private final List<ObjectProperty<Boolean>> routesClaimed;

    private final PlayerId playerId;
    private PublicGameState gameState;
    private PlayerState playerState;

    public ObservableGameState(PlayerId id){
        this.ticketsPercentage = new SimpleObjectProperty<>(0);
        this.cardsPercentage = new SimpleObjectProperty<>(0);
        this.faceUpCards = new ArrayList<>(FACE_UP_CARDS_COUNT);
        this.routes = new ArrayList<>(ChMap.routes().size());

        this.ticketsCount = new HashMap<>(PlayerId.COUNT);
        this.cardsCount = new HashMap<>(PlayerId.COUNT);
        this.carsCount = new HashMap<>(PlayerId.COUNT);
        this.claimPoints = new HashMap<>(PlayerId.COUNT);

        this.tickets = new SimpleObjectProperty<>(null);
        this.cards = new ArrayList<>(Card.COUNT);
        this.routesClaimed = new ArrayList<>(ChMap.routes().size());

        this.playerId = id;
    }

    public void setState(PublicGameState newGameState, PlayerState playerState){
        //TODO: casts
        ticketsPercentage.setValue((int) ((double) newGameState.ticketsCount() / (double) ChMap.tickets().size() * 100));
        cardsPercentage.setValue((int) ((double) newGameState.cardState().deckSize() / (double) TOTAL_CARDS_COUNT * 100));
        for (int slot : FACE_UP_CARD_SLOTS) {
            Card newCard = newGameState.cardState().faceUpCard(slot);
            faceUpCards.get(slot).set(newCard);
        }
        //TODO: remettre null après coup??
        for (int i = 0; i < ChMap.routes().size(); i++) {
            for (PlayerId pId : PlayerId.ALL) {
                if (newGameState.playerState(pId).routes().contains(ChMap.routes().get(i)))
                    routes.get(i).set(pId);
            }
        }

        for (PlayerId pId : PlayerId.ALL) {
            ticketsCount.get(pId).setValue(newGameState.playerState(pId).ticketCount());
            cardsCount.get(pId).setValue(newGameState.playerState(pId).cardCount());
            carsCount.get(pId).setValue(newGameState.playerState(pId).carCount());
            claimPoints.get(pId).setValue(newGameState.playerState(pId).claimPoints());
        }

        tickets.setValue(playerState.tickets());
        //todo: pas beau mais vasy j'ai pas d'idée
        for(int i = 0; i < Card.COUNT; i++) {
            int j = 0;
            for(Card card : playerState.cards()) {
                if(card.ordinal() == i) {
                    j++;
                }
            }
            cards.get(i).setValue(j);
        }

        for (int i = 0; i < ChMap.routes().size(); i++) {
            Route route = ChMap.routes().get(i);
            if(newGameState.currentPlayerId() == playerId) {

                // Vu qu'on sait que c'est le joueur courant, alors pas besoin de for each tout les joueurs.
                if(!newGameState.currentPlayerState().routes().contains(route)) {
                    //TODO: savoir si c'est une route double ??

                    // si il ne l'a pas, logiquement on peut continue sur la prochaine carte non ?
                    routesClaimed.get(i).set(false);
                    continue;
                }

                //TODO: est-ce qu'il faut différentier les tunnels des routes ? car la donnée est pas claire...
                if(!playerState.canClaimRoute(route)) {
                    // si il ne peut pas, logiquement on peut continue sur la prochaine carte non ?
                    routesClaimed.get(i).set(false);
                    continue;
                }
            }
            routesClaimed.get(i).setValue(true);
        }
    }

    public ReadOnlyIntegerProperty ticketsPercentage() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketsPercentage);
    }

    public ReadOnlyIntegerProperty cardsPercentage() {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardsPercentage);
    }

    public ReadOnlyObjectProperty<Card> faceUpCard(int slot) {
        return faceUpCards.get(slot);
    }

    public ReadOnlyObjectProperty<PlayerId> route(int slot) {
        return routes.get(slot);
    }

    public ReadOnlyIntegerProperty ticketsCount(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(ticketsCount.get(id));
    }

    public ReadOnlyIntegerProperty cardsCount(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cardsCount.get(id));
    }

    public ReadOnlyIntegerProperty carsCount(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(carsCount.get(id));
    }

    public ReadOnlyIntegerProperty points(PlayerId id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(claimPoints.get(id));
    }

    public ReadOnlyObjectProperty<SortedBag<Ticket>> tickets() {
        return tickets;
    }

    public ReadOnlyIntegerProperty card(int id) {
        return ReadOnlyIntegerProperty.readOnlyIntegerProperty(cards.get(id));
    }

    public ReadOnlyBooleanProperty routesClaimed(int id) {
        return ReadOnlyBooleanProperty.readOnlyBooleanProperty(routesClaimed.get(id));
    }

    public boolean canDrawTickets(){
        return gameState.canDrawTickets();
    }

    public boolean canDrawCards(){
        return gameState.canDrawCards();
    }

    public List<SortedBag<Card>> possibleClaimCards(Route route){
        return playerState.possibleClaimCards(route);
    }
}
