package ch.epfl.tchu.net;

import ch.epfl.tchu.game.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class SerdesTest {

    private static final List<Card> FACE_UP_CARDS =
            List.of(Card.BLUE, Card.BLACK, Card.ORANGE, Card.ORANGE, Card.RED);

    @Test
    void publicCardStateSerdeTest() {
        PublicCardState publicCardState = new PublicCardState(FACE_UP_CARDS, 20, 10);
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
                PlayerId.PLAYER_1, new PublicPlayerState(10, 11, rs1),
                PlayerId.PLAYER_2, new PublicPlayerState(20, 21, List.of()));
        PublicGameState gs =
                new PublicGameState(40, cs, PlayerId.PLAYER_2, ps, null);

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

        System.out.println("element 1 de la map: " + e.playerState(e.currentPlayerId().next()).ticketCount() + " ticket, "
                + e.playerState(e.currentPlayerId().next()).cardCount()
                + " cards et " + e.playerState(e.currentPlayerId().next()).routes().size() + " routes");
    }

}














