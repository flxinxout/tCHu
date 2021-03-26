package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;

import java.util.List;
import java.util.Map;

public class GameTest {

    public class Player1 implements Player{

        @Override
        public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {

        }

        @Override
        public void receiveInfo(String info) {

        }

        @Override
        public void updateState(PublicGameState newState, PlayerState ownState) {

        }

        @Override
        public void setInitialTicketChoice(SortedBag<Ticket> tickets) {

        }

        @Override
        public SortedBag<Ticket> chooseInitialTickets() {
            return null;
        }

        @Override
        public TurnKind nextTurn() {
            return null;
        }

        @Override
        public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
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


}
