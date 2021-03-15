package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.*;

public final class GameState extends PublicGameState {

    private final SortedBag<Ticket> tickets;

    private GameState(SortedBag<Ticket> tickets, PublicCardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, playerState, lastPlayer);

        this.tickets = tickets;
    }

    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {

        // 1. The 2 players
        PlayerId firstPlayer = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));
        PlayerId lastPlayer = firstPlayer.next();

        // 2. Create the cards in hand the 2 the players
        Collections.shuffle(tickets.toList(), rng);
        Deck<Card> deckOfCardForTheGame = Deck.of(Constants.ALL_CARDS, rng);
        SortedBag<Card> the8CardsFromTheTop = deckOfCardForTheGame.topCards(8);

        SortedBag.Builder<Card> cardsFirst = new SortedBag.Builder<>();
        SortedBag.Builder<Card> cardsLast = new SortedBag.Builder<>();
        for(int i = 0; i < Constants.INITIAL_CARDS_COUNT; i++) {
            cardsFirst.add(the8CardsFromTheTop.get(i));
            cardsLast.add(the8CardsFromTheTop.get(i + 4));
        }

        SortedBag<Card> cardsFirstPlayer = cardsFirst.build();
        SortedBag<Card> cardsLastPlayer = cardsFirst.build();

        // 3. Create the tickets for the 2 players
        SortedBag.Builder<Ticket> ticketsFirst = new SortedBag.Builder<>();
        SortedBag.Builder<Ticket> ticketsLast = new SortedBag.Builder<>();
        for(int i = 0; i < Constants.INITIAL_TICKETS_COUNT; i++) {
            ticketsFirst.add(tickets.get(i));
            ticketsLast.add(tickets.get(i + 5));
        }

        SortedBag<Ticket> ticketsFirstPlayer = ticketsFirst.build();
        SortedBag<Ticket> ticketsLastPlayer = ticketsLast.build();

        // 4. Create the public part of the 2 players
        PublicPlayerState statePlayer1 = makePublic(new PlayerState(ticketsFirstPlayer, cardsFirstPlayer, List.of()));
        PublicPlayerState statePlayer2 = makePublic(new PlayerState(ticketsLastPlayer, cardsLastPlayer, List.of()));

        Map<PlayerId, PublicPlayerState> playerStateEnumMap = new EnumMap<>(PlayerId.class);
        playerStateEnumMap.put(firstPlayer, statePlayer1);
        playerStateEnumMap.put(lastPlayer, statePlayer2);

        // 5. Create the card state of the game
        Deck<Card> cardsWithoutThe8CardsFromTheTop = deckOfCardForTheGame.withoutTopCards(8);

        //TODO j'ai mis un card state dans un public card state mais est-ce juste ? comme pour le makePublic() ?
        PublicCardState publicCardState = CardState.of(cardsWithoutThe8CardsFromTheTop);

        return new GameState(tickets, publicCardState, firstPlayer, playerStateEnumMap, lastPlayer);
    }

    private static PublicPlayerState makePublic(PlayerState playerState) {
        return new PublicPlayerState(playerState.ticketCount(), playerState.cardCount(), playerState.routes());
    }

}
