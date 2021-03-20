package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Représente la partie publique de l'état d'une partie de tCHu.
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
     * Constructeur d'une partie publique de l'état d'une partie de tCHu
     *
     * @param ticketsCount
     *              la taille de la pioche
     * @param cardState
     *              l'état public des cartes wagon/locomotive (non null)
     * @param currentPlayerId
     *              le joueur courant (non null)
     * @param playerState
     *              l'état public des joueurs (non null)
     * @param lastPlayer
     *              l'identité du dernier joueur (peut être null)
     * @throws IllegalArgumentException
     *              si la taille de la pioche de billets est strictement négative ou
     *              si playerState ne contient pas exactement deux paires clef/valeur
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                           Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == 2);

        this.cardState = Objects.requireNonNull(cardState);
        this.currentPlayerId = Objects.requireNonNull(currentPlayerId);
        this.playerState = Map.copyOf(playerState);
        this.ticketsCount = ticketsCount;
        this.lastPlayer = lastPlayer;
    }

    /**
     * Retourne la taille de la pioche de billets.
     * @return la taille de la pioche de billets
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Retourne vrai ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide.
     * @return vrai ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide
     */
    public boolean canDrawTickets() {
        return ticketsCount != 0;
    }

    /**
     * Retourne la partie publique de l'état des cartes wagon/locomotive.
     * @return la partie publique de l'état des cartes wagon/locomotive
     */
    public PublicCardState cardState() {
        return cardState;
    }

    /**
     * Retourne vrai ssi il est possible de tirer des cartes, c-à-d si la pioche
     * et la défausse contiennent entre elles au moins 5 cartes.
     * @return ssi il est possible de tirer des cartes
     */
    public boolean canDrawCards() {
        return cardState.deckSize() + cardState.discardsSize() >= 5;
    }

    /**
     * Retourne l'identité du joueur actuel
     * @return l'identité du joueur actuel
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Retourne la partie publique de l'état du joueur d'identité donnée.
     *
     * @param playerId
     *              le joueur donné
     * @return la partie publique de l'état du joueur d'identité donnée
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Retourne la partie publique de l'état du joueur courant.
     * @return la partie publique de l'état du joueur courant
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * Retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé.
     * @return la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     */
    public List<Route> claimedRoutes() {
        List<Route> routes = new ArrayList<>();
        for(PublicPlayerState ps : playerState.values()) {
            routes.addAll(ps.routes());
        }

        return List.copyOf(routes);
    }

    /**
     * Retourne l'identité du dernier joueur, ou null si elle n'est pas encore connue car le dernier tour n'a pas commencé.
     * @return l'identité du dernier joueur, ou null si elle n'est pas encore connue
     */
    public PlayerId lastPlayer() {
        return lastPlayer;
    }
}