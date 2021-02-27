package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrailTest {

    private static final Station station1 = new Station(2, "gnv");
    private static final Station station2 = new Station(4, "lsn");
    private static final Station station3 = new Station(5, "frg");
    private static final Station station4 = new Station(6, "berne");
    private static final Route route1 = new Route("1", station1, station2, 2, Route.Level.OVERGROUND, Color.YELLOW);
    private static final Route route2 = new Route("2", station2, station3, 2, Route.Level.OVERGROUND, Color.ORANGE);
    private static final Route route3 = new Route("3", station2, station4, 5, Route.Level.OVERGROUND, Color.VIOLET);
    private static final List<Route> routes = new ArrayList<>();
    private static final List<Route> routesEmpty = new ArrayList<>();

    @Test
    void isLength2AccessorWork() {
        createTrail2Roads();
        Trail trail = Trail.longest(routes);
        assertEquals(4, trail.length());
    }

    // NOT WORK affiche 9 au lieu de 7
    @Test
    void isLengthMoreThan2AccessorWork() {
        createTrail3Roads();
        Trail trail = Trail.longest(routes);
        assertEquals(7, trail.length());
    }

    @Test
    void isStation1GetStationAccessorWork() {
        createTrail2Roads();
        Trail trail = Trail.longest(routes);
        assertEquals(station1, trail.station1());
    }

    @Test
    void isStation1GetNUllAccessorWork() {
        Trail trail = Trail.longest(routesEmpty);
        assertEquals(null, trail.station1());
    }

    @Test
    void isStation2GetStationAccessorWork() {
        createTrail2Roads();
        Trail trail = Trail.longest(routes);
        assertEquals(station3, trail.station2());
    }

    @Test
    void isStation2GetNUllAccessorWork() {
        Trail trail = Trail.longest(routesEmpty);
        assertEquals(null, trail.station2());
    }

    /*
    faudra revoir l'implémentation de longest car si tu test le test de toString, le chemin met 2 fois lsn
     */
    @Test
    void isToStringWorkFine() {
        createTrail2Roads();
        Trail trail = Trail.longest(routes);
        assertEquals(createToString(), trail.toString());
    }

    private static void createTrail2Roads() {
        routes.add(route1);
        routes.add(route2);
    }

    private static void createTrail3Roads() {
        routes.add(route1);
        routes.add(route2);
        routes.add(route3);
    }

    private static String createToString() {
        return "gnv - lsn - frg (9)";
    }

}























