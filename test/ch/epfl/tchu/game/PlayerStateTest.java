package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTest {

    @Test
    void constructorStaticError() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState state = PlayerState.initial(SortedBag.of(Card.BLUE));
        });
    }



}
