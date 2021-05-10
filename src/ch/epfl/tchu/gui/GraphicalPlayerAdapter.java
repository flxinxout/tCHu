package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static javafx.application.Platform.runLater;

public final class GraphicalPlayerAdapter implements Player {

    private final BlockingQueue<SortedBag<Ticket>> ticketsBQ;
    private final BlockingQueue<Integer> cardSlotBQ;
    private final BlockingQueue<SortedBag<Card>> cardsBQ;
    private final BlockingQueue<Route> routeBQ;

    private GraphicalPlayer graphicalPlayer;

    public GraphicalPlayerAdapter() {
        this.ticketsBQ = new ArrayBlockingQueue<>(1);
        this.cardSlotBQ = new ArrayBlockingQueue<>(1);
        this.cardsBQ = new ArrayBlockingQueue<>(1);
        this.routeBQ = new ArrayBlockingQueue<>(1);
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        runLater(() -> graphicalPlayer = new GraphicalPlayer(ownId, playerNames));
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
        runLater(() -> graphicalPlayer.chooseTickets(tickets, ticketsBQ::add));
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        try {
            return ticketsBQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

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
        try {
            return turnKindBQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        runLater(() -> graphicalPlayer.chooseTickets(options, ticketsBQ::add));
        try {
            return ticketsBQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public int drawSlot() {
        if(!cardSlotBQ.isEmpty()) {
            try {
                return cardSlotBQ.take();
            } catch (InterruptedException e) {
                throw new Error();
            }
        }

        runLater(() -> graphicalPlayer.drawCard(cardSlotBQ::add));
        try {
            return cardSlotBQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public Route claimedRoute() {
        try {
            return routeBQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        try {
            return cardsBQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        runLater(() -> graphicalPlayer.chooseAdditionalCards(options, cardsBQ::add));
        try {
            return cardsBQ.take();
        } catch (InterruptedException e) {
            throw new Error();
        }
    }
}
