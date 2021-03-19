package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    static Station s1 = new Station(1, "Yverdon");
    static Station s2 = new Station(2, "Fribourg");
    static Station s3 = new Station(3, "Neuchâtel");
    static Station s4 = new Station(4, "Berne");
    static Station s5 = new Station(5, "Lucerne");
    static Station s6 = new Station(6, "Soleure");

    private static GameState gameState = GameState.initial(SortedBag.of(1, new Ticket(s1, s2, 2), 1, new Ticket(s3, s4, 3)),
            TestRandomizer.newRandom());

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

}
