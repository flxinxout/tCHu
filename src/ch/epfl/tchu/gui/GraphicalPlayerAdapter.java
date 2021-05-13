package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

/**
 * Adapte une instance de GraphicalPlayer en une valeur de type Player.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 * @implNote Toutes les méthodes de cette classe sont destinées à être exécutées sur le fil d'exécution de JavaFX.
 */
public final class GraphicalPlayerAdapter implements Player {

    private final BlockingQueue<SortedBag<Ticket>> ticketsBQ;
    private final BlockingQueue<Integer> cardSlotBQ;
    private final BlockingQueue<SortedBag<Card>> cardsBQ;
    private final BlockingQueue<Route> routeBQ;

    private GraphicalPlayer graphicalPlayer;

    /**
     * Construit les différentes files bloquantes utilisées pour bloquer le fil d'exécution de JavaFX
     */
    public GraphicalPlayerAdapter() {
        this.ticketsBQ = new ArrayBlockingQueue<>(1);
        this.cardSlotBQ = new ArrayBlockingQueue<>(1);
        this.cardsBQ = new ArrayBlockingQueue<>(1);
        this.routeBQ = new ArrayBlockingQueue<>(1);
    }

    /**
     * Retourne le premier élément de la file bloquante donnée, qui en est retiré.
     *
     * @param queue la file bloquante
     * @param <T>   le type d'élément que contient la file bloquante
     * @return le premier élément de la file bloquante donnée
     */
    private static <T> T takeFromQueue(BlockingQueue<T> queue) {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    /**
     * Construit l'instance du joueur graphique GraphicalPlayer qu'elle adapte.
     *
     * @param ownId       l'identité du joueur
     * @param playerNames les noms des différents joueurs
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    /**
     * Ajoute l'information donnée aux messages d'information de cette interface graphique. S'il y a déjà 5 messages
     * affichés, le plus ancien est remplacé par celui donné.
     *
     * @param info l'information qui doit être communiquée
     * @see GraphicalPlayer#receiveInfo(String)
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    /**
     * Met à jour la totalité des propriétés de l'état de jeu lié à cette interface graphique
     * en fonction des deux états donnés.
     *
     * @param newState le nouvel état de jeu
     * @param ownState le nouvel état du joueur associé à cette interface graphique
     * @see GraphicalPlayer#setState(PublicGameState, PlayerState)
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix entre les billets donnés. Une fois celui-ci confirmé,
     * la fenêtre de sélection est fermée.
     * À utiliser en complément avec {@link GraphicalPlayerAdapter#chooseInitialTickets()}.
     *
     * @param tickets les billets distribués au joueur
     * @see GraphicalPlayer#chooseTickets(SortedBag, ActionHandlers.ChooseTicketsHandler)
     * @see GraphicalPlayerAdapter#chooseInitialTickets()
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketsBQ::add));
    }

    /**
     * Retourne le choix initial des billets du joueur parmi ceux proposés dans
     * {@link GraphicalPlayerAdapter#setInitialTicketChoice(SortedBag)}.
     * Bloque l'interface graphique le temps qu'il fasse ce choix.
     *
     * @return le choix initial des billets du joueur
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return takeFromQueue(ticketsBQ);
    }

    /**
     * Autorise le joueur à effectuer des actions durant son tour. Bloque l'interface graphique tant que son choix n'a
     * pas été fait.
     *
     * @return le type de tour que le joueur désire effectuer
     * @see GraphicalPlayer#startTurn(ActionHandlers.DrawTicketsHandler, ActionHandlers.DrawCardHandler, ActionHandlers.ClaimRouteHandler)
     */
    @Override
    public TurnKind nextTurn() {
        BlockingQueue<TurnKind> turnKindBQ = new ArrayBlockingQueue<>(1);

        runLater(() -> graphicalPlayer.startTurn(() -> turnKindBQ.add(TurnKind.DRAW_TICKETS),
                (slot) -> {
                    cardSlotBQ.add(slot);
                    turnKindBQ.add(TurnKind.DRAW_CARDS);
                },
                (route, cards) -> {
                    routeBQ.add(route);
                    cardsBQ.add(cards);
                    turnKindBQ.add(TurnKind.CLAIM_ROUTE);
                }));

        return takeFromQueue(turnKindBQ);
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix entre les billets donnés; une fois celui-ci confirmé,
     * le retourne et ferme la fenêtre.
     *
     * @param options les billets tirés de la pioche
     * @return les billets gardés par le joueur
     * @see GraphicalPlayerAdapter#setInitialTicketChoice(SortedBag)
     * @see GraphicalPlayerAdapter#chooseInitialTickets()
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        runLater(() -> graphicalPlayer.chooseTickets(options, ticketsBQ::add));
        return takeFromQueue(ticketsBQ);
    }

    /**
     * Si le joueur tire sa première carte du tour, retourne son emplacement. Sinon, autorise le joueur
     * à en tirer une deuxième et bloque l'interface graphique en attendant son choix, qui est retourné.
     *
     * @return l'emplacement de la carte tirée par le joueur
     */
    @Override
    public int drawSlot() {
        if (!cardSlotBQ.isEmpty())
            return takeFromQueue(cardSlotBQ);

        runLater(() -> graphicalPlayer.drawCard(cardSlotBQ::add));
        return takeFromQueue(cardSlotBQ);
    }

    /**
     * Retourne la route dont le joueur tente de s'emparer.
     *
     * @return la route dont le joueur tente de s'emparer
     */
    @Override
    public Route claimedRoute() {
        return takeFromQueue(routeBQ);
    }

    /**
     * Bloque l'interface graphique tant que le joueur n'a choisi aucune cartes initiales pour s'emparer d'une route,
     * les retourne une fois qu'il les a choisies.
     *
     * @return les cartes utilisées initialement par le joueur pour s'emparer d'une route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return takeFromQueue(cardsBQ);
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix sur les cartes additionnelles qu'il peut utiliser pour
     * s'emparer d'un tunnel; l'interface graphique est bloquée jusqu'à ce que celui-ci ait été confirmé.
     * Une fois le choix effectué, cette méthode le retourne et la fenêtre de choix est fermée.
     *
     * @param options les possibilités de cartes pour s'emparer d'un tunnel
     * @return le multi-ensemble de cartes utilisé pour s'emparer d'un tunnel
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        assert cardsBQ.isEmpty();
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, cardsBQ::add));
        return takeFromQueue(cardsBQ);
    }
}
