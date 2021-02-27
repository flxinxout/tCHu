package ch.epfl.tchu.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

public class TrailTest {

    private static final Station GENEVE = new Station(2, "GEN");
    private static final Station LAUSANNE = new Station(4, "LAU");
    private static final Station FRIBOURG = new Station(5, "FRI");
    private static final Station BERNE = new Station(6, "BER");

    private static final Route GEN_FRI = new Route("6", GENEVE, FRIBOURG, 2, Route.Level.OVERGROUND, Color.YELLOW);
    private static final Route GEN_BER = new Route("5", GENEVE, BERNE, 2, Route.Level.OVERGROUND, Color.YELLOW);
    private static final Route GEN_LAU = new Route("4", GENEVE, LAUSANNE, 2, Route.Level.OVERGROUND, Color.YELLOW);
    private static final Route LAU_FRI = new Route("2", LAUSANNE, FRIBOURG, 4, Route.Level.OVERGROUND, Color.ORANGE);
    private static final Route LAU_BER = new Route("3", LAUSANNE, BERNE, 1, Route.Level.OVERGROUND, Color.VIOLET);
    private static final Route BER_FRI = new Route("2", BERNE, FRIBOURG, 4, Route.Level.OVERGROUND, Color.ORANGE);

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
        assertEquals(GENEVE, trail.station1());
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
        assertEquals(FRIBOURG, trail.station2());
    }

    @Test
    void isStation2GetNUllAccessorWork() {
        Trail trail = Trail.longest(routesEmpty);
        assertEquals(null, trail.station2());
    }

    @Test
    void toStringWorks() {
        List<Route> routes = new ArrayList<>(List.of(GEN_LAU, LAU_BER, LAU_FRI, GEN_BER, GEN_FRI, BER_FRI));

        Trail trail = Trail.longest(routes);
        assertEquals("GEN - LAU - FRI - GEN - BER - FRI (14)", trail.toString());
    }

    @Test
    void toStringWorks1Road() {
        List<Route> routes = new ArrayList<>(List.of(GEN_LAU));

        Trail trail = Trail.longest(routes);
        assertEquals("GEN - LAU (2)", trail.toString());
    }

    @Test
    void toStringWorks2Roads() {
        List<Route> routes = new ArrayList<>(List.of(GEN_LAU, LAU_BER));

        Trail trail = Trail.longest(routes);
        assertEquals("GEN - LAU - BER (3)", trail.toString());
    }

    @Test
    void toStringWorks3Roads() {
        List<Route> routes = new ArrayList<>(List.of(GEN_LAU, LAU_BER, BER_FRI));

        Trail trail = Trail.longest(routes);
        assertEquals("GEN - LAU - BER - FRI (7)", trail.toString());
    }

    @Test
    void toStringWorksWithNullRoutesList() {
        List<Route> routes = new ArrayList<>(List.of());

        Trail trail = Trail.longest(routes);
        assertEquals("Chemin inexistant", trail.toString());
    }

    private static void createTrail2Roads() {
        routes.add(GEN_LAU);
        routes.add(LAU_FRI);
    }

    private static void createTrail3Roads() {
        routes.add(GEN_LAU);
        routes.add(LAU_FRI);
        routes.add(LAU_BER);
    }
}























