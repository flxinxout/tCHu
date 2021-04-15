package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * L'état d'une partie de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class GameState extends PublicGameState {

    /**
     * Le nombre de wagons maximal à partir duquel la fin de partie est annoncée.
     */
    private static final int END_CAR_COUNT = 2;

    private final Deck<Ticket> tickets;
    private final Map<PlayerId, PlayerState> playerState;
    private final CardState cardState;

    /**
     * Construit l'état de partie de tCHu.
     */
    private GameState(Deck<Ticket> tickets, CardState cardState, PlayerId currentPlayerId,
                      Map<PlayerId, PlayerState> playerState, PlayerId lastPlayer) {
        super(tickets.size(), cardState, currentPlayerId, Map.copyOf(playerState), lastPlayer);

        this.playerState = Map.copyOf(playerState);
        this.cardState = Objects.requireNonNull(cardState);
        this.tickets = tickets;
    }

    /**
     * Retourne l'état initial d'une partie de tCHu dans laquelle la pioche des billets contient {@code tickets}
     * et la pioche des cartes contient toutes les cartes du jeu, sans les 8 (2×4) du dessus, distribuées
     * aux joueurs; ces pioches sont mélangées au moyen du générateur aléatoire {@code rng}
     * qui est aussi utilisé pour choisir au hasard l'identité du premier joueur.
     *
     * @param tickets les billets constituant la pioche
     * @param rng     le générateur aléatoire utilisé
     * @return l'état initial d'une partie de tCHu
     */
    public static GameState initial(SortedBag<Ticket> tickets, Random rng) {
        Deck<Ticket> ticketsDeck = Deck.of(tickets, rng);
        Deck<Card> cardsDeck = Deck.of(Constants.ALL_CARDS, rng);

        final Map<PlayerId, PlayerState> playerState = new EnumMap<>(PlayerId.class);
        for (PlayerId playerId : PlayerId.ALL) {
            playerState.put(playerId, PlayerState.initial(cardsDeck.topCards(Constants.INITIAL_CARDS_COUNT)));
            cardsDeck = cardsDeck.withoutTopCards(Constants.INITIAL_CARDS_COUNT);
        }

        final CardState cardState = CardState.of(cardsDeck);
        final PlayerId firstPlayer = PlayerId.ALL.get(rng.nextInt(PlayerId.COUNT));

        return new GameState(ticketsDeck, cardState, firstPlayer, playerState, null);
    }

    /**
     * Retourne l'état complet du joueur d'identité {@code playerId}.
     *
     * @param playerId l'identité donnée
     * @return l'état complet du joueur d'identité {@code playerId}
     */
    @Override
    public PlayerState playerState(PlayerId playerId) {
        return this.playerState.get(playerId);
    }

    /**
     * @return l'état complet du joueur courant
     */
    @Override
    public PlayerState currentPlayerState() {
        return this.playerState.get(currentPlayerId());
    }

    /**
     * Retourne les {@code count} billets du sommet de la pioche.
     *
     * @param count le nombre de billets à retourner du sommet de la pioche
     * @return les {@code count} billets du sommet de la pioche
     * @throws IllegalArgumentException si {@code count} n'est pas compris entre 0 et la taille de la pioche (inclus)
     */
    public SortedBag<Ticket> topTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= this.tickets.size());
        return this.tickets.topCards(count);
    }

    /**
     * Retourne un état identique au récepteur, mais sans les {@code count} billets du sommet de la pioche.
     *
     * @param count le nombre de billets enlevés du sommet de la pioche
     * @return un état identique au récepteur, mais sans les {@code count} billets du sommet de la pioche
     * @throws IllegalArgumentException si {@code count} n'est pas compris entre 0 et la taille de la pioche (inclus)
     */
    public GameState withoutTopTickets(int count) {
        Preconditions.checkArgument(count >= 0 && count <= this.tickets.size());
        return new GameState(this.tickets.withoutTopCards(count), this.cardState, currentPlayerId(), this.playerState, lastPlayer());
    }

    /**
     * Retourne la carte au sommet de la pioche.
     *
     * @return la carte au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topCard() {
        Preconditions.checkArgument(!this.cardState.isDeckEmpty());
        return this.cardState.topDeckCard();
    }

    /**
     * Retourne un état identique au récepteur mais sans la carte au sommet de la pioche.
     *
     * @return un état identique au récepteur mais sans la carte au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public GameState withoutTopCard() {
        Preconditions.checkArgument(!this.cardState.isDeckEmpty());
        return new GameState(this.tickets, this.cardState.withoutTopDeckCard(), currentPlayerId(), this.playerState, lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur mais avec les cartes {@code discardedCards} ajoutées à la défausse.
     *
     * @param discardedCards les cartes à ajouter à la défausse
     * @return un état identique au récepteur mais avec les cartes {@code discardedCards} ajoutées à la défausse.
     */
    public GameState withMoreDiscardedCards(SortedBag<Card> discardedCards) {
        return new GameState(this.tickets, this.cardState.withMoreDiscardedCards(discardedCards),
                currentPlayerId(), this.playerState, lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur sauf si la pioche de cartes est vide, auquel cas elle est recréée
     * à partir de la défausse, mélangée au moyen du générateur aléatoire {@code rng}.
     *
     * @param rng le générateur aléatoire utilisé
     * @return un état identique au récepteur sauf si la pioche de cartes est vide, auquel cas elle est recréée
     * à partir de la défausse
     */
    public GameState withCardsDeckRecreatedIfNeeded(Random rng) {
        return this.cardState.isDeckEmpty() ?
                new GameState(this.tickets, this.cardState.withDeckRecreatedFromDiscards(rng),
                        currentPlayerId(), this.playerState, lastPlayer()) :
                this;
    }

    /**
     * Retourne un état identique au récepteur mais dans lequel les billets {@code chosenTickets} ont été
     * ajoutés à la main du joueur d'identité {@code playerId}. Destinée à être appelée au début de partie uniquement.
     *
     * @param playerId      l'identité du joueur auquel les billets sont ajoutés
     * @param chosenTickets les billets à ajouter
     * @return un état identique au récepteur mais dans lequel les billets {@code chosenTickets} ont été
     * ajoutés à la main du joueur {@code playerId}
     * @throws IllegalArgumentException si le joueur d'identité {@code playerId} possède déjà au moins un billet
     */
    public GameState withInitiallyChosenTickets(PlayerId playerId, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(playerState(playerId).tickets().isEmpty());

        final Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(this.playerState);
        newPlayerState.put(playerId, playerState(playerId).withAddedTickets(chosenTickets));

        return new GameState(this.tickets, this.cardState, currentPlayerId(), newPlayerState, lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets
     * {@code drawnTickets} du sommet de la pioche, et choisi de garder ceux contenus dans {@code chosenTicket}.
     *
     * @param drawnTickets  les billets tirés par le joueur
     * @param chosenTickets les billets gardés par le joueur
     * @return un état identique au récepteur, mais dans lequel le joueur courant a tiré les billets
     * {@code drawnTickets} du sommet de la pioche, et choisi de garder ceux contenus dans {@code chosenTicket}
     * @throws IllegalArgumentException si {@code chosenTickets} n'est pas inclus dans {@code drawnTickets}
     */
    public GameState withChosenAdditionalTickets(SortedBag<Ticket> drawnTickets, SortedBag<Ticket> chosenTickets) {
        Preconditions.checkArgument(drawnTickets.contains(chosenTickets));

        final Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(this.playerState);
        newPlayerState.put(currentPlayerId(), currentPlayerState().withAddedTickets(chosenTickets));

        return new GameState(this.tickets.withoutTopCards(drawnTickets.size()),
                this.cardState, currentPlayerId(), newPlayerState, lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement {@code slot}
     * a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche.
     *
     * @param slot l'emplacement de la carte à remplacer
     * @return un état identique au récepteur si ce n'est que la carte face retournée à l'emplacement {@code slot}
     * a été placée dans la main du joueur courant, et remplacée par celle au sommet de la pioche
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes,
     *                                  c-à-d si {@code canDrawCards} retourne faux
     * @see GameState#canDrawCards()
     */
    public GameState withDrawnFaceUpCard(int slot) {
        Preconditions.checkArgument(canDrawCards());

        final Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(this.playerState);
        newPlayerState.put(currentPlayerId(), currentPlayerState().withAddedCard(this.cardState.faceUpCard(slot)));

        return new GameState(this.tickets, this.cardState.withDrawnFaceUpCard(slot),
                currentPlayerId(), newPlayerState, lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée
     * dans la main du joueur courant.
     *
     * @return un état identique au récepteur si ce n'est que la carte du sommet de la pioche a été placée dans
     * la main du joueur courant
     * @throws IllegalArgumentException s'il n'est pas possible de tirer des cartes,
     *                                  c-à-d si {@code canDrawCards} retourne faux
     * @see GameState#canDrawCards()
     */
    public GameState withBlindlyDrawnCard() {
        Preconditions.checkArgument(canDrawCards());

        final Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(this.playerState);
        newPlayerState.put(currentPlayerId(), currentPlayerState().withAddedCard(this.cardState.topDeckCard()));

        return new GameState(this.tickets, this.cardState.withoutTopDeckCard(), currentPlayerId(), newPlayerState, lastPlayer());
    }

    /**
     * Retourne un état identique au récepteur mais dans lequel le joueur courant
     * s'est emparé de la route {@code route} au moyen des cartes {@code cards}.
     * Les cartes utilisées sont ajoutées à la défausse.
     *
     * @param route la route dont le joueur s'est emparé
     * @param cards les cartes utilisées pour s'emparer de {@code route}
     * @return un état identique au récepteur mais dans lequel le joueur courant s'est emparé de la route {@code route}
     * au moyen des cartes {@code cards}
     */
    public GameState withClaimedRoute(Route route, SortedBag<Card> cards) {
        final Map<PlayerId, PlayerState> newPlayerState = new EnumMap<>(this.playerState);
        newPlayerState.put(currentPlayerId(), currentPlayerState().withClaimedRoute(route, cards));

        return new GameState(this.tickets, this.cardState.withMoreDiscardedCards(cards),
                currentPlayerId(), newPlayerState, lastPlayer());
    }

    /**
     * Retourne vrai ssi le dernier tour commence, c-à-d si l'identité du dernier joueur est actuellement inconnue
     * mais que le joueur courant n'a plus que deux wagons ou moins; cette méthode doit être appelée uniquement à
     * la fin du tour d'un joueur.
     *
     * @return vrai ssi le dernier tour commence
     */
    public boolean lastTurnBegins() {
        return lastPlayer() == null && currentPlayerState().carCount() <= END_CAR_COUNT;
    }

    /**
     * Termine le tour du joueur courant, c-à-d retourne un état identique au récepteur si ce n'est que le joueur
     * courant est celui qui suit le joueur courant actuel;
     * de plus, si {@code lastTurnBegins()} retourne vrai, le joueur courant actuel devient le dernier joueur.
     *
     * @return un état identique au récepteur si ce n'est que le joueur le tour des joueurs a été inversé
     * @see GameState#lastTurnBegins()
     */
    public GameState forNextTurn() {
        final PlayerId lastPlayer = lastTurnBegins() ? currentPlayerId() : lastPlayer();
        return new GameState(this.tickets, this.cardState, currentPlayerId().next(), this.playerState, lastPlayer);
    }
}
