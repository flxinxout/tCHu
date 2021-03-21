package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    static Station s1 = new Station(1, "Yverdon");
    static Station s2 = new Station(2, "Fribourg");
    static Station s3 = new Station(3, "Neuchâtel");
    static Station s4 = new Station(4, "Berne");
    static Station s5 = new Station(5, "Lucerne");
    static Station s6 = new Station(6, "Soleure");

    static Random r = TestRandomizer.newRandom();

    private static GameState gameState = GameState.initial(SortedBag.of(
            List.of(new Ticket(s1, s2, 2),
                    new Ticket(s3, s4, 3),
                    new Ticket(s5, s6, 4),
                    new Ticket(s2, s6, 8))
    ), r);

    //PlayerState playerState1 = new PlayerState()

    @Test
    void failTopTicket() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.topTickets(-1);
        });
    }

    @Test
    void failTopTicket2() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.topTickets(3);
        });
    }

    @Test
    void failWithoutTopTickets() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.withoutTopTickets(-1);
        });
    }

    @Test
    void failWithoutTopTickets2() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.withoutTopTickets(3);
        });
    }

    @Test
    void failTopCard() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.withoutTopTickets(3);
        });
    }

    @Test
    void initialWorks() {
        //TODO BUG
        System.out.println("cards randomizer: " + Deck.of(Constants.ALL_CARDS, r).topCards(8));
        System.out.println("current player id: " + gameState.currentPlayerId());
        System.out.println("cards of player 1: " + gameState.playerState(gameState.currentPlayerId()).cards().toString());
        System.out.println("cards of the other player: " + gameState.playerState(gameState.currentPlayerId().next()).cards().toString());
    }

    @Test
    void withMoreDiscardedCardsWorks() {
        assertEquals(2, gameState.withMoreDiscardedCards(SortedBag.of(2, Card.BLUE)).cardState().discardsSize());
        CardState cardState = (CardState) gameState.withMoreDiscardedCards(SortedBag.of(2, Card.BLUE)).cardState();
        // Test with a getter now removed
        //assertEquals(SortedBag.of(2, Card.BLUE).toString(), cardState.getDiscards().toString());
    }

    @Test
    void withCardsDeckRecreatedIfNeededWorksWithNonEmptyDeck() {
        CardState cardState = (CardState) gameState.withCardsDeckRecreatedIfNeeded(r).cardState();
        // Test with a getter now removed
       // assertEquals(SortedBag.of().toString(), cardState.getDiscards().toString());
    }

   /* @Test
    void withCardsDeckRecreatedIfNeededWorksWithEmptyDeck() {
        CardState cardState = (CardState) gameState.cardState();
        // Test with a setter now removed
        cardState.setDeck(cardState.getDeck().withoutTopCards(cardState.getDeck().size()));
        System.out.println("new deck size: " + cardState.getDeck().size());
        cardState.setDiscards(SortedBag.of(1, Card.BLUE));
        System.out.println("new discard: " + cardState.getDiscards().size());
        CardState cardState2 = (CardState) gameState.withCardsDeckRecreatedIfNeeded(r).cardState();
        assertEquals(1, cardState2.getDeck().size());
    }*/

    



}































