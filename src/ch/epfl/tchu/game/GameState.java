package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Représente l'état d'une partie de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class GameState extends PublicGameState {

    private final Deck<Ticket> tickets;

    /**
     * Constructeur d'un état de partie de tCHu.
     */
    private GameState(Deck<Ticket> tickets, PublicCardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, makePublic(playerState), lastPlayer);

        this.tickets = tickets;
    }

    /**
     * Retourne l'état initial d'une partie de tCHu dans laquelle la pioche des billets contient les billets donnés.
     * @param tickets
     *          billets donnés
     * @param rng
     *          générateur aléatoire donné
     * @return l'état initial d'une partie de tCHu
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {

        PlayerId firstPlayer = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));

        // 2. Create the cards of the deck and those in hand of the 2 players

        Deck<Card> cardsDeck = Deck.of(Constants.ALL_CARDS, rng);

        SortedBag<Card> firstPlayerCards = cardsDeck.topCards(4);
        cardsDeck = cardsDeck.withoutTopCards(4);

        SortedBag<Card> secondPlayerCards = cardsDeck.topCards(4);
        cardsDeck = cardsDeck.withoutTopCards(4);

        // 4. Create the player states of the 2 players
        PlayerState statePlayer1 = new PlayerState(SortedBag.of(), firstPlayerCards, List.of());
        PlayerState statePlayer2 = new PlayerState(SortedBag.of(), secondPlayerCards, List.of());

        //5. Deck of tickets
        Deck<Ticket> ticketsDeck = Deck.of(tickets, rng);

        Map<PlayerId, PlayerState> playerStateEnumMap = new EnumMap<>(PlayerId.class);
        playerStateEnumMap.put(firstPlayer, statePlayer1);

        CardState cardState = CardState.of(cardsDeck);

        return new GameState(ticketsDeck, cardState, firstPlayer, playerStateEnumMap, null);
    }

    private static Map<PlayerId, PublicPlayerState>  makePublic(Map<PlayerId, PlayerState> nonPublicMap) {
        Map<PlayerId, PublicPlayerState> publicMap = new EnumMap(PlayerId.class);
        publicMap.putAll(nonPublicMap);

        return publicMap;
    }

}
