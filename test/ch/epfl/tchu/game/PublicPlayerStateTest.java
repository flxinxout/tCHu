package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PublicPlayerStateTest {

    @Test
    void claimPointsWork() {
        List<Route> routes = List.of(new Route("1", new Station(0, "LSN"), new Station(1, "GVN"), 3, Route.Level.OVERGROUND, Color.YELLOW),
                new Route("2", new Station(3, "RD"), new Station(4, "GV"), 5, Route.Level.OVERGROUND, Color.YELLOW),
                new Route("3", new Station(5, "SN"), new Station(6, "VN"), 4, Route.Level.OVERGROUND, Color.YELLOW));
        PublicPlayerState state = new PublicPlayerState(3, 5, routes);
        assertEquals(4 + 10 + 7, state.claimPoints());
    }

    @Test
    void carCountWork() {
        List<Route> routes = List.of(new Route("1", new Station(0, "LSN"), new Station(1, "GVN"), 3, Route.Level.OVERGROUND, Color.YELLOW),
                new Route("2", new Station(3, "RD"), new Station(4, "GV"), 5, Route.Level.OVERGROUND, Color.YELLOW),
                new Route("3", new Station(5, "SN"), new Station(6, "VN"), 4, Route.Level.OVERGROUND, Color.YELLOW));
        PublicPlayerState state = new PublicPlayerState(3, 5, routes);
        assertEquals(40 - (3 + 5 + 4), state.carCount());
    }

}













