package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTest {

    @Test
    void constructorStaticError() {
        assertThrows(IllegalArgumentException.class, () -> {
            PlayerState state = PlayerState.initial(SortedBag.of(Card.BLUE));
        });
    }

    @Test
    void canClaimRouteWorks(){
        PlayerState state = PlayerState.initial(SortedBag.of(List.of(Card.BLUE, Card.GREEN, Card.YELLOW, Card.LOCOMOTIVE)));

        Route route1 = new Route("1", new Station(0, "LSN"), new Station(1, "GVN"), 3, Route.Level.OVERGROUND, Color.YELLOW);
        assertFalse(state.canClaimRoute(route1));

        Route route2 = new Route("2", new Station(1, "LSN"), new Station(2, "GVN"), 2, Route.Level.UNDERGROUND, Color.YELLOW);
        assertTrue(state.canClaimRoute(route2));
    }

    @Test
    void possibleClaimCardsWorks(){
        PlayerState state = PlayerState.initial(SortedBag.of(2, Card.YELLOW, 2, Card.LOCOMOTIVE));

        Route route2 = new Route("2", new Station(1, "LSN"), new Station(2, "GVN"), 2, Route.Level.UNDERGROUND, Color.YELLOW);
        List<SortedBag<Card>> possibleExpected = new ArrayList<>();
        possibleExpected.add(SortedBag.of(2, Card.YELLOW));
        possibleExpected.add(SortedBag.of(1, Card.LOCOMOTIVE, 1, Card.YELLOW));
        possibleExpected.add(SortedBag.of(2, Card.LOCOMOTIVE));

        assertEquals(possibleExpected, state.possibleClaimCards(route2));
    }

    @Test
    void possibleAdditionalCardsFailsWith3TypesOfCards(){
        PlayerState state = PlayerState.initial(SortedBag.of(2, Card.YELLOW, 2, Card.LOCOMOTIVE));
        SortedBag<Card> cards = SortedBag.of(List.of(Card.BLUE, Card.YELLOW, Card.GREEN));

        assertThrows(IllegalArgumentException.class, () -> {
            state.possibleAdditionalCards(3, cards, cards);
        });
    }

    @Test
    void possibleAdditionalCardsWorks(){
        PlayerState state = PlayerState.initial(SortedBag.of(2, Card.BLUE, 2, Card.LOCOMOTIVE));
        state = state.withAddedCards(SortedBag.of(List.of(Card.BLUE, Card.BLUE, Card.LOCOMOTIVE, Card.GREEN, Card.GREEN, Card.YELLOW)));

        SortedBag<Card> initialCards = SortedBag.of(List.of(Card.BLUE, Card.BLUE, Card.LOCOMOTIVE));
        SortedBag<Card> drawnCards = SortedBag.of(List.of(Card.BLUE, Card.BLUE, Card.YELLOW));

        List<SortedBag<Card>> expected = new ArrayList<>();
        expected.add(SortedBag.of(2, Card.BLUE));
        expected.add(SortedBag.of(1, Card.BLUE, 1, Card.LOCOMOTIVE));
        expected.add(SortedBag.of(2, Card.LOCOMOTIVE));

        assertEquals(expected, state.possibleAdditionalCards(2, initialCards, drawnCards));
    }
}
