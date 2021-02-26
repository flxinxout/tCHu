package ch.epfl.tchu.game;

import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RouteTest {
    @Test
    void routeConstructorFailsForSameStations() {
        Station station = new Station(0, "test");
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("test", station, station, 2, Route.Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsForNullId() {
        assertThrows(NullPointerException.class, () -> {
            new Route(null, new Station(0, "test"), new Station(1, "test2"), 2, Route.Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsForNullStation1() {
        assertThrows(NullPointerException.class, () -> {
            new Route("test", null, new Station(1, "test2"), 2, Route.Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsForNullStation2() {
        assertThrows(NullPointerException.class, () -> {
            new Route("test", new Station(0, "test"), null, 2, Route.Level.OVERGROUND, Color.BLACK);
        });
    }

    @Test
    void routeConstructorFailsForNullLevel() {
        assertThrows(NullPointerException.class, () -> {
            new Route("test", new Station(0, "test"), new Station(1, "test2"), 2, null, Color.BLACK);
        });
    }

    @Test
    void idAccessorWorks() {
        Route route = new Route("test", new Station(0, "test"), new Station(1, "test2"), 2, Route.Level.OVERGROUND, Color.BLACK);
        assertEquals("test", route.id());
    }

    @Test
    void station1AccessorWorks() {
        Station station1 = new Station(0, "test");
        Route route = new Route("test", station1, new Station(1, "test2"), 2, Route.Level.OVERGROUND, Color.BLACK);
        assertEquals(station1, route.station1());
    }

    @Test
    void station2AccessorWorks() {
        Station station2 = new Station(1, "test2");
        Route route = new Route("test", new Station(0, "test"), station2, 2, Route.Level.OVERGROUND, Color.BLACK);
        assertEquals(station2, route.station2());
    }

    @Test
    void lengthAccessorWorks() {
        Route route = new Route("test", new Station(0, "test"), new Station(1, "test2"), 2, Route.Level.OVERGROUND, Color.BLACK);
        assertEquals(2, route.length());
    }

    @Test
    void levelAccessorWorks() {
        Route route = new Route("test", new Station(0, "test"), new Station(1, "test2"), 2, Route.Level.OVERGROUND, Color.BLACK);
        assertEquals(Route.Level.OVERGROUND, route.level());
    }

    @Test
    void colorAccessorWorks() {
        Route route = new Route("test", new Station(0, "test"), new Station(1, "test2"), 2, Route.Level.OVERGROUND, Color.BLACK);
        assertEquals(Color.BLACK, route.color());
    }

    @Test
    void stationsAccessorWorks() {
        List<Station> stations = new ArrayList<>(Arrays.asList(new Station(0, "test"), new Station(1, "test2")));
        Route route = new Route("test", stations.get(0), stations.get(1), 2, Route.Level.OVERGROUND, Color.BLACK);
        assertEquals(stations, route.stations());
    }

    @Test
    void stationOppositeWorksWithStation1() {
        Station[] stations = new Station[] {
                new Station(0, "test"),
                new Station(1, "test2")
        };
        Route route = new Route("test", stations[0], stations[1], 2, Route.Level.OVERGROUND, Color.BLACK);
        assertEquals(stations[1], route.stationOpposite(route.station1()));
    }

    @Test
    void stationOppositeWorksWithStation2() {
        Station[] stations = new Station[] {
                new Station(0, "test"),
                new Station(1, "test2")
        };
        Route route = new Route("test", stations[0], stations[1], 2, Route.Level.OVERGROUND, Color.BLACK);
        assertEquals(stations[0], route.stationOpposite(route.station2()));
    }

    @Test
    void stationOppositeFailsWithRandomStation() {
        Station[] stations = new Station[] {
                new Station(0, "test"),
                new Station(1, "test2")
        };
        Route route = new Route("test", stations[0], stations[1], 2, Route.Level.OVERGROUND, Color.BLACK);
        assertThrows(IllegalArgumentException.class, () -> {
            route.stationOpposite(new Station(3, "test3"));
        });
    }
}
