package ch.epfl.tchu.net;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.PublicCardState;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SerdesTest {

    private static final List<Card> FACE_UP_CARDS =
            List.of(Card.BLUE, Card.BLACK, Card.ORANGE, Card.ORANGE, Card.RED);

    @Test
    void publicCardStateSerdeTest() {
        PublicCardState publicCardState = new PublicCardState(FACE_UP_CARDS, 20, 10);
        String s = Serdes.SERDE_OF_PUBLIC_CARD_STATE.serialize(publicCardState);
        System.out.println(s);
        PublicCardState t = Serdes.SERDE_OF_PUBLIC_CARD_STATE.deserialize(s);
        for(Card c : t.faceUpCards()) {
            System.out.print(c.color());
        }
        System.out.println();
        System.out.println("decksuze: " + t.deckSize());
        System.out.println("discard: " + t.discardsSize());
    }

}
