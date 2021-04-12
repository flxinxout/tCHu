package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

public class Serdes {

    private Serdes() {
    }

    public static final Serde<Integer> SERDE_OF_INTEGERS = Serde.of(i -> Integer.toString(i), Integer::parseInt);

    public static final Serde<String> SERDE_OF_STRINGS = Serde.of(
                    s -> Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)),
                    s -> new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8));

    public static final Serde<PlayerId> SERDE_OF_PLAYER_ID = Serde.oneOf(PlayerId.ALL);

    public static final Serde<Player.TurnKind> SERDE_OF_TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);

    public static final Serde<Card> SERDE_OF_CARDS = Serde.oneOf(Card.ALL);

    public static final Serde<Route> SERDE_OF_ROUTES = Serde.oneOf(ChMap.routes());

    public static final Serde<Ticket> SERDE_OF_TICKETS = Serde.oneOf(ChMap.tickets());

    public static final Serde<List<String>> SERDE_OF_LIST_OF_STRINGS = Serde.listOf(SERDE_OF_STRINGS, ",");

    public static final Serde<List<Card>> SERDE_OF_LIST_OF_CARDS = Serde.listOf(SERDE_OF_CARDS, ",");

    public static final Serde<List<Route>> SERDE_OF_LIST_OF_ROUTES = Serde.listOf(SERDE_OF_ROUTES, ",");

    public static final Serde<SortedBag<Card>> SERDE_OF_SORTEGBAG_OF_CARD = Serde.bagOf(SERDE_OF_CARDS, ", ");

    public static final Serde<SortedBag<Ticket>> SERDE_OF_SORTEGBAG_OF_TICKETS = Serde.bagOf(SERDE_OF_TICKETS, ", ");

    public static final Serde<List<SortedBag<Card>>> SERDE_OF_LIST_OF_SORTEGBAG_OF_CARDS =
            Serde.listOf(Serde.bagOf(SERDE_OF_CARDS, ", "), ", ");

    public static final Serde<PublicCardState> SERDE_OF_PUBLIC_CARD_STATE =
            Serde.of();


}
















