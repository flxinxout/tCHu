package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Classes possédant la totalité des serdes utiles au projet.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class Serdes {

    private Serdes() {
    }

    /**
     * Serde relative aux entiers.
     */
    public static final Serde<Integer> SERDE_OF_INTEGERS = Serde.of(i -> Integer.toString(i), Integer::parseInt);

    /**
     * Serde relative aux chaînes de caractères.
     */
    public static final Serde<String> SERDE_OF_STRINGS = Serde.of(
                    s -> Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)),
                    s -> new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8));

    /**
     * Serde relative aux PlayerId.
     */
    public static final Serde<PlayerId> SERDE_OF_PLAYER_ID = Serde.oneOf(PlayerId.ALL);

    /**
     * Serde relative aux typex de tour de jeu.
     */
    public static final Serde<Player.TurnKind> SERDE_OF_TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Serde relative aux Card.
     */
    public static final Serde<Card> SERDE_OF_CARDS = Serde.oneOf(Card.ALL);

    /**
     * Serde relative aux routes.
     */
    public static final Serde<Route> SERDE_OF_ROUTES = Serde.oneOf(ChMap.routes());

    /**
     * Serde relative aux tickets.
     */
    public static final Serde<Ticket> SERDE_OF_TICKETS = Serde.oneOf(ChMap.tickets());

    /**
     * Serde relative aux listes de chaînes de caractères.
     */
    public static final Serde<List<String>> SERDE_OF_LIST_OF_STRINGS = Serde.listOf(SERDE_OF_STRINGS, ",");

    /**
     * Serde relative aux listes de chaînes de caractères.
     */
    public static final Serde<List<Card>> SERDE_OF_LIST_OF_CARDS = Serde.listOf(SERDE_OF_CARDS, ",");

    /**
     * Serde relative aux listes de routes.
     */
    public static final Serde<List<Route>> SERDE_OF_LIST_OF_ROUTES = Serde.listOf(SERDE_OF_ROUTES, ",");

    /**
     * Serde relative aux sorted bag de Card.
     */
    public static final Serde<SortedBag<Card>> SERDE_OF_SORTEGBAG_OF_CARD = Serde.bagOf(SERDE_OF_CARDS, ",");

    /**
     * Serde relative aux sorted bag de tickets.
     */
    public static final Serde<SortedBag<Ticket>> SERDE_OF_SORTEGBAG_OF_TICKETS = Serde.bagOf(SERDE_OF_TICKETS, ",");

    /**
     * Serde relative aux listes de sorted bag de Card.
     */
    public static final Serde<List<SortedBag<Card>>> SERDE_OF_LIST_OF_SORTEGBAG_OF_CARDS =
            Serde.listOf(Serde.bagOf(SERDE_OF_CARDS, ","), ";");

    /**
     * Serde relative aux PublicCardState.
     */
    public static final Serde<PublicCardState> SERDE_OF_PUBLIC_CARD_STATE =
            Serde.of(makeFunctionPublicCardState(), makeFunctionStringForPublicCardState());

    /**
     * Serde relative aux PublicPlayerState.
     */
    public static final Serde<PublicPlayerState> SERDE_OF_PUBLIC_PLAYER_STATE =
            Serde.of(makeFunctionPublicPlayerState(), makeFunctionStringForPublicPlayerState());

    /**
     * Serde relative aux PlayerState.
     */
    public static final Serde<PlayerState> SERDE_OF_PLAYER_STATE =
            Serde.of(makeFunctionPlayerState(), makeFunctionStringForPlayerState());


    /**
     * Création de la sérialisation d'un PublicCardState
     * @return la sérialisation d'un PublicCardState
     */
    private static Function<PublicCardState, String> makeFunctionPublicCardState() {
        return publicCardState -> {
            StringJoiner joiner = new StringJoiner(";");

            joiner.add(SERDE_OF_LIST_OF_CARDS.serialize(publicCardState.faceUpCards()));
            joiner.add(SERDE_OF_INTEGERS.serialize(publicCardState.deckSize()));
            joiner.add(SERDE_OF_INTEGERS.serialize(publicCardState.discardsSize()));

            return joiner.toString();
        };
    }

    /**
     * Création de la désérialisation d'un PublicCardState
     * @return la désérialisation d'un PublicCardState
     */
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

    /**
     * Création de la sérialisation d'un PublicPlayerState
     * @return la sérialisation d'un PublicPlayerState
     */
    private static Function<PublicPlayerState, String> makeFunctionPublicPlayerState() {
        return publicPlayerState -> {
            StringJoiner joiner = new StringJoiner(";");

            joiner.add(SERDE_OF_INTEGERS.serialize(publicPlayerState.ticketCount()));
            joiner.add(SERDE_OF_INTEGERS.serialize(publicPlayerState.cardCount()));
            joiner.add(SERDE_OF_LIST_OF_ROUTES.serialize(publicPlayerState.routes()));

            return joiner.toString();

        };
    }

    /**
     * Création de la désérialisation d'un PublicPlayerState
     * @return la désérialisation d'un PublicPlayerState
     */
    private static Function<String, PublicPlayerState> makeFunctionStringForPublicPlayerState() {
        return s -> {
            final String[] elements = s.split(Pattern.quote(";"), -1);

            return new PublicPlayerState(SERDE_OF_INTEGERS.deserialize(elements[0]), SERDE_OF_INTEGERS.deserialize(elements[1]),
                    SERDE_OF_LIST_OF_ROUTES.deserialize(elements[2]));
        };
    }

    /**
     * Création de la sérialisation d'un PlayerState
     * @return la sérialisation d'un PlayerState
     */
    private static Function<PlayerState, String> makeFunctionPlayerState() {
        return playerState -> {
            StringJoiner joiner = new StringJoiner(";");

            joiner.add(SERDE_OF_SORTEGBAG_OF_TICKETS.serialize(playerState.tickets()));
            joiner.add(SERDE_OF_SORTEGBAG_OF_CARD.serialize(playerState.cards()));
            joiner.add(SERDE_OF_LIST_OF_ROUTES.serialize(playerState.routes()));

            return joiner.toString();

        };
    }

    /**
     * Création de la désérialisation d'un PlayerState
     * @return la désérialisation d'un PlayerState
     */
    private static Function<String, PlayerState> makeFunctionStringForPlayerState() {
        return s -> {
            final String[] elements = s.split(Pattern.quote(";"), -1);

            return new PlayerState(SERDE_OF_SORTEGBAG_OF_TICKETS.deserialize(elements[0]), SERDE_OF_SORTEGBAG_OF_CARD.deserialize(elements[1]),
                    SERDE_OF_LIST_OF_ROUTES.deserialize(elements[2]));
        };
    }

}
















