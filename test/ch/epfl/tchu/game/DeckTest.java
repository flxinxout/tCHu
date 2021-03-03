package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {
    private static List<Card> CARD_1 = new ArrayList<>(List.of(Card.BLUE));
    private static List<Card> CARDS_2 = new ArrayList<>(List.of(Card.WHITE, Card.GREEN));
    private static List<Card> CARDS_3 = new ArrayList<>(List.of(Card.RED, Card.LOCOMOTIVE, Card.ORANGE));
    private static List<Card> CARDS_4 = new ArrayList<>(List.of(Card.BLUE, Card.GREEN, Card.LOCOMOTIVE, Card.VIOLET));
    private static List<Card> CARDS_5 = new ArrayList<>(List.of(Card.YELLOW, Card.ORANGE, Card.GREEN, Card.BLUE, Card.BLACK));

    private static Deck<Card> emptyCardDeck(){
        return Deck.of(new SortedBag.Builder<Card>().build(), new Random());
    }

    @Test
    void sizeWorks(){
        List<List<Card>> cards = List.of(CARD_1, CARDS_2, CARDS_3, CARDS_4, CARDS_5);
        for (int i = 0; i < cards.size(); i++) {
            Deck deck = Deck.of(SortedBag.of(cards.get(i)), new Random());
            assertEquals(i+1, deck.size());
        }
    }

    @Test
    void sizeWorksEmptyDeck(){
        Deck deck = emptyCardDeck();
        assertEquals(0, deck.size());
    }

    @Test
    void isEmptyWorksWithNonEmptyDeck(){
        List<List<Card>> cards = List.of(CARD_1, CARDS_2, CARDS_3, CARDS_4, CARDS_5);
        for (int i = 0; i < cards.size(); i++) {
            Deck deck = Deck.of(SortedBag.of(cards.get(i)), new Random());
            assertFalse(deck.isEmpty());
        }
    }

    @Test
    void isEmptyWorksWithEmptyDeck(){
        Deck deck = emptyCardDeck();
        assertTrue(deck.isEmpty());
    }

    @Test
    void topCardFailsWithEmptyDeck(){
        Deck deck = emptyCardDeck();
        assertThrows(IllegalArgumentException.class, () -> {
            deck.topCard();
        });
    }

    @Test
    void withoutTopCardFailsWithEmptyDeck(){
        Deck deck = emptyCardDeck();
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCard();
        });
    }

    @Test
    void topCardsFailsWithNegativeValues(){
        Deck deck = Deck.of(SortedBag.of(CARDS_3), new Random());
        assertThrows(IllegalArgumentException.class, () -> {
            deck.topCards(-1);
        });
    }

    @Test
    void topCardsFailsWithBiggerValues(){
        Deck deck = Deck.of(SortedBag.of(CARDS_3), new Random());
        assertThrows(IllegalArgumentException.class, () -> {
            deck.topCards(deck.size() + 1);
        });
    }

    @Test
    void withoutTopCardsFailsWithNegativeValues(){
        Deck deck = Deck.of(SortedBag.of(CARDS_3), new Random());
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCards(-1);
        });
    }

    @Test
    void withoutTopCardsFailsWithBiggerValues(){
        Deck deck = Deck.of(SortedBag.of(CARDS_3), new Random());
        assertThrows(IllegalArgumentException.class, () -> {
            deck.withoutTopCards(deck.size() + 1);
        });
    }
}
