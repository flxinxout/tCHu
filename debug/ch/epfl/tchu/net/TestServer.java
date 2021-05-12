package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.gui.Info;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;

public class TestServer {

    private static final SortedBag<Card> cards = SortedBag.of(3, Card.LOCOMOTIVE, 2, Card.BLUE);
    private static final List<SortedBag<Card>> cardsOptions = List.of(SortedBag.of(Card.YELLOW), cards);
    private static final SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets().subList(5, 10));
    private static final List<Route> routes = List.of();
    private static final Random rng = new Random();

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server!");
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {
            Player playerProxy = new RemotePlayerProxy(socket);
            var playerNames = Map.of(PLAYER_1, "Ada",
                    PLAYER_2, "Charles");
            playerProxy.initPlayers(PLAYER_1, playerNames);
            playerProxy.receiveInfo(new Info(PLAYER_1.name()).canPlay());
            playerProxy.updateState(GameState.initial(tickets, rng), new PlayerState(tickets, cards, routes));
            playerProxy.setInitialTicketChoice(tickets);
            System.out.printf("chosen initial tickets: %s\n", playerProxy.chooseInitialTickets());
            System.out.printf("turn kind: %s\n", playerProxy.nextTurn());
            System.out.printf("chosen in game tickets: %s\n", playerProxy.chooseTickets(tickets));
            System.out.printf("draw at slot: %s\n", playerProxy.drawSlot());
            System.out.printf("claimed route id: %s\n", playerProxy.claimedRoute().id());
            System.out.printf("initial claim cards: %s\n", playerProxy.initialClaimCards());
            System.out.printf("additional chosen cards: %s\n", playerProxy.chooseAdditionalCards(cardsOptions));
        }
        System.out.println("Server done!");
    }

}
