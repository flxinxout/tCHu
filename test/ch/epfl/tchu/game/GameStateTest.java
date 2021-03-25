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
    void topTicketFailsWithNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.topTickets(-1);
        });
    }

    @Test
    void TopTicketFailsWithTooBigCount() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.topTickets(5);
        });
    }

    @Test
    void withoutTopTicketsFailsWithNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.withoutTopTickets(-1);
        });
    }

    @Test
    void withoutTopTicketsFailsWithTooBigCount() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.withoutTopTickets(5);
        });
    }

    @Test
    void topCardFails() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameState.topCard();
        });
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

    @Test
    void withChosenAdditionalTicketsFailsWithNonContainigTickets(){
        SortedBag<Ticket> drawnTickets = SortedBag.of(List.of(
                new Ticket(s1, s2, 2),
                new Ticket(s3, s4, 3),
                new Ticket(s5, s6, 4),
                new Ticket(s2, s6, 8)));

        SortedBag<Ticket> chosenTickets = SortedBag.of(List.of(
                new Ticket(s1, s2, 2),
                new Ticket(s2, s6, 8)));

        assertThrows(IllegalArgumentException.class, () -> {
            gameState.withChosenAdditionalTickets(chosenTickets, drawnTickets);
        });
    }
}



























