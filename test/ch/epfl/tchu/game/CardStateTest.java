package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CardStateTest {
    private static List<Card> CARD_1 = new ArrayList<>(List.of(Card.BLUE));
    private static List<Card> CARDS_2 = new ArrayList<>(List.of(Card.WHITE, Card.GREEN));
    private static List<Card> CARDS_3 = new ArrayList<>(List.of(Card.RED, Card.LOCOMOTIVE, Card.ORANGE));
    private static List<Card> CARDS_4 = new ArrayList<>(List.of(Card.BLUE, Card.GREEN, Card.LOCOMOTIVE, Card.VIOLET));
    private static List<Card> CARDS_5 = new ArrayList<>(List.of(Card.YELLOW, Card.ORANGE, Card.GREEN, Card.BLUE, Card.BLACK));
    private static List<Card> CARDS_6 = new ArrayList<>(List.of(Card.YELLOW, Card.ORANGE, Card.GREEN, Card.BLUE, Card.BLACK, Card.LOCOMOTIVE));


    @Test
    void ofFailsWithSmallDeckSize(){
        Deck<Card> deck = Deck.of(SortedBag.of(CARDS_4), new Random());

        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(deck);;
        });
    }

    @Test
    void withDrawnFaceUpCardWorks(){
        CardState state = CardState.of(Deck.of(SortedBag.of(CARDS_6), new Random()));
        state.withDrawnFaceUpCard(3);
        assertEquals(CARDS_6, state.faceUpCards);
    }
}
