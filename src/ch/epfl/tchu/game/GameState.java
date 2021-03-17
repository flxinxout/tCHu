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
     * Retourne l'état initial d'une partie de tCHu dans laquelle la pioche des billets contient les billets donnés
     * et la pioche des cartes contient les cartes de Constants.ALL_CARDS, sans les 8 (2×4) du dessus, distribuées
     * aux joueurs ; ces pioches sont mélangées au moyen du générateur aléatoire donné, qui est aussi utilisé pour
     * choisir au hasard l'identité du premier joueur.
     *
     * @param tickets
     *          billets donnés
     * @param rng
     *          générateur aléatoire donné
     *
     * @return l'état initial d'une partie de tCHu.
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

    /**
     * Converti une map avec des PlayerState comme valeur en une map avec des PublicPlayerState en valeur.
     * @param nonPublicMap
     *              la map avec les PlayerState comme valeur
     * @return la nouvelle map avec la conversion effectuée.
     */
    private static Map<PlayerId, PublicPlayerState> makePublic(Map<PlayerId, PlayerState> nonPublicMap) {
        Map<PlayerId, PublicPlayerState> publicMap = new EnumMap(PlayerId.class);
        publicMap.putAll(nonPublicMap);

        return publicMap;
    }

    /**
     * Retourne l'état complet du joueur d'identité donnée.
     *
     * @param playerId
     *          le joueur donné
     * @return l'état complet du joueur d'identité donnée.
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return (PlayerState) super.playerState(playerId);
    }

    /**
     * Retourne l'état complet du joueur courant.
     * @return Retourne l'état complet du joueur courant.
     */
    @Override
    public PlayerState currentPlayerState() {
        return (PlayerState) super.currentPlayerState();
    }

    /**
     * Retourne les {@code count} billets du sommet de la pioche.
     * @param count
     *          le nombre de billets retournés du sommet de la pioche
     * @throws IllegalArgumentException
     *         si {@code count} n'est pas compris entre 0 et la taille de la pioche (inclus)
     * @return le nombre de billets retournés du sommet de la pioche.
     */
    public SortedBag<Ticket> topTickets(int count){
        Preconditions.checkArgument(count >= 0 && count <= ticketsCount());
        return tickets.topCards(count);
    }

    /**
     * Retourne un état identique au récepteur, mais sans les {@code count} billets du sommet de la pioche.
     * @param count
     *          le nombre de cartes enlevées du sommet de la pioche
     * @throws IllegalArgumentException
     *          si {@code count} n'est pas compris entre 0 et la taille de la pioche (inclus)
     * @return un état identique au récepteur, mais sans les {@code count} billets du sommet de la pioche.
     */
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

    /**
     * Retourne la carte au sommet de la pioche.
     * @throws IllegalArgumentException
     *              si la pioche est vide
     * @return la carte au sommet de la pioche.
     */
    public Card topCard() {
        Preconditions.checkArgument(!cardState().isDeckEmpty());
        //TODO: cast? or constructor?
        CardState cardState = (CardState) cardState();
        return cardState.topDeckCard();
    }

    /**
     * Retourne un état identique au récepteur mais sans la carte au sommet de la pioche.
     * @throws IllegalArgumentException
     *              si la pioche est vide
     * @return un état identique au récepteur mais sans la carte au sommet de la pioche.
     */
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

    /**
     * Retourne un état identique au récepteur mais avec les cartes données {@code discardedCards} ajoutées à la défausse.
     * @param discardedCards
     *                  les cartes ajoutées à la défausse
     * @return un état identique au récepteur mais avec les cartes données ajoutées à la défausse.
     */
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

    /**
     * Retourne un état identique au récepteur sauf si la pioche de cartes est vide, auquel cas elle est recréée
     * à partir de la défausse, mélangée au moyen du générateur aléatoire donné {@code rng}.
     * @param rng
     *          le générateur aléatoire pour le mélange des cartes.
     * @return un état identique au récepteur sauf si la pioche de cartes est vide, auquel cas elle est recréée
     * à partir de la défausse, mélangée au moyen du générateur aléatoire donné {@code rng}.
     */
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

    /**
     * Retourne un état identique au récepteur mais dans lequel les billets donnés {@code chosenTickets} ont été
     * ajoutés à la main du joueur donné {@code playerId}.
     * @param playerId
     *              le joueur auquel on lui a ajouté des billets
     * @param chosenTickets
     *              les billets ajoutés au joueur
     * @throws IllegalArgumentException
     *              si le joueur en question possède déjà au moins un billet
     *
     * @implNote cette méthode ne doit pas modifier la pioche de billets ! En effet, les 5 billets distribués
     * initialement aux joueurs auront déjà été extraits préalablement de la pioche au moyen de la méthode
     * withoutTopTickets, et le seul but de withInitiallyChosenTickets est de modifier l'état du joueur pour y
     * stocker le sous-ensemble de ces 5 billets qu'il a choisi de garder.
     *
     * @see GameState#withoutTopTickets(int)
     * @see GameState#withInitiallyChosenTickets(PlayerId, SortedBag)
     *
     * @return un état identique au récepteur mais dans lequel les billets donnés {@code chosenTickets} ont été
     * ajoutés à la main du joueur donné {@code playerId}.
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets){
        Preconditions.checkArgument(playerState(playerId).tickets().size() == 0);

        //TODO: better way?
        PlayerId otherPlayer = playerId.next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(playerId, playerState(playerId).withAddedTickets(chosenTickets));
        playerState.put(otherPlayer, playerState(otherPlayer));

        return new GameState(tickets, cardState(), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets {@code drawnTickets}
     * du sommet de la pioche, et choisi de garder ceux contenus dans {@code chosenTicket}.
     * @param drawnTickets
     *                  les billets tirés par le joueur
     * @param chosenTickets
     *                  les billets gardés par le joueur
     * @throws IllegalArgumentException
     *                  si l'ensemble des billets gardés n'est pas inclus dans celui des billets tirés
     * @return un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets {@code drawnTickets}
     * du sommet de la pioche, et choisi de garder ceux contenus dans {@code chosenTicket}.
     */
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

    /**
     * Retourne un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné {@code slot}
     * a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche.
     * @param slot
     *          l'emplacement de la carte qui a été placée dans la main du joueur courant
     * @throws IllegalArgumentException
     *          s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     *
     * @see GameState#canDrawCards()
     *
     * @return un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement donné {@code slot}
     * a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche.
     */
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

    /**
     * Retourne un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée
     * dans la main du joueur courant.
     * @throws IllegalArgumentException
     *          s'il n'est pas possible de tirer des cartes, c-à-d si canDrawCards retourne faux
     * @see GameState#canDrawCards()
     *
     * @return  un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée dans
     * la main du joueur courant
     */
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

    /**
     * Retourne un état identique au récepteur mais dans lequel le joueur courant s'est emparé de la route {@code route}
     * donnée au moyen des cartes données {@code cards}.
     * @param route
     *          la route dont le joueur s'est emparé
     * @param cards
     *          les cartes utilisées pour s'emparer de la route
     * @return n état identique au récepteur mais dans lequel le joueur courant s'est emparé de la route {@code route}
     * donnée au moyen des cartes données {@code cards}.
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards){
        //TODO: better way?
        PlayerId currentPlayer = currentPlayerId();
        PlayerId otherPlayer = currentPlayerId().next();

        Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        playerState.put(currentPlayer, currentPlayerState().withClaimedRoute(route, cards));
        playerState.put(otherPlayer, playerState(otherPlayer));

        return new GameState(tickets, cardState(), currentPlayerId(), playerState, lastPlayer());
    }

    /**
     * Retourne vrai ssi le dernier tour commence, c-à-d si l'identité du dernier joueur est actuellement inconnue
     * mais que le joueur courant n'a plus que deux wagons ou moins ; cette méthode doit être appelée uniquement à
     * la fin du tour d'un joueur.
     * @return vrai/faux en fonction des arguments ci-dessus.
     */
    public boolean lastTurnBegins(){
        return lastPlayer() == null && currentPlayerState().carCount() <= 2;
    }

    /**
     * Termine le tour du joueur courant, c-à-d retourne un état identique au récepteur si ce n'est que le joueur
     * courant est celui qui suit le joueur courant actuel ; de plus, si lastTurnBegins retourne vrai, le joueur
     * courant actuel devient le dernier joueur.
     * 
     * @return un état identique au récepteur si ce n'est que le joueur
     * courant est celui qui suit le joueur courant actuel ; de plus, si lastTurnBegins retourne vrai, le joueur
     * courant actuel devient le dernier joueur.
     */
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
