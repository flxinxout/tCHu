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
     * @param ticketsCount
     *              la taille de la pioche
     * @param cardState
     *              l'état public des cartes wagon/locomotive
     * @param currentPlayerId
     *              le joueur courant
     * @param playerState
     *              l'état public des joueurs
     * @param lastPlayer
     *              l'identité du dernier joueur
     */
    public PublicGameState(int ticketsCount, PublicCardState cardState, PlayerId currentPlayerId,
                           Map<PlayerId, PublicPlayerState> playerState, PlayerId lastPlayer) {
        Preconditions.checkArgument(ticketsCount >= 0 && playerState.size() == 2);
        Objects.requireNonNull(currentPlayerId);
        Objects.requireNonNull(cardState);

        this.ticketsCount = ticketsCount;
        this.cardState = cardState;
        this.currentPlayerId = currentPlayerId;
        this.playerState = playerState;
        this.lastPlayer = lastPlayer;
    }

    /**
     * Retourne la taille de la pioche de billets
     * @return la taille de la pioche de billets
     */
    public int ticketsCount() {
        return ticketsCount;
    }

    /**
     * Retourne vrai ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide
     * @return vrai ssi il est possible de tirer des billets, c-à-d si la pioche n'est pas vide
     */
    public boolean canDrawTickets() {
        return ticketsCount != 0;
    }

    /**
     * Retourne la partie publique de l'état des cartes wagon/locomotive
     * @return la partie publique de l'état des cartes wagon/locomotive
     */
    public PublicCardState cardState() {
        //TODO faut copier ou pas (pas une classe final mais immuable) ?! Je crois vu l'immuabilité ? (jsuis une merde pour savoir ça mdrrrr)
        return new PublicCardState(cardState.faceUpCards(), cardState.deckSize(), cardState.discardsSize());
    }

    /**
     * Retourne vrai ssi il est possible de tirer des cartes, c-à-d si la pioche
     * et la défausse contiennent entre elles au moins 5 cartes
     * @return la valeur de la condition au-dessus
     */
    public boolean canDrawCards() {
        return ticketsCount + cardState.discardsSize() >= 5;
    }

    /**
     * Retourne l'identité du joueur actuel
     * @return l'identité du joueur actuel
     */
    public PlayerId currentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Retourne la partie publique de l'état du joueur d'identité donnée
     * @param playerId
     *              le joueur donné
     * @return la partie publique de l'état du joueur d'identité donnée
     */
    public PublicPlayerState playerState(PlayerId playerId) {
        return playerState.get(playerId);
    }

    /**
     * Retourne la partie publique de l'état du joueur courant
     * @return la partie publique de l'état du joueur courant
     */
    public PublicPlayerState currentPlayerState() {
        return playerState.get(currentPlayerId);
    }

    /**
     * Retourne la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     * @return la totalité des routes dont l'un ou l'autre des joueurs s'est emparé
     */
    public List<Route> claimedRoutes() {
        List<Route> routes = new ArrayList<>();
        //TODO Je pense à ça mais maybe c'est autre chose mais en tout cas pour fore each une map c'est comme ça qu'on fait en java
        for(Map.Entry<PlayerId, PublicPlayerState> playerStateEntry : playerState.entrySet()) {
            for(Route route : playerStateEntry.getValue().routes()) {
                routes.add(route);
            }
        }
        return routes;
    }

    /**
     * Retourne l'identité du dernier joueur, ou null si elle n'est pas encore connue car le dernier tour n'a pas commencé
     * @return l'identité du dernier joueur, ou null si elle n'est pas encore connue car le dernier tour n'a pas commencé
     */
    public PlayerId lastPlayer() {
        return lastPlayer != null ? lastPlayer : null;
    }

}