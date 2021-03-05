package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InfoTest {

    private static final Station GENEVE = new Station(2, "GEN");
    private static final Station LAUSANNE = new Station(4, "LAU");
    private static final Station FRIBOURG = new Station(5, "FRI");
    private static final Station BERNE = new Station(6, "BER");
    private static final Station LUCERNE = new Station(7, "LCN");
    private static final Station INTERLAKEN = new Station(8, "INT");

    private static final Route GEN_FRI = new Route("6", GENEVE, FRIBOURG, 2, Route.Level.OVERGROUND, Color.YELLOW);
    private static final Route GEN_BER = new Route("5", GENEVE, BERNE, 2, Route.Level.OVERGROUND, Color.YELLOW);
    private static final Route GEN_LAU = new Route("4", GENEVE, LAUSANNE, 2, Route.Level.OVERGROUND, Color.YELLOW);
    private static final Route LAU_FRI = new Route("2", LAUSANNE, FRIBOURG, 1, Route.Level.OVERGROUND, Color.ORANGE);
    private static final Route LAU_BER = new Route("3", LAUSANNE, BERNE, 1, Route.Level.OVERGROUND, Color.VIOLET);
    private static final Route BER_FRI = new Route("2", BERNE, FRIBOURG, 1, Route.Level.OVERGROUND, Color.ORANGE);
    private static final Route LCN_INT = new Route("8", LUCERNE, INTERLAKEN, 5, Route.Level.OVERGROUND, Color.ORANGE);
    private static final Route FRI_LUCN = new Route("1", FRIBOURG, LUCERNE, 3, Route.Level.OVERGROUND, Color.ORANGE);

    private static List<Card> CARD_1 = new ArrayList<>(List.of(Card.BLUE, Card.BLUE, Card.BLUE));
    private static List<Card> CARDS_2 = new ArrayList<>(List.of(Card.WHITE, Card.GREEN));
    private static List<Card> CARDS_3 = new ArrayList<>(List.of(Card.RED, Card.LOCOMOTIVE, Card.ORANGE, Card.RED));
    private static List<Card> CARDS_4 = new ArrayList<>(List.of(Card.BLUE, Card.GREEN, Card.LOCOMOTIVE, Card.VIOLET));
    private static List<Card> CARDS_5 = new ArrayList<>(List.of(Card.YELLOW, Card.ORANGE, Card.GREEN, Card.BLUE, Card.BLACK));
    private static List<Card> CARDS_MORE = new ArrayList<>(List.of(Card.WHITE, Card.GREEN, Card.YELLOW, Card.ORANGE, Card.GREEN, Card.BLUE, Card.BLACK));

   /* @Test
    void constructorWork() {
        Info info = new Info("Giovanni");
        assertEquals("Giovanni", info.name());
    }*/

    @Test
    void cardNameWorkSingular() {
        assertEquals(StringsFr.BLUE_CARD, Info.cardName(Card.BLUE, 1));
    }

    @Test
    void cardNameWorkPlural() {
        assertEquals(StringsFr.BLUE_CARD
                + "s", Info.cardName(Card.BLUE, 2));
    }


    @Test
    void willPlayFirstWork() {
        Info info = new Info("Giovanni");
        assertEquals("Giovanni jouera en premier.\n\n", info.willPlayFirst());
    }

    @Test
    void keftTicketsWork() {
        Info info = new Info("Giovanni");
        assertEquals("Giovanni a gardé 3 billets.\n", info.keptTickets(3));
    }

    @Test
    void drewAdditionalCostWork() {
        Info info = new Info("Giovanni");
        SortedBag<Card> sortedBag = SortedBag.of(CARDS_4);
        String string = info.drewAdditionalCards(sortedBag, 2);
        assertEquals("Les cartes supplémentaires sont 1 violette, 1 bleue, 1 verte et 1 locomotive." +
                " Elles impliquent un coût additionnel de 2 cartes.\n", string);
    }

    @Test
    void drewAdditionalCostWorkWith0AditionnalCards() {
        Info info = new Info("Giovanni");
        SortedBag<Card> sortedBag = SortedBag.of(CARDS_3);
        String string = info.drewAdditionalCards(sortedBag, 0);
        assertEquals("Les cartes supplémentaires sont 1 orange, 2 rouges et 1 locomotive." +
                " Elles n'impliquent aucun coût additionnel.\n", string);
    }

    @Test
    void attemptsTunnelClaimWork() {
        Info info = new Info("Giovanni");
        SortedBag<Card> sortedBag = SortedBag.of(CARDS_3);
        Route route = new Route("1", new Station(1, "gnv"), new Station(2, "lsn"), 4, Route.Level.UNDERGROUND, Color.YELLOW);
        assertEquals("Giovanni tente de s'emparer du tunnel gnv" + StringsFr.EN_DASH_SEPARATOR + "lsn au moyen de " +
                "1 orange, 2 rouges et 1 locomotive !\n", info.attemptsTunnelClaim(route, sortedBag));
    }

    @Test
    void attemptsTunnelClaimWork3SameCards() {
        Info info = new Info("Giovanni");
        SortedBag<Card> sortedBag = SortedBag.of(CARD_1);
        Route route = new Route("1", new Station(1, "gnv"), new Station(2, "lsn"), 3, Route.Level.UNDERGROUND, Color.YELLOW);
        assertEquals("Giovanni tente de s'emparer du tunnel gnv" + StringsFr.EN_DASH_SEPARATOR + "lsn au moyen de " +
                "3 bleues !\n", info.attemptsTunnelClaim(route, sortedBag));
    }

    @Test
    void attemptsTunnelClaimWork1Card() {
        Info info = new Info("Giovanni");
        SortedBag<Card> sortedBag = SortedBag.of(List.of(Card.BLUE));
        Route route = new Route("1", new Station(1, "gnv"), new Station(2, "lsn"), 3, Route.Level.UNDERGROUND, Color.YELLOW);
        assertEquals("Giovanni tente de s'emparer du tunnel gnv" + StringsFr.EN_DASH_SEPARATOR + "lsn au moyen de " +
                "1 bleue !\n", info.attemptsTunnelClaim(route, sortedBag));
    }

    @Test
    void claimedRouteWork() {
        Info info = new Info("Giovanni");
        SortedBag<Card> sortedBag = SortedBag.of(List.of(Card.BLUE));
        Route route = new Route("1", new Station(1, "gnv"), new Station(2, "lsn"), 3, Route.Level.OVERGROUND, Color.YELLOW);
        assertEquals("Giovanni a pris possession de la route gnv" + StringsFr.EN_DASH_SEPARATOR + "lsn au moyen de " +
                "1 bleue.\n", info.claimedRoute(route, sortedBag));
    }

    @Test
    void getsLongestTrailBonusWork() {
        Info info = new Info("Giovanni");
        Trail trail = Trail.longest(List.of(GEN_BER, GEN_FRI, GEN_LAU, LCN_INT, BER_FRI));
        assertEquals("\nGiovanni reçoit un bonus de 10 points pour le plus long trajet (GEN - LAU).\n" +
                "", info.getsLongestTrailBonus(trail));
    }

    @Test
    void wonWork() {
        Info info = new Info("Giovanni");
        assertEquals("\nGiovanni remporte la victoire avec 10 points, contre 4 points !\n",
                info.won(10, 4));
    }

    @Test
    void wonWorkSingular() {
        Info info = new Info("Giovanni");
        assertEquals("\nGiovanni remporte la victoire avec 1 point, contre 1 point !\n",
                info.won(1, 1));
    }

    @Test
    void wonWorkSingularAndPlural() {
        Info info = new Info("Giovanni");
        assertEquals("\nGiovanni remporte la victoire avec 2 points, contre 1 point !\n",
                info.won(2, 1));
    }

}












