package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
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
    private static List<Card> CARDS_6 = new ArrayList<>(List.of(Card.YELLOW, Card.ORANGE, Card.GREEN, Card.BLUE,
            Card.BLACK, Card.LOCOMOTIVE, Card.BLUE, Card.ORANGE, Card.WHITE));

    private static Deck<Card> emptyCardDeck(){
        return Deck.of(new SortedBag.Builder<Card>().build(), new Random());
    }

    @Test
    void ofFailsWithSmallDeckSize(){
        Deck<Card> deck = Deck.of(SortedBag.of(CARDS_4), TestRandomizer.newRandom());
        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(deck);;
        });
    }

    @Test
    void ofWorks(){
        Deck<Card> deck = Deck.of(SortedBag.of(CARDS_6), TestRandomizer.newRandom());
        Deck<Card> initialDeck = deck;
        List<Card> topCards = new ArrayList<>();
        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            topCards.add(deck.topCard());
            deck = deck.withoutTopCard();
        }

        assertEquals(topCards, CardState.of(initialDeck).faceUpCards());
    }

    @Test
    void withDrawnFaceUpCardFailNullSlot() {
        Deck<Card> deck = Deck.of(SortedBag.of(CARDS_4), TestRandomizer.newRandom());
        assertThrows(IllegalArgumentException.class, () -> {
           CardState.of(deck).withDrawnFaceUpCard(-1);
        });
    }

    @Test
    void withDrawnFaceUpCardFailEmptyDeck() {
        Deck<Card> deck = emptyCardDeck();
        assertThrows(IllegalArgumentException.class, () -> {
            CardState.of(deck).withDrawnFaceUpCard(0);
        });
    }

    @Test
    void withDrawnFaceUpCardWorks() {
        Deck<Card> deck = Deck.of(SortedBag.of(CARDS_6), TestRandomizer.newRandom());
        CardState cardState = CardState.of(deck); // faceup de 5, discard 0 et pioche 4
        CardState newCardState = cardState.withDrawnFaceUpCard(3);
        //assertEquals(deck.cards, newCardState.faceUpCards());
    }



}
