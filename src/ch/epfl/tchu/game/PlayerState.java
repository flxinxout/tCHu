package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.List;

public final class PlayerState extends PublicPlayerState {
    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes){
        super(tickets.size(), cards.size(), routes);

        this.tickets = tickets;
        this.cards = cards;
    }

    public static PlayerState initial(SortedBag<Card> initialCards){
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), SortedBag.of(initialCards), List.of());
    }

    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }

    public SortedBag<Card> cards() {
        return cards;
    }

    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes());
    }

    public boolean canClaimRoute(Route route){
        if (carCount() >= route.length()) {
            for (SortedBag<Card> cardSet : route.possibleClaimCards()) {
                if (cards.contains(cardSet))
                    return true;
            }
        }

        return false;
    }


    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());
        return route.possibleClaimCards();
    }

    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount > 0 &&  additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        
    }
}
