package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PublicCardStateTest {
    private static List<Card> CARD_1 = new ArrayList<>(List.of(Card.BLUE));
    private static List<Card> CARDS_2 = new ArrayList<>(List.of(Card.WHITE, Card.GREEN));
    private static List<Card> CARDS_3 = new ArrayList<>(List.of(Card.RED, Card.LOCOMOTIVE, Card.ORANGE));
    private static List<Card> CARDS_4 = new ArrayList<>(List.of(Card.BLUE, Card.GREEN, Card.LOCOMOTIVE, Card.VIOLET));
    private static List<Card> CARDS_5 = new ArrayList<>(List.of(Card.YELLOW, Card.ORANGE, Card.GREEN, Card.BLUE, Card.BLACK));

    @Test
    void constructorFailsWrongNumberFaceUpCards(){
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(CARDS_3, 2, 2);
        });
    }

    @Test
    void constructorFailsNegativeDeckSize(){
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(CARDS_5, -2, 2);
        });
    }

    @Test
    void constructorFailsNegativeDiscardsSize(){
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicCardState(CARDS_5, 10, -2);
        });
    }

    @Test
    void totalSizeWorks(){
        PublicCardState state = new PublicCardState(CARDS_5, 10, 2);
        assertEquals(17, state.totalSize());
    }
}
