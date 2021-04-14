package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class SerdesTest {

    private static final List<Card> FACE_UP_CARDS =
            List.of(Card.BLUE, Card.BLACK, Card.ORANGE, Card.ORANGE, Card.RED);

    @Test
    void publicCardStateSerdeTest() {
        PublicCardState publicCardState = new PublicCardState(FACE_UP_CARDS, 10, 10);
        String s = Serdes.OF_PUBLIC_CARD_STATE.serialize(publicCardState);
        System.out.println(s);
        PublicCardState t = Serdes.OF_PUBLIC_CARD_STATE.deserialize(s);
        for(Card c : t.faceUpCards()) {
            System.out.print(c.color());
        }
        System.out.println();
        System.out.println("decksuze: " + t.deckSize());
        System.out.println("discard: " + t.discardsSize());
    }

    @Test
    void publicGameStateSerdeTest() {
        List<Card> fu = List.of(Card.RED, Card.WHITE, Card.BLUE, Card.BLACK, Card.RED);
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
        List<Route> rs1 = ChMap.routes().subList(0, 2);

        PlayerState playerState = new PlayerState(SortedBag.of(), SortedBag.of(), rs1);

        String s = Serdes.OF_PLAYER_STATE.serialize(playerState);
        System.out.println(s);
        System.out.println("");

        PlayerState e = Serdes.OF_PLAYER_STATE.deserialize(s);

        System.out.println("tickets size: " + e.tickets().size());
        for(Ticket ticket : e.tickets()) {
            System.out.print(ticket.text());
        };
        System.out.println("");

        System.out.println("size of cards: " + e.cards().size());
        for(Card card : e.cards()) {
            System.out.print(card.color());
        }

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













