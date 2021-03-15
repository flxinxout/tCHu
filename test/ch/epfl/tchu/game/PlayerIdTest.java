package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerIdTest {

    @Test
    void allWorks() {
        PlayerId pl1 = PlayerId.PLAYER_1;
        PlayerId pl2 = PlayerId.PLAYER_2;
        assertEquals(List.of(pl1, pl2), PlayerId.ALL);
    }

    @Test
    void countWorks() {
        assertEquals(2, PlayerId.COUNT);
    }

    @Test
    void nextWork() {
        PlayerId pl1 = PlayerId.PLAYER_1;
        PlayerId pl2 = PlayerId.PLAYER_2;
        assertEquals(pl2, pl1.next());
        assertEquals(pl1, pl2.next());
    }

}
