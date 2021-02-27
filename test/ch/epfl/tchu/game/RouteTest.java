package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import ch.epfl.test.TestRandomizer;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RouteTest {
    private static final Route.Level OVERGROUND = Route.Level.OVERGROUND;
    private static final Route.Level UNDERGROUND = Route.Level.UNDERGROUND;
    private static final Color BLACK = Color.BLACK;
    private static final Station STATION1 = new Station(0, "station1");
    private static final Station STATION2 = new Station(1, "station2");
    private static final Station GENEVE = new Station(2, "GEN");
    private static final Station LAUSANNE = new Station(3, "LAU");
    private static final Station BERNE = new Station(4, "BER");

    @Test
    void routeConstructorFailsForSameStations() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Route("route", STATION1, STATION1, 2, OVERGROUND, BLACK);
        });
    }

    @Test
    void routeConstructorFailsForNullId() {
        assertThrows(NullPointerException.class, () -> {
            new Route(null, STATION1, STATION2, 2, OVERGROUND, BLACK);
        });
    }

    @Test
    void routeConstructorFailsForNullStation1() {
        assertThrows(NullPointerException.class, () -> {
            new Route("route", null, STATION2, 2, OVERGROUND, BLACK);
        });
    }

    @Test
    void routeConstructorFailsForNullStation2() {
        assertThrows(NullPointerException.class, () -> {
            new Route("route", STATION1, null, 2, OVERGROUND, BLACK);
        });
    }

    @Test
    void routeConstructorFailsForNullLevel() {
        assertThrows(NullPointerException.class, () -> {
            new Route("route", STATION1, STATION2, 2, null, BLACK);
        });
    }

    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private static String randomName(Random rng, int length) {
        var sb = new StringBuilder();
        for (int i = 0; i < length; i++)
            sb.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        return sb.toString();
    }

    @Test
    void idAccessorWorks() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var name = randomName(rng, 1 + rng.nextInt(10));
            var route = new Route(name, STATION1, STATION2, 2, OVERGROUND, BLACK);
            assertEquals(name, route.id());
        }
    }

    @Test
    void station1AccessorWorks() {
        Route route = new Route("route", STATION1, STATION2, 2, OVERGROUND, BLACK);
        assertEquals(STATION1, route.station1());
    }

    @Test
    void station2AccessorWorks() {
        Route route = new Route("route", STATION1, STATION2, 2, OVERGROUND, BLACK);
        assertEquals(STATION2, route.station2());
    }

    @Test
    void lengthAccessorWorks() {
        var rng = TestRandomizer.newRandom();
        for (int i = 0; i < TestRandomizer.RANDOM_ITERATIONS; i++) {
            var length = rng.nextInt(Integer.MAX_VALUE);
            var route = new Route("route", STATION1, STATION2, length, OVERGROUND, BLACK);
            assertEquals(length, route.length());
        }
    }

    @Test
    void levelAccessorWorks() {
        Route route = new Route("route", STATION1, STATION2, 2, OVERGROUND, BLACK);
        assertEquals(OVERGROUND, route.level());

        Route route2 = new Route("route2", STATION1, STATION2, 2, UNDERGROUND, BLACK);
        assertEquals(UNDERGROUND, route2.level());
    }

    @Test
    void colorAccessorWorks() {
        for (Color color: Color.ALL) {
            Route route = new Route("route", STATION1, STATION2, 2, OVERGROUND, color);
            assertEquals(color, route.color());
        }
    }

    @Test
    void stationsAccessorWorks() {
        List<Station> stations = new ArrayList<>(Arrays.asList(STATION1, STATION2));
        Route route = new Route("route", STATION1, STATION2, 2, OVERGROUND, BLACK);
        assertEquals(stations, route.stations());
    }

    @Test
    void stationOppositeWorks() {
        Route route = new Route("route", STATION1, STATION2, 2, OVERGROUND, BLACK);
        assertEquals(STATION1, route.stationOpposite(route.station2()));
        assertEquals(STATION2, route.stationOpposite(route.station1()));
    }

    @Test
    void stationOppositeFailsWithRandomStation() {

        Route route = new Route("route", STATION1, STATION2, 2, OVERGROUND, BLACK);
        assertThrows(IllegalArgumentException.class, () -> {
            route.stationOpposite(new Station(3, "test3"));
        });
    }

    @Test
    void possibleClaimCardsWorksWithColoredOvergroundRoads() {
        for (int length = Constants.MIN_ROUTE_LENGTH; length < Constants.MAX_ROUTE_LENGTH; length++) {
            for (Color color: Color.ALL) {
                Route route = new Route("route", GENEVE, LAUSANNE, length, OVERGROUND, color);
                List<SortedBag<Card>> possibleClaimCards = route.possibleClaimCards();

                List<SortedBag<Card>> expectedPossibleCards = new ArrayList<>();
                SortedBag<Card> bag = SortedBag.of(length, Card.of(color));
                expectedPossibleCards.add(bag);

                assertEquals(expectedPossibleCards, possibleClaimCards);
            }
        }
    }

    @Test
    void possibleClaimCardsWorksWithNeutralOvergroundRoads() {
        //Test bonne liste
        for (int length = Constants.MIN_ROUTE_LENGTH; length < Constants.MAX_ROUTE_LENGTH; length++) {
            Route route = new Route("route", GENEVE, LAUSANNE, length, OVERGROUND, null);
            List<SortedBag<Card>> possibleClaimCards = route.possibleClaimCards();

            List<SortedBag<Card>> expectedPossibleCards = new ArrayList<>();
            for (Card card: Card.CARS) {
                SortedBag<Card> bag = SortedBag.of(length, card);
                expectedPossibleCards.add(bag);
            }

            assertEquals(expectedPossibleCards, possibleClaimCards);
        }

        //Test de l'ordre dans l'ordre des couleurs
        for (int i = 0; i < Color.COUNT; i++){
            Route route = new Route("route", GENEVE, LAUSANNE, 2, OVERGROUND, null);
            List<SortedBag<Card>> possibleClaimCards = route.possibleClaimCards();

            SortedBag<Card> expectedCard = SortedBag.of(2, Card.values()[i]);
            assertEquals(expectedCard, possibleClaimCards.get(i));
        }
    }

    @Test
    void possibleClaimCardsWorksWithColoredUndergroundRoads() {
        for (int length = Constants.MIN_ROUTE_LENGTH; length < Constants.MAX_ROUTE_LENGTH; length++) {
            for (Color color: Color.ALL) {
                Route route = new Route("route", GENEVE, LAUSANNE, length, UNDERGROUND, color);
                List<SortedBag<Card>> possibleClaimCards = route.possibleClaimCards();

                List<SortedBag<Card>> expectedPossibleCards = new ArrayList<>();

                SortedBag<Card> onlyColorBag = SortedBag.of(length, Card.of(color));
                expectedPossibleCards.add(onlyColorBag);

                for (int i = 1; i <= length; i++) {
                    SortedBag.Builder<Card> bagBuilder = new SortedBag.Builder<>();
                    bagBuilder.add(length-i, Card.of(color));
                    bagBuilder.add(i, Card.LOCOMOTIVE);
                    expectedPossibleCards.add(bagBuilder.build());
                }
                if (expectedPossibleCards.isEmpty())
                    expectedPossibleCards.add(SortedBag.of(0, Card.LOCOMOTIVE));

                assertEquals(expectedPossibleCards, possibleClaimCards);
            }
        }

        //Test de l'ordre
        Route route = new Route("route", GENEVE, LAUSANNE, 3, UNDERGROUND, Color.BLACK);
        List<SortedBag<Card>> possibleClaimCards = route.possibleClaimCards();
        for (int i = 0; i < 4; i++){
            SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
            builder.add(3 - i, Card.of(BLACK));
            builder.add(i, Card.LOCOMOTIVE);
            assertEquals(builder.build(), possibleClaimCards.get(i));
        }

        //First
        assertEquals(SortedBag.of(3, Card.of(BLACK)), possibleClaimCards.get(0));
        //Second
        assertEquals(SortedBag.of(2, Card.of(BLACK), 1, Card.LOCOMOTIVE), possibleClaimCards.get(1));
        //Third
        assertEquals(SortedBag.of( 1, Card.BLACK, 2, Card.LOCOMOTIVE), possibleClaimCards.get(2));
        //Last
        assertEquals(SortedBag.of(3, Card.LOCOMOTIVE), possibleClaimCards.get(3));
    }

    @Test
    void possibleClaimCardsWorksWithNeutralUndergroundRoads() {

        Route route = new Route("route", GENEVE, LAUSANNE, 2, UNDERGROUND, null);
        List<SortedBag<Card>> possibleClaimCards = route.possibleClaimCards();
        List<SortedBag<Card>> expectedPossibleCards = new ArrayList<>();

        for (int i = 0; i <= 2; i++) {
            if (i < 2) {
                for (Card car : Card.CARS) {
                    SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();
                    cardsBuilder.add(2 - i, car);
                    cardsBuilder.add(i, Card.LOCOMOTIVE);
                    expectedPossibleCards.add(cardsBuilder.build());
                }
            }
            else {
                expectedPossibleCards.add(SortedBag.of(i, Card.LOCOMOTIVE));
            }
        }

        assertEquals(expectedPossibleCards, possibleClaimCards);
    }

    @Test
    void additionnalClaimCardsCountWorksWithOnlyColorAtStart(){
        Route route = new Route("route", GENEVE, LAUSANNE, 3, UNDERGROUND, null);

        for (int i = Constants.ADDITIONAL_TUNNEL_CARDS; i >= 0; i--) {
            SortedBag.Builder<Card> builder = new SortedBag.Builder<>();
            builder.add(i, Card.of(Color.ORANGE));
            builder.add(Constants.ADDITIONAL_TUNNEL_CARDS-i, Card.of(BLACK));
            int expected = route.additionalClaimCardsCount(SortedBag.of(3, Card.of(Color.ORANGE)), builder.build());
            assertEquals(i, expected);
        }
    }

    //TODO: same but with locomotive au départ aussi
    @Test
    void additionnalClaimCardsCountWorks(){

    }

    @Test
    void claimPointsIsCorrect(){
        for (int i = Constants.MIN_ROUTE_LENGTH; i < Constants.MAX_ROUTE_LENGTH; i++) {
            Route route = new Route("route", GENEVE, LAUSANNE, i, OVERGROUND, null);
            int expected = 0;
            switch (i) {
                case 1:
                    expected = 1;
                    break;
                case 2:
                    expected = 2;
                    break;
                case 3:
                    expected = 4;
                    break;
                case 4:
                    expected = 7;
                    break;
                case 5:
                    expected = 10;
                    break;
                case 6:
                    expected = 15;
                    break;
            }
            assertEquals(expected, route.claimPoints());
        }
    }
}
