package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
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
        PlayerId secondPlayer = firstPlayer.next();

        Deck<Ticket> ticketsDeck = Deck.of(tickets, rng);

        //Create the cards of the deck and those in hand of the 2 players
        Deck<Card> cardsDeck = Deck.of(Constants.ALL_CARDS, rng);

        SortedBag<Card> firstPlayerCards = cardsDeck.topCards(4);
        cardsDeck = cardsDeck.withoutTopCards(4);

        SortedBag<Card> secondPlayerCards = cardsDeck.topCards(4);
        cardsDeck = cardsDeck.withoutTopCards(4);

        //Create the player states of the 2 players
        PlayerState statePlayer1 = PlayerState.initial(firstPlayerCards);
        PlayerState statePlayer2 = PlayerState.initial(secondPlayerCards);

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(firstPlayer, statePlayer1);
        playerState.put(secondPlayer, statePlayer2);

        CardState cardState = CardState.of(cardsDeck);

        return new GameState(ticketsDeck, cardState, firstPlayer, playerState, null);
    }

    private static Map<PlayerId, PublicPlayerState>  makePublic(Map<PlayerId, PlayerState> nonPublicMap) {
        Map<PlayerId, PublicPlayerState> publicMap = new EnumMap(PlayerId.class);
        publicMap.putAll(nonPublicMap);

        return publicMap;
    }

    /**
     * Retourne l'état complet du joueur d'identité donnée.
     *
     * @param playerId
     *          le joueur donné
     * @return l'état complet du joueur d'identité donnée
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return (PlayerState) super.playerState(playerId);
    }

    /**
     * Retourne l'état complet du joueur courant.
     * @return Retourne l'état complet du joueur courant
     */
    @Override
    public PlayerState currentPlayerState() {
        return (PlayerState) super.currentPlayerState();
    }

    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return tickets.topCards(count);
    }

    public GameState withoutTopTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());

        //TODO: better way?
        PlayerId currentPlayer = currentPlayerId();
        PlayerId otherPlayer = currentPlayerId().next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(currentPlayer, currentPlayerState());
        playerState.put(otherPlayer, playerState(otherPlayer));

        return new GameState(tickets.withoutTopCards(count), cardState(), currentPlayerId(), playerState, lastPlayer());
    }

    public Card topCard() {
        Preconditions.checkArgument(!cardState().isDeckEmpty());
        //TODO: cast? or constructor?
        CardState cardState = (CardState) cardState();
        return cardState.topDeckCard();
    }

    public GameState withoutTopCard(){
        Preconditions.checkArgument(!cardState().isDeckEmpty());
        //TODO: better way?
        PlayerId currentPlayer = currentPlayerId();
        PlayerId otherPlayer = currentPlayerId().next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(currentPlayer, currentPlayerState());
        playerState.put(otherPlayer, playerState(otherPlayer));

        CardState cardState = (CardState) cardState();
        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
    }

    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards){
        //TODO: better way?
        PlayerId currentPlayer = currentPlayerId();
        PlayerId otherPlayer = currentPlayerId().next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(currentPlayer, currentPlayerState());
        playerState.put(otherPlayer, playerState(otherPlayer));

        CardState cardState = (CardState) cardState();
        return new GameState(tickets, cardState.withMoreDiscardedCards(discardedCards), currentPlayerId(), playerState, lastPlayer());
    }

    public GameState withCardsDeckRecreatedIfNeeded(Random rng){
        //TODO: better way?
        PlayerId currentPlayer = currentPlayerId();
        PlayerId otherPlayer = currentPlayerId().next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(currentPlayer, currentPlayerState());
        playerState.put(otherPlayer, playerState(otherPlayer));

        CardState cardState = (CardState) cardState();
        if (cardState.isDeckEmpty())
            cardState = cardState.withDeckRecreatedFromDiscards(rng);

        return new GameState(tickets, cardState, currentPlayerId(), playerState, lastPlayer());
    }

    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(playerState(playerId).tickets().size() == 0);

        //TODO: better way?
        PlayerId otherPlayer = playerId.next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(playerId, playerState(playerId).withAddedTickets(chosenTickets));
        playerState.put(otherPlayer, playerState(otherPlayer));

        return new GameState(tickets, cardState(), currentPlayerId(), playerState, lastPlayer());
    }

    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));
        //TODO: better way?
        PlayerId currentPlayer = currentPlayerId();
        PlayerId otherPlayer = currentPlayerId().next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(currentPlayer, currentPlayerState().withAddedTickets(chosenTickets));
        playerState.put(otherPlayer, playerState(otherPlayer));

        return new GameState(tickets.withoutTopCards(drawnTickets.size()), cardState(), currentPlayerId(), playerState, lastPlayer());
    }

    public GameState withDrawnFaceUpCard(int slot){
        Preconditions.checkArgument(canDrawCards());

        CardState cardState = (CardState) cardState();

        //TODO: better way?
        PlayerId currentPlayer = currentPlayerId();
        PlayerId otherPlayer = currentPlayerId().next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(currentPlayer, currentPlayerState().withAddedCard(cardState.faceUpCard(slot)));
        playerState.put(otherPlayer, playerState(otherPlayer));

        return new GameState(tickets, cardState.withDrawnFaceUpCard(slot), currentPlayerId(), playerState, lastPlayer());
    }

    public GameState withBlindlyDrawnCard(){
        Preconditions.checkArgument(canDrawCards());

        CardState cardState = (CardState) cardState();

        //TODO: better way?
        PlayerId currentPlayer = currentPlayerId();
        PlayerId otherPlayer = currentPlayerId().next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(currentPlayer, currentPlayerState().withAddedCard(cardState.topDeckCard()));
        playerState.put(otherPlayer, playerState(otherPlayer));

        return new GameState(tickets, cardState.withoutTopDeckCard(), currentPlayerId(), playerState, lastPlayer());
    }

    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){
        //TODO: better way?
        PlayerId currentPlayer = currentPlayerId();
        PlayerId otherPlayer = currentPlayerId().next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(currentPlayer, currentPlayerState().withClaimedRoute(route, cards));
        playerState.put(otherPlayer, playerState(otherPlayer));

        return new GameState(tickets, cardState(), currentPlayerId(), playerState, lastPlayer());
    }

    public boolean lastTurnBegins(){
        return lastPlayer() == null && currentPlayerState().carCount() <= 2;
    }

    public GameState forNextTurn(){
        //TODO: better way?
        PlayerId currentPlayer = currentPlayerId();
        PlayerId otherPlayer = currentPlayerId().next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(currentPlayer, currentPlayerState());
        playerState.put(otherPlayer, playerState(otherPlayer));

        PlayerId lastPlayer = null;
        if (lastTurnBegins())
            lastPlayer = currentPlayer;

        return new GameState(tickets, cardState(), currentPlayerId().next(), playerState, lastPlayer);
    }
}
