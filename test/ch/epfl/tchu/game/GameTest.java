package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class GameTest {

    private static final class TestPlayer implements Player {
        private static final int TURN_LIMIT = 1000;

        private final Random rng;
        // Toutes les routes de la carte
        private final List<Route> allRoutes;

        public int turnCounter;
        private PlayerState ownState;
        private PublicGameState gameState;

        // Lorsque nextTurn retourne CLAIM_ROUTE
        private Route routeToClaim;
        private SortedBag<Card> initialClaimCards;

        private SortedBag<Ticket> initialTickets;
        public SortedBag<Ticket> tickets;
        public int receiveInfoNb = 0;
        public int updateStateNb = 0;

        public TestPlayer(long randomSeed, List<Route> allRoutes) {
            this.rng = new Random(randomSeed);
            this.allRoutes = List.copyOf(allRoutes);
            this.turnCounter = 0;

            this.initialTickets = SortedBag.of();
        }

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        }

        @Override
        public void receiveInfo(String info) {
            receiveInfoNb++;
        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {
            this.gameState = newState;
            this.ownState = ownState;

            updateStateNb++;
        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
            this.initialTickets = tickets;
        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            SortedBag.Builder<Ticket> builder = new SortedBag.Builder<>();

            for (int i = 0; i < 3; i++) {
                builder.add(initialTickets.get(i));
            }

            this.tickets = builder.build();
            return tickets;
        }

        @Override
        public TurnKind nextTurn() {
            turnCounter += 1;
            if (turnCounter > TURN_LIMIT)
                throw new Error("Trop de tours joués !");

            // Détermine les routes dont ce joueur peut s'emparer
            List<Route> claimableRoutes = allRoutes.stream()
                    .filter(r -> ownState.canClaimRoute(r))
                    .collect(Collectors.toList());

            if (claimableRoutes.isEmpty()) {
                return TurnKind.DRAW_CARDS;
            } else {
                int routeIndex = rng.nextInt(claimableRoutes.size());
                Route route = claimableRoutes.get(routeIndex);
                List<SortedBag<Card>> cards = ownState.possibleClaimCards(route);

                routeToClaim = route;
                initialClaimCards = cards.get(0);
                return TurnKind.CLAIM_ROUTE;
            }
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
            return null;
        }

        @Override
        public int drawSlot() {
            int slot = new Random().nextInt(6);

            if (slot == 5)
                slot = -1;
            return slot;
        }

        @Override
        public Route claimedRoute() {
            return routeToClaim;
        }

        @Override
        public SortedBag<Card> initialClaimCards() {
            return initialClaimCards;
        }

        @Override
        public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
            return options.get(0);
        }
    }

    @Test
    void TestGame(){
        TestPlayer player1 = new TestPlayer(150, ChMap.routes());
        TestPlayer player2 = new TestPlayer(200, ChMap.routes());

        Map<PlayerId, Player> players = new HashMap<>();
        players.put(PlayerId.PLAYER_1, player1);
        players.put(PlayerId.PLAYER_2, player2);

        Map<PlayerId, String> playersNames = new HashMap<>();
        playersNames.put(PlayerId.PLAYER_1, "Giovanni");
        playersNames.put(PlayerId.PLAYER_2, "Dylan");


        Game.play(players, playersNames, SortedBag.of(ChMap.tickets()), TestRandomizer.newRandom());

        assertTrue(player1.turnCounter < player1.receiveInfoNb);
        assertTrue(player2.turnCounter < player2.receiveInfoNb);
        assertTrue(player1.turnCounter < player1.updateStateNb);
        assertTrue(player2.turnCounter < player2.updateStateNb);
    }

}
