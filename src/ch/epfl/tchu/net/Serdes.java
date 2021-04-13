package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

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

    public static final Serde<SortedBag<Card>> SERDE_OF_SORTEGBAG_OF_CARD = Serde.bagOf(SERDE_OF_CARDS, ",");

    public static final Serde<SortedBag<Ticket>> SERDE_OF_SORTEGBAG_OF_TICKETS = Serde.bagOf(SERDE_OF_TICKETS, ",");

    public static final Serde<List<SortedBag<Card>>> SERDE_OF_LIST_OF_SORTEGBAG_OF_CARDS =
            Serde.listOf(Serde.bagOf(SERDE_OF_CARDS, ","), ",");

    public static final Serde<PublicCardState> SERDE_OF_PUBLIC_CARD_STATE =
            Serde.of(makeFunctionPublicCardState(), makeFunctionStringForPublicCardState());

    public static final Serde<PublicPlayerState> SERDE_OF_PUBLIC_PLAYER_STATE =
            Serde.of(makeFunctionPublicPlayerState(), makeFunctionStringForPublicPlayerState());

    public static final Serde<PlayerState> SERDE_OF_PLAYER_STATE =
            Serde.of(makeFunctionPlayerState(), makeFunctionStringForPlayerState());



    private static Function<PublicCardState, String> makeFunctionPublicCardState() {
        return publicCardState -> {
            StringJoiner joiner = new StringJoiner(";");

            joiner.add(SERDE_OF_LIST_OF_CARDS.serialize(publicCardState.faceUpCards()));
            joiner.add(SERDE_OF_INTEGERS.serialize(publicCardState.deckSize()));
            joiner.add(SERDE_OF_INTEGERS.serialize(publicCardState.discardsSize()));

            return joiner.toString();
        };
    }

    private static Function<String, PublicCardState> makeFunctionStringForPublicCardState() {
        return s -> {
            final String[] elements = s.split(Pattern.quote(";"), -1);

            System.out.println(SERDE_OF_LIST_OF_CARDS.deserialize(elements[0]));
            System.out.println(SERDE_OF_INTEGERS.deserialize(elements[1]));
            System.out.println(SERDE_OF_INTEGERS.deserialize(elements[2]));

            return new PublicCardState(SERDE_OF_LIST_OF_CARDS.deserialize(elements[0]), SERDE_OF_INTEGERS.deserialize(elements[1]),
                    SERDE_OF_INTEGERS.deserialize(elements[2]));
        };
    }

    private static Function<PublicPlayerState, String> makeFunctionPublicPlayerState() {
        return publicPlayerState -> {
            StringJoiner joiner = new StringJoiner(";");

            joiner.add(SERDE_OF_INTEGERS.serialize(publicPlayerState.ticketCount()));
            joiner.add(SERDE_OF_INTEGERS.serialize(publicPlayerState.cardCount()));
            joiner.add(SERDE_OF_LIST_OF_ROUTES.serialize(publicPlayerState.routes()));

            return joiner.toString();

        };
    }

    private static Function<String, PublicPlayerState> makeFunctionStringForPublicPlayerState() {
        return s -> {
            final String[] elements = s.split(Pattern.quote(";"), -1);

            return new PublicPlayerState(SERDE_OF_INTEGERS.deserialize(elements[0]), SERDE_OF_INTEGERS.deserialize(elements[1]),
                    SERDE_OF_LIST_OF_ROUTES.deserialize(elements[2]));
        };
    }

    private static Function<PlayerState, String> makeFunctionPlayerState() {
        return playerState -> {
            StringJoiner joiner = new StringJoiner(";");

            joiner.add(SERDE_OF_SORTEGBAG_OF_TICKETS.serialize(playerState.tickets()));
            joiner.add(SERDE_OF_SORTEGBAG_OF_CARD.serialize(playerState.cards()));
            joiner.add(SERDE_OF_LIST_OF_ROUTES.serialize(playerState.routes()));

            return joiner.toString();

        };
    }

    private static Function<String, PlayerState> makeFunctionStringForPlayerState() {
        return s -> {
            final String[] elements = s.split(Pattern.quote(";"), -1);

            return new PlayerState(SERDE_OF_SORTEGBAG_OF_TICKETS.deserialize(elements[0]), SERDE_OF_SORTEGBAG_OF_CARD.deserialize(elements[1]),
                    SERDE_OF_LIST_OF_ROUTES.deserialize(elements[2]));
        };
    }

}
















