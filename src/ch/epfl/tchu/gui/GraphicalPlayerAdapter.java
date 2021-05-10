package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;
import static ch.epfl.tchu.gui.ActionHandlers.*;

public final class GraphicalPlayerAdapter implements Player {

    private GraphicalPlayer graphicalPlayer;
    private final BlockingQueue<Ticket> ticketsBQ;
    private final BlockingQueue<TurnKind> turnKindsBQ;
    private final BlockingQueue<Card> cardsBQ;
    private final BlockingQueue<Integer> integersBQ;
    private final BlockingQueue<Route> routesBQ;

    public GraphicalPlayerAdapter() {
        this.ticketsBQ = new ArrayBlockingQueue<>(1);
        this.turnKindsBQ = new ArrayBlockingQueue<>(1);
        this.cardsBQ = new ArrayBlockingQueue<>(1);
        this.integersBQ = new ArrayBlockingQueue<>(1);
        this.routesBQ = new ArrayBlockingQueue<>(1);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        //TODO: on nous parle d'apapter mais ça veut dire avoir une instance de Player nan ?
        runLater(() -> this.graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
    }

    @Override
    public void receiveInfo(String info) {
        runLater(() -> graphicalPlayer.receiveInfo(info));
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        runLater(() -> graphicalPlayer.setState(newState, ownState));
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        //TODO: plutot confiant de ça mais à voir
        runLater(() -> graphicalPlayer.chooseTickets(tickets, (ticketChoice) -> ticketChoice.forEach(ticketsBQ::add)));
        try { ticketsBQ.take(); } catch (InterruptedException e) { throw new Error(); }
    }

    // D'après un post piazza c'est pour réutiliser du code pour les tickets
    private SortedBag<Ticket> takeTicketsFromQueue() {

    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        //TODO: alors là aucune idée
        try {
            return SortedBag.of(ticketsBQ.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TurnKind nextTurn() {
        runLater(() -> {
            graphicalPlayer.startTurn(() -> {
                turnKindsBQ.add(TurnKind.DRAW_TICKETS);
            }, (slot) -> {
                integersBQ.add(slot);
                turnKindsBQ.add(TurnKind.DRAW_CARDS);
            }, (route, cards) -> {
                cards.forEach(cardsBQ::add);
                routesBQ.add(route);
                turnKindsBQ.add(TurnKind.CLAIM_ROUTE);
            });
        });

        //TODO: maudit return je comprends pas on retourne null sinon ??
        try { turnKindsBQ.take(); } catch (InterruptedException e) { throw new Error(); }
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        runLater(() -> {

        });
        return null;
    }

    @Override
    public int drawSlot() {
        return 0;
    }

    @Override
    public Route claimedRoute() {
        return null;
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        return null;
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        return null;
    }
}
