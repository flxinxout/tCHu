package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Classes possédant la totalité des serdes utiles au projet.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 * @see Serde
 */
public class Serdes {

    private Serdes() {
    }

    /**
     * Serde relatif aux entiers.
     */
    public static final Serde<Integer> OF_INTEGERS = Serde.of(i -> Integer.toString(i), Integer::parseInt);

    /**
     * Serde relatif aux chaînes de caractères.
     */
    public static final Serde<String> OF_STRINGS = Serde.of(
                    s -> Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)),
                    s -> new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8));

    /**
     * Serde relatif aux {@code PlayerId}.
     */
    public static final Serde<PlayerId> OF_PLAYER_ID = Serde.oneOf(PlayerId.ALL);

    /**
     * Serde relatif aux {@code TurnKind}.
     */
    public static final Serde<Player.TurnKind> OF_TURN_KIND = Serde.oneOf(Player.TurnKind.ALL);

    /**
     * Serde relatif aux {@code Card}.
     */
    public static final Serde<Card> OF_CARDS = Serde.oneOf(Card.ALL);

    /**
     * Serde relatif aux {@code Route}.
     */
    public static final Serde<Route> OF_ROUTES = Serde.oneOf(ChMap.routes());

    /**
     * Serde relatif aux {@code Ticket}.
     */
    public static final Serde<Ticket> OF_TICKETS = Serde.oneOf(ChMap.tickets());

    /**
     * Serde relatif aux listes de chaînes de caractères.
     */
    public static final Serde<List<String>> OF_LIST_OF_STRINGS = Serde.listOf(Serdes.OF_STRINGS,',');

    /**
     * Serde relatif aux listes de {@code Card}.
     */
    public static final Serde<List<Card>> OF_LIST_OF_CARDS = Serde.listOf(Serdes.OF_CARDS, ',');

    /**
     * Serde relatif aux listes de {@code Route}.
     */
    public static final Serde<List<Route>> OF_LIST_OF_ROUTES = Serde.listOf(Serdes.OF_ROUTES, ',');

    /**
     * Serde relatif aux {@code SortedBag} de {@code Card}.
     */
    public static final Serde<SortedBag<Card>> OF_SORTEDBAG_OF_CARD = Serde.bagOf(Serdes.OF_CARDS, ',');

    /**
     * Serde relatif aux {@code SortedBag} de {@code Ticket}.
     */
    public static final Serde<SortedBag<Ticket>> OF_SORTEDBAG_OF_TICKETS = Serde.bagOf(Serdes.OF_TICKETS, ',');

    /**
     * Serde relatif aux listes de {@code SortedBag} de {@code Card}.
     */
    public static final Serde<List<SortedBag<Card>>> OF_LIST_OF_SORTEDBAG_OF_CARDS =
            Serde.listOf(Serde.bagOf(Serdes.OF_CARDS, ','), ';');

    /**
     * Serde relatif aux {@code PublicCardState}.
     */
    public static final Serde<PublicCardState> OF_PUBLIC_CARD_STATE =
            Serde.of(serFuncPublicCardState(), deserFuncPublicCardState());

    /**
     * Serde relatif aux {@code PublicPlayerState}.
     */
    public static final Serde<PublicPlayerState> OF_PUBLIC_PLAYER_STATE =
            Serde.of(serFuncPublicPlayerState(), deserFuncPublicPlayerState());

    /**
     * Serde relatif aux {@code PlayerState}.
     */
    public static final Serde<PlayerState> OF_PLAYER_STATE =
            Serde.of(serFuncPlayerState(), deserFuncPlayerState());

    /**
     * Serde relatif aux {@code PublicGameState}.
     */
    public static final Serde<PublicGameState> OF_PUBLIC_GAME_STATE =
            Serde.of(serFuncPublicGameState(), deserFuncPublicGameState());

    /**
     * Retourne la fonction de sérialisation d'un {@code PublicCardState}.
     * @return la fonction de sérialisation d'un {@code PublicCardState}
     */
    private static Function<PublicCardState, String> serFuncPublicCardState() {
        return publicCardState -> {
            final StringJoiner joiner = new StringJoiner(";");

            joiner.add(Serdes.OF_LIST_OF_CARDS.serialize(publicCardState.faceUpCards()));
            joiner.add(Serdes.OF_INTEGERS.serialize(publicCardState.deckSize()));
            joiner.add(Serdes.OF_INTEGERS.serialize(publicCardState.discardsSize()));

            return joiner.toString();
        };
    }

    /**
     * Retourne la fonction de désérialisation d'un {@code PublicCardState}.
     * @return la fonction de désérialisation d'un {@code PublicCardState}
     */
    private static Function<String, PublicCardState> deserFuncPublicCardState() {
        return s -> {
            final String[] elements = s.split(Pattern.quote(";"), -1);

            System.out.println(Serdes.OF_LIST_OF_CARDS.deserialize(elements[0]));
            System.out.println(Serdes.OF_INTEGERS.deserialize(elements[1]));
            System.out.println(Serdes.OF_INTEGERS.deserialize(elements[2]));

            return new PublicCardState(Serdes.OF_LIST_OF_CARDS.deserialize(elements[0]),
                    Serdes.OF_INTEGERS.deserialize(elements[1]),
                    Serdes.OF_INTEGERS.deserialize(elements[2]));
        };
    }

    /**
     * Retourne la fonction de sérialisation d'un {@code PublicPlayerState}.
     * @return la fonction de sérialisation d'un {@code PublicPlayerState}
     */
    private static Function<PublicPlayerState, String> serFuncPublicPlayerState() {
        return publicPlayerState -> {
            StringJoiner joiner = new StringJoiner(";");

            joiner.add(Serdes.OF_INTEGERS.serialize(publicPlayerState.ticketCount()));
            joiner.add(Serdes.OF_INTEGERS.serialize(publicPlayerState.cardCount()));
            joiner.add(Serdes.OF_LIST_OF_ROUTES.serialize(publicPlayerState.routes()));

            return joiner.toString();
        };
    }

    /**
     * Retourne la fonction de désérialisation d'un {@code PublicPlayerState}.
     * @return la fonction de désérialisation d'un {@code PublicPlayerState}
     */
    private static Function<String, PublicPlayerState> deserFuncPublicPlayerState() {
        return s -> {
            final String[] elements = s.split(Pattern.quote(";"), -1);

            return new PublicPlayerState(Serdes.OF_INTEGERS.deserialize(elements[0]),
                    Serdes.OF_INTEGERS.deserialize(elements[1]),
                    Serdes.OF_LIST_OF_ROUTES.deserialize(elements[2]));
        };
    }

    /**
     * Retourne la fonction de sérialisation d'un {@code PlayerState}.
     * @return la fonction de sérialisation d'un {@code PlayerState}
     */
    private static Function<PlayerState, String> serFuncPlayerState() {
        return playerState -> {
            StringJoiner joiner = new StringJoiner(";");

            joiner.add(Serdes.OF_SORTEDBAG_OF_TICKETS.serialize(playerState.tickets()));
            joiner.add(Serdes.OF_SORTEDBAG_OF_CARD.serialize(playerState.cards()));
            joiner.add(Serdes.OF_LIST_OF_ROUTES.serialize(playerState.routes()));

            return joiner.toString();

        };
    }

    /**
     * Retourne la fonction de désérialisation d'un {@code PlayerState}.
     * @return la fonction de désérialisation d'un {@code PlayerState}
     */
    private static Function<String, PlayerState> deserFuncPlayerState() {
        return s -> {
            final String[] elements = s.split(Pattern.quote(";"), -1);

            return new PlayerState(Serdes.OF_SORTEDBAG_OF_TICKETS.deserialize(elements[0]),
                    Serdes.OF_SORTEDBAG_OF_CARD.deserialize(elements[1]),
                    Serdes.OF_LIST_OF_ROUTES.deserialize(elements[2]));
        };
    }

    /**
     * Retourne la fonction de  sérialisation d'un {@code PublicGameState}.
     * @return la fonction de  sérialisation d'un {@code PublicGameState}
     */
    private static Function<PublicGameState, String> serFuncPublicGameState() {
        return publicGameState -> {
            StringJoiner joiner = new StringJoiner(":");

            joiner.add(Serdes.OF_INTEGERS.serialize(publicGameState.ticketsCount()));
            joiner.add(Serdes.OF_PUBLIC_CARD_STATE.serialize(publicGameState.cardState()));
            joiner.add(Serdes.OF_PLAYER_ID.serialize(publicGameState.currentPlayerId()));
            joiner.add(Serdes.OF_PUBLIC_PLAYER_STATE.serialize(publicGameState.playerState(PlayerId.PLAYER_1)));
            joiner.add(Serdes.OF_PUBLIC_PLAYER_STATE.serialize(publicGameState.playerState(PlayerId.PLAYER_2)));

            if(publicGameState.lastPlayer() == null)
                joiner.add("");
            else
                joiner.add(Serdes.OF_PLAYER_ID.serialize(publicGameState.lastPlayer()));

            return joiner.toString();
        };
    }

    /**
     * Retourne la fonction de désérialisation d'un {@code PublicGameState}.
     * @return la fonction de désérialisation d'un {@code PublicGameState}
     */
    private static Function<String, PublicGameState> deserFuncPublicGameState() {
        return s -> {
            final String[] elements = s.split(Pattern.quote(":"), -1);
            //40:6,7,2,0,6;30;31:1:10;11;0,1:20;21;: chaine vide à la fin
            //40   6,7,0,6;30;31   1   10;11;0,1   20;21:""  ""
            final Map<PlayerId, PublicPlayerState> playerState = Map.of(
                    PlayerId.PLAYER_1, Serdes.OF_PUBLIC_PLAYER_STATE.deserialize(elements[3]),
                    PlayerId.PLAYER_2, Serdes.OF_PUBLIC_PLAYER_STATE.deserialize(elements[4]));

            return new PublicGameState(
                    Serdes.OF_INTEGERS.deserialize(elements[0]),
                    Serdes.OF_PUBLIC_CARD_STATE.deserialize(elements[1]),
                    Serdes.OF_PLAYER_ID.deserialize(elements[2]),
                    playerState,
                    elements[5].isEmpty() ? null : Serdes.OF_PLAYER_ID.deserialize(elements[5]));
        };
    }
}
















