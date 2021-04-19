package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerdesTest {

    private static final Serde<Integer> intSerde = Serdes.OF_INTEGERS;
    private static final Serde<String> stringSerde = Serdes.OF_STRINGS;
    private static final Serde<PlayerId> playerIdSerde = Serdes.OF_PLAYER_ID;
    private static final Serde<Player.TurnKind> turnKindSerde = Serdes.OF_TURN_KIND;
    private static final Serde<PublicCardState> publicCardStateSerde = Serdes.OF_PUBLIC_CARD_STATE;

    private static final List<Card> FACE_UP_CARDS =
            List.of(Card.BLUE, Card.BLACK, Card.ORANGE, Card.LOCOMOTIVE, Card.RED);

    @Test
    void integerSerdeTestWorks(){
        for (int i = 0; i < 1000; i++) {
            String ser = intSerde.serialize(i);
            assertEquals(String.format(String.valueOf(i)), ser);
            int in = intSerde.deserialize(ser);
            assertEquals(i, in);
        }
    }

    @Test
    void stringSerdeTestWorks(){
        String ser = stringSerde.serialize("Charles");
        assertEquals("Q2hhcmxlcw==", ser);
        String des = stringSerde.deserialize(ser);
        assertEquals("Charles", des);
    }

    @Test
    void oneOfWorks(){
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i <= 1000; i++)
            integers.add(i);

        Serde<Integer> serde = Serde.oneOf(integers);
        for (int i = 999; i >= 0; i--) {
            String ser = serde.serialize(i);
            assertEquals(String.valueOf(integers.indexOf(i)), ser);
            int des = serde.deserialize(ser);
            assertEquals(i, des);
        }
    }

    @Test
    void listOfWorks(){
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i <= 10; i++)
            integers.add(i);

        Serde<List<Integer>> serde = Serde.listOf(intSerde, ',');

        String ser = serde.serialize(integers);
        StringJoiner expected = new StringJoiner(",");
        for (int i = 0; i <= 10 ; i++)
            expected.add(String.valueOf(i));

        assertEquals(expected.toString(), ser);
        List<Integer> des = serde.deserialize(ser);
        assertEquals(integers, des);
    }

    @Test
    void publicCardStateSerdeTest() {
        PublicCardState publicCardState = new PublicCardState(FACE_UP_CARDS, 10, 10);
        String s = publicCardStateSerde.serialize(publicCardState);
        assertEquals("2,0,5,8,6;10;10", s);
        PublicCardState t = publicCardStateSerde.deserialize(s);
        assertEquals(10, t.deckSize());
        assertEquals(10, t.discardsSize());
    }

    @Test
    void publicGameStateSerdeTest() {
        List<Card> fu = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.LOCOMOTIVE);
        PublicCardState cs = new PublicCardState(fu, 30, 31);
        List<Route> rs1 = ChMap.routes().subList(0, 2);
        Map<PlayerId, PublicPlayerState> ps = Map.of(
                PlayerId.PLAYER_1, new PublicPlayerState(0, 0, rs1),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PlayerId.PLAYER_2, ps, PlayerId.PLAYER_1);

        String s = Serdes.OF_PUBLIC_GAME_STATE.serialize(gs);
        System.out.println(s);
        System.out.println("");

        PublicGameState e = Serdes.OF_PUBLIC_GAME_STATE.deserialize(s);

        System.out.println("tickets: " + e.ticketsCount());

        for(Card card : e.cardState().faceUpCards()) {
            System.out.print(card.color());
        }
        System.out.println();
        System.out.println("decksize: " + e.cardState().deckSize());
        System.out.println("discardsize: " + e.cardState().discardsSize());

        System.out.println("current player: " + e.currentPlayerId());
        System.out.println("lastplayer: " + e.lastPlayer());

        System.out.println("element 1 de la map: " + e.currentPlayerState().ticketCount() + " ticket, "
        + e.currentPlayerState().cardCount() + " cards et " + e.currentPlayerState().routes().size() + " routes");

        System.out.println("element 2 de la map: " + e.playerState(e.currentPlayerId().next()).ticketCount() + " ticket, "
                + e.playerState(e.currentPlayerId().next()).cardCount()
                + " cards et " + e.playerState(e.currentPlayerId().next()).routes().size() + " routes");
    }

    @Test
    void playerStateSerdeTest() {
        List<Route> rs1 = ChMap.routes().subList(10, 15);

        PlayerState playerState = new PlayerState(SortedBag.of(ChMap.tickets().get(2)),
                SortedBag.of(2, Card.LOCOMOTIVE, 5, Card.BLACK), rs1);

        String s = Serdes.OF_PLAYER_STATE.serialize(playerState);
        System.out.println(s);
        System.out.println();

        PlayerState e = Serdes.OF_PLAYER_STATE.deserialize(s);

        System.out.println("tickets size: " + e.tickets().size());
        for(Ticket ticket : e.tickets()) {
            System.out.print(ticket.text());
        }
        System.out.println();
        System.out.println();

        System.out.println("size of cards: " + e.cards().size());
        for(Card card : e.cards()) {
            System.out.print(card.color());
        }
        System.out.println();
        System.out.println();

        System.out.println("size of routes: " + e.routes().size());
        for(Route r : e.routes()) {
            System.out.print(r.length() + " ");
        }
    }

    @Test
    void publicPlayerStateSerdeTest() {
        List<Route> rs1 = ChMap.routes().subList(0, 2);

        PublicPlayerState publicPlayerState = new PublicPlayerState(20, 20, List.of());

        String s = Serdes.OF_PUBLIC_PLAYER_STATE.serialize(publicPlayerState);
        System.out.println(s);
        System.out.println("");

        PublicPlayerState e = Serdes.OF_PUBLIC_PLAYER_STATE.deserialize(s);

        System.out.println("tickets size: " + e.ticketCount());

        System.out.println("size of cards: " + e.cardCount());

        System.out.println("size of routes: " + e.routes().size());
    }

}














