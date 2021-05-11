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
     * Appelle la méthode du même nom du joueur graphique.
     *
     * @param info l'information qui doit être communiquée
     */
    @Override
    public void receiveInfo(String info) {
        runLater(() -> {
            //TODO: null check obligatoire?
            if (graphicalPlayer != null)
                graphicalPlayer.receiveInfo(info);
        });
    }

    /**
     * Appelle la méthode setState du joueur graphique.
     *
     * @param newState le nouvel état de la partie
     * @param ownState l'état du joueur
     * @see GraphicalPlayer#setState(PublicGameState, PlayerState)
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> {
            //TODO: null check obligatoire?
            if (graphicalPlayer != null)
                graphicalPlayer.setState(newState, ownState);
        });
    }

    /**
     * Appelle la méthode chooseTickets du joueur graphique, pour lui demander de choisir ses billets initiaux,
     * en lui passant un gestionnaire de choix qui stocke le choix du joueur dans une file bloquante.
     *
     * @param tickets les billets distribués au joueur
     * @see GraphicalPlayer#chooseTickets(SortedBag, ActionHandlers.ChooseTicketsHandler)
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        runLater(() -> {
            //TODO: null check obligatoire?
            if (graphicalPlayer != null)
                graphicalPlayer.chooseTickets(tickets, ticketsBQ::add);
        });
    }

    /**
     * Bloque en attendant que la file des billets contienne une valeur, puis la retourne.
     *
     * @return la valeur contenue dans la file des billets.
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        return takeFromQueue(ticketsBQ);
    }

    /**
     * Appelle la méthode startTurn du joueur graphique, en lui passant des gestionnaires d'action qui placent le type
     * de tour choisi, de même que les éventuels «arguments» de l'action— p.ex. la route dont le joueur désire
     * s'emparer — dans des files bloquantes, puis bloque en attendant qu'une valeur soit placée dans la file contenant
     * le type de tour, qu'elle retire et retourne.
     *
     * @return la valeur stockée dans la file de type d'action des joueurs
     * @see GraphicalPlayer#startTurn(ActionHandlers.DrawTicketsHandler, ActionHandlers.DrawCardHandler, ActionHandlers.ClaimRouteHandler)
     */
    @Override
    public TurnKind nextTurn() {
        BlockingQueue<TurnKind> turnKindBQ = new ArrayBlockingQueue<>(1);

        runLater(() -> graphicalPlayer.startTurn(() -> turnKindBQ.add(TurnKind.DRAW_TICKETS),
                (slot) -> {
                    turnKindBQ.add(TurnKind.DRAW_CARDS);
                    cardSlotBQ.add(slot);
                },
                (route, cards) -> {
                    routeBQ.add(route);
                    cardsBQ.add(cards);
                    turnKindBQ.add(TurnKind.CLAIM_ROUTE);
                }));

        return takeFromQueue(turnKindBQ);
    }

    /**
     * Enchaîne les actions effectuées par setInitialTicketChoice et chooseInitialTickets.
     *
     * @param options les billets tirés
     * @return les billets choisit
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        runLater(() -> graphicalPlayer.chooseTickets(options, ticketsBQ::add));
        return takeFromQueue(ticketsBQ);
    }

    /**
     * Teste (sans bloquer!) si la file contenant les emplacements des cartes contient une valeur; si c'est le cas,
     * cela signifie que drawSlot est appelée pour la première fois du tour, et que le gestionnaire installé par
     * nextTurn a placé l'emplacement de la première carte tirée dans cette file, qu'il suffit donc de retourner;
     * sinon, cela signifie que drawSlot est appelée pour la seconde fois du tour, afin que le joueur tire sa seconde
     * carte, et il faut donc appeler, sur le fil JavaFX, la méthode drawCard du joueur graphique, avant de bloquer
     * en attendant que le gestionnaire qu'on lui passe place l'emplacement de la carte tirée dans la file,
     * qui est alors extrait et retourné.
     *
     * @return la valeur de la file relative aux entiers si celle-ci en possède une, sinon elle attend d'en avoir une.
     */
    @Override
    public int drawSlot() {
        if (!cardSlotBQ.isEmpty())
            return takeFromQueue(cardSlotBQ);

        runLater(() -> graphicalPlayer.drawCard(cardSlotBQ::add));
        return takeFromQueue(cardSlotBQ);
    }

    /**
     * Extrait et retourne le premier élément de la file contenant les routes, qui y aura été placé par le
     * gestionnaire passé à startTurn par nextTurn.
     *
     * @return le premier élément de la contenant les routes
     */
    @Override
    public Route claimedRoute() {
        return takeFromQueue(routeBQ);
    }

    /**
     * Similaire à claimedRoute mais utilise la file contenant les multiensembles de cartes.
     *
     * @return
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        return takeFromQueue(cardsBQ);
    }

    /**
     * Appelle la méthode du même nom du joueur graphique puis bloque en attendant qu'un élément soit placé
     * dans la file contenant les multiensembles de cartes, qu'elle retourne.
     *
     * @param options les possibilités de cartes pour s'emparer du tunnel
     * @return le sortedbag contenu dans la file des multiensembles de cartes
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        assert cardsBQ.isEmpty();
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, cardsBQ::add));
        return takeFromQueue(cardsBQ);
    }
}
