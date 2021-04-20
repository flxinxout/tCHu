package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.util.List;
import java.util.Map;

public class TestClient {

    private static SortedBag<Card> cards = SortedBag.of(3, Card.LOCOMOTIVE, 2, Card.BLUE);

    public static void main(String[] args) {
        System.out.println("Starting client!");
        RemotePlayerClient playerClient = new RemotePlayerClient(new TestPlayer(),
                        "localhost",
                        5108);
        playerClient.run();
        System.out.println("Client done!");
    }

    private final static class TestPlayer implements Player {

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> names) {
            System.out.printf("ownId: %s\n", ownId);
            System.out.printf("playerNames: %s\n", names);
        }

        @Override
        public void receiveInfo(String info) {
            System.out.printf("info: %s", info);
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            System.out.printf("new GameState last player: %s\n", newState.lastPlayer());
            System.out.printf("ownState cards: %s\n", ownState.cards());
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            System.out.printf("initial tickets: %s\n", tickets);
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            return SortedBag.of(ChMap.tickets().get(3));
        }

        @Override
        public TurnKind nextTurn() {
            return TurnKind.DRAW_CARDS;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            System.out.printf("tickets options: %s\n", options);
            return SortedBag.of(ChMap.tickets().get(3));
        }

        @Override
        public int drawSlot() {
            return 0;
        }

        @Override
        public Route claimedRoute() {
            return ChMap.routes().get(10);
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return cards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            System.out.printf("additional cards options: %s\n", options);
            return cards;
        }
    }

}
