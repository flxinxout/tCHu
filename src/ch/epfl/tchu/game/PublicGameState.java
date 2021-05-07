package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * La partie publique de l'état d'une partie de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class PublicGameState {

    private final int ticketsCount;
    private final PublicCardState cardState;
    private final PlayerId currentPlayerId;
    private final Map<PlayerId, PublicPlayerState> playerState;
    private final PlayerId lastPlayer;

    /**
     * Construit la partie publique de l'état d'une partie de tCHu dans laquelle la pioche de billets a une taille
     * de {@code ticketsCount}, l'état public des cartes wagon/locomotive est {@code cardState}, le joueur courant
     * est {@code currentPlayerId}, l'état public des joueurs est contenu dans {@code playerState}, et l'identité
     * du dernier joueur est {@code lastPlayer} (qui peut être {@code null} si cette identité est encore inconnue)
     *
     * @param ticketsCount    la taille de la pioche
     * @param cardState       l'état public des cartes wagon/locomotive (non {@code null})
     * @param currentPlayerId le joueur courant (non {@code null})
     * @param playerState     l'état public des joueurs (non {@code null})
     * @param lastPlayer      l'identité du dernier joueur (peut être {@code null})
     * @throws IllegalArgumentException si {@code ticketsCount} < 0 ou
     *                                  si {@code playerState} ne contient pas exactement deux paires clef/valeur
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                           Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == 2);

        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(playerState);
        this.lastPlayer = lastPlayer;
        this.ticketsCount = ticketsCount;
    }

    /**
     * Retourne vrai ssi il est possible de tirer des cartes, c-à-d si la pioche
     * et la défausse contiennent entre elles au moins 5 cartes.
     *
     * @return ssi il est possible de tirer des cartes
     */
    public boolean canDrawCards() {
        return this.cardState.deckSize() + this.cardState.discardsSize() >= PlayerId.COUNT + Constants.ADDITIONAL_TUNNEL_CARDS;
    }

    /**
     * Retourne la partie publique de l'état du joueur d'identité {@code playerId}.
     *
     * @param playerId l'identité de joueur donnée
     * @return la partie publique de l'état du joueur d'identité {@code playerId}
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return this.playerState.get(playerId);
    }

    /**
     * Retourne la partie publique de l'état du joueur courant.
     *
     * @return la partie publique de l'état du joueur courant
     */
    public PublicPlayerState currentPlayerState() {
        return this.playerState.get(this.currentPlayerId);
    }

    /**
     * Retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé.
     *
     * @return la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     */
    public List<Route> claimedRoutes() {
        List<Route> routes = new ArrayList<>();
        for (PublicPlayerState ps : this.playerState.values()) {
            routes.addAll(ps.routes());
        }

        return List.copyOf(routes);
    }

    /**
     * Retourne l'identité du dernier joueur ({@code null} si elle n'est pas encore connue).
     *
     * @return l'identité du dernier joueur
     */
    public PlayerId lastPlayer() {
        return this.lastPlayer;
    }

    /**
     * Retourne l'identité du joueur actuel.
     *
     * @return l'identité du joueur actuel
     */
    public PlayerId currentPlayerId() {
        return this.currentPlayerId;
    }

    /**
     * Retourne la taille de la pioche de billets.
     *
     * @return la taille de la pioche de billets
     */
    public int ticketsCount() {
        return this.ticketsCount;
    }

    /**
     * Retourne ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide.
     *
     * @return ssi il est possible de tirer des billets
     */
    public boolean canDrawTickets() {
        return this.ticketsCount > 0;
    }

    /**
     * Retourne la partie publique de l'état des cartes wagon/locomotive.
     *
     * @return la partie publique de l'état des cartes wagon/locomotive
     */
    public PublicCardState cardState() {
        return this.cardState;
    }
}