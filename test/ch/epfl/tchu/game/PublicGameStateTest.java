package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PublicGameStateTest {

    static Station s1 = new Station(1, "Yverdon");
    static Station s2 = new Station(2, "Fribourg");
    static Station s3 = new Station(3, "Neuchâtel");
    static Station s4 = new Station(4, "Berne");
    static Station s5 = new Station(5, "Lucerne");
    static Station s6 = new Station(6, "Soleure");

    private static List<Route> routes1 = List.of(
            new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("B", s3, s6, 4, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 2, Route.Level.OVERGROUND, Color.RED));

    private static List<Route> routes2 = List.of(
            new Route("D", s4, s6, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 4, Route.Level.OVERGROUND, null),
            new Route("F", s4, s2, 1, Route.Level.OVERGROUND, Color.ORANGE));

    static List<Route> routes = List.of(
            new Route("A", s3, s1, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("B", s3, s6, 4, Route.Level.OVERGROUND, Color.GREEN),
            new Route("C", s4, s3, 2, Route.Level.OVERGROUND, Color.RED),
            new Route("D", s4, s6, 2, Route.Level.OVERGROUND, Color.BLACK),
            new Route("E", s4, s5, 4, Route.Level.OVERGROUND, null),
            new Route("F", s4, s2, 1, Route.Level.OVERGROUND, Color.ORANGE));

    @Test
    void constructorFailsNegativeTicketsCount() {
        assertThrows(IllegalArgumentException.class, () -> {
           new PublicGameState(-1, new PublicCardState(List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.WHITE, Card.BLACK),
                   2, 3), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(2, 2, List.of()),
                   PlayerId.PLAYER_2, new PublicPlayerState(3, 3, List.of())), null);
        });
    }

    @Test
    void constructorFailsTooBigMap() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(2, new PublicCardState(List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.WHITE, Card.BLACK),
                    2, 3), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(2, 2, List.of()),
                    PlayerId.PLAYER_2, new PublicPlayerState(3, 3, List.of()),
                    PlayerId.PLAYER_1, new PublicPlayerState(3, 3, List.of())),
                    null);
        });
    }

    @Test
    void constructorFailsTooSmallMap() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PublicGameState(2, new PublicCardState(List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.WHITE, Card.BLACK),
                    2, 3), PlayerId.PLAYER_1, Map.of(), null);
        });
    }

    @Test
    void constructorFailsNullCardState() {
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(2,null, PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(2, 2, List.of()),
                    PlayerId.PLAYER_2, new PublicPlayerState(3, 3, List.of())), null);
        });
    }

    @Test
    void constructorFailsNullFirstPlayerId() {
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(2,new PublicCardState(List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.WHITE, Card.BLACK),
                    2, 3), null, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(2, 2, List.of()),
                    PlayerId.PLAYER_2, new PublicPlayerState(3, 3, List.of())), null);
        });
    }

    @Test
    void constructorFailsNullPlayerState() {
        assertThrows(NullPointerException.class, () -> {
            new PublicGameState(2,new PublicCardState(List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.WHITE, Card.BLACK),
                    2, 3), PlayerId.PLAYER_1, null, null);
        });
    }

    @Test
    void canDrawCardsWorks() {
        PublicGameState gameState = new PublicGameState(3, new PublicCardState(List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.WHITE, Card.BLACK),
                2, 3), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(2, 2, List.of()),
                PlayerId.PLAYER_2, new PublicPlayerState(3, 3, List.of())), null);

        assertTrue(gameState.canDrawCards());
    }

    @Test
    void canDrawCardsFailsWithFewerCards() {
        PublicGameState gameState = new PublicGameState(3, new PublicCardState(List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.WHITE, Card.BLACK),
                1, 3), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(2, 2, List.of()),
                PlayerId.PLAYER_2, new PublicPlayerState(3, 3, List.of())), null);

        assertFalse(gameState.canDrawCards());
    }

    @Test
    void claimedRoutesWorks() {
        PublicGameState publicGameState = new PublicGameState(4, new PublicCardState(List.of(Card.BLUE, Card.RED, Card.LOCOMOTIVE, Card.WHITE, Card.BLACK),
                2, 3), PlayerId.PLAYER_1, Map.of(PlayerId.PLAYER_1, new PublicPlayerState(2, 2, routes1),
                PlayerId.PLAYER_2, new PublicPlayerState(3, 3, routes2)), null);

        for (int i = 0; i < publicGameState.claimedRoutes().size(); i++) {
            assertEquals(routes.get(i).id(), publicGameState.claimedRoutes().get(i).id());
        }
    }

}
















