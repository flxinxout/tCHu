package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class DeckTest {

    @Test
    void isDeckImmutable() {
        SortedBag<String> cards = SortedBag.of(2, "as de pique", 3, "dame de cœur");
        Deck<String> deck1 = Deck.of(cards, new Random());
        Deck deck2 = deck1.withoutTopCard();

    }
}
