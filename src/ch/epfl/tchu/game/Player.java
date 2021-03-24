package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

/**
 * Un joueur de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public interface Player {

    /**
     * Appelée au début de la partie pour communiquer au joueur sa propre identité {@code ownId}, ainsi que les noms
     * des différents joueurs, le sien inclus, qui se trouvent dans la liste des noms de player {@code playerNames}.
     *
     * @param ownId       l'identité du joueur
     * @param playerNames les noms des différents joueurs
     */
    void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);

    /**
     * Appelée chaque fois qu'une information doit être communiquée au joueur au cours de la partie; cette information
     * est donnée sous la forme d'une chaîne de caractères {@code info}, généralement produite par la classe Info.
     *
     * @param info l'information qui doit être communiquée
     * @see ch.epfl.tchu.gui.Info
     */
    void receiveInfo(String info);

    /**
     * Appelée chaque fois que l'état du jeu a changé, pour informer le joueur de la composante publique de ce nouvel
     * état, {@code newState}, ainsi que de son propre état, {@code ownState}.
     *
     * @param newState le nouvel état du joueur
     * @param ownState l'état du joueur
     */
    void updateState(PublicGameState newState, PlayerState ownState);

    /**
     * Appelée au début de la partie pour communiquer au joueur les cinq billets qui lui ont été distribués.
     *
     * @param tickets les billets distribués au joueur
     */
    void setInitialTicketChoice(SortedBag<Ticket> tickets);

    /**
     * Appelée au début de la partie pour demander au joueur lesquels des billets qu'on lui a distribué initialement
     * (via la méthode précédente) il garde.
     *
     * @return les billets que le joueur garde parmi ceux qu'on lui a donné initialement.
     * @see Player#setInitialTicketChoice(SortedBag)
     */
    SortedBag<Ticket> chooseInitialTickets();

    /**
     * Appelée au début du tour d'un joueur, pour savoir quel type d'action il désire effectuer durant ce tour.
     *
     * @return l'action que veut effectuer le joueur.
     */
    TurnKind nextTurn();

    /**
     * Appelée lorsque le joueur a décidé de tirer des billets supplémentaires en cours de partie, afin de lui
     * communiquer les billets tirés et de savoir lesquels il garde.
     *
     * @param options les billets tirés
     * @return les billets gardés
     */
    SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options);

    /**
     * Appelée lorsque le joueur a décidé de tirer des cartes wagon/locomotive, afin de savoir d'où il désire les
     * tirer: d'un des emplacements contenant une carte face visible — auquel cas la valeur retournée est comprise
     * entre 0 et 4 inclus —, ou de la pioche — auquel cas la valeur retournée vaut Constants.DECK_SLOT (c-à-d -1).
     *
     * @return entre 0 et 4 inclus:= si le joueur tire des cartes faces visibles / Constants.DECK_SLOT (-1) si c'est
     * dans la pioche
     */
    int drawSlot();

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir de quelle route
     * il s'agit.
     *
     * @return la route dont le joueur tente de s'emparer
     */
    Route claimedRoute();

    /**
     * Appelée lorsque le joueur a décidé de (tenter de) s'emparer d'une route, afin de savoir quelle(s) carte(s)
     * il désire initialement utiliser pour cela.
     *
     * @return les cartes que le joueur veut utiliser pour s'emparer d'une route
     */
    SortedBag<Card> initialClaimCards();

    /**
     * Appelée lorsque le joueur a décidé de tenter de s'emparer d'un tunnel et que des cartes additionnelles
     * sont nécessaires, afin de savoir quelle(s) carte(s) il désire utiliser pour cela, les possibilités lui
     * étant passées en argument; si le multiensemble retourné est vide, cela signifie que le joueur ne désire
     * pas (ou ne peut pas) choisir l'une de ces possibilités.
     *
     * @param options les possibilités de cartes pour s'emparer d'un tunnel
     * @return un multiensemble vide si le joueur ne désire pas (ou ne peut pas) choisir l'une de ces possibilités.
     */
    SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options);

    /**
     * Énumération qui représente les trois types d'actions qu'un joueur de tCHu peut effectuer durant un tour.
     */
    enum TurnKind {

        DRAW_TICKETS,
        DRAW_CARDS,
        CLAIM_ROUTE;

        /**
         * Liste contenant toutes les différentes actions d'un tour de jeu.
         */
        public static final List<TurnKind> ALL = List.of(values());
    }

}




















