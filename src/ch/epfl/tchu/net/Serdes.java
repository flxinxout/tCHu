package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

import static ch.epfl.tchu.game.Constants.*;
import static ch.epfl.tchu.game.Player.*;

/**
 * Contient la totalité des {@code Serdes} utiles au jeu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 * @see Serde
 */
public final class Serdes {

    /** Séparateur des éléments d'une collection */
    private static final char COLL_DELIM = ',';
    /** Séparateur des éléments d'une collection de collections */
    private static final char COLL_DELIM_DEG2 = ';';
    /** Séparateur des éléments d'une collection de collections de collections */
    private static final char COLL_DELIM_DEG3 = ':';

    /**
     * {@code Serde} relatif aux entiers.
     */
    public static final Serde<Integer> OF_INTEGER = Serde.of(i -> Integer.toString(i), Integer::parseInt);

    /**
     * {@code Serde} relatif aux chaînes de caractères.
     */
    public static final Serde<String> OF_STRING = Serde.of(
            s -> Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8)),
            s -> new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8));

    /**
     * {@code Serde} relatif aux {@code PlayerId}.
     */
    public static final Serde<PlayerId> OF_PLAYER_ID = Serde.oneOf(PlayerId.ALL);

    /**
     * {@code Serde} relatif aux types de tour de jeu.
     */
    public static final Serde<TurnKind> OF_TURN_KIND = Serde.oneOf(TurnKind.ALL);

    /**
     * {@code Serde} relatif aux cartes.
     */
    public static final Serde<Card> OF_CARD = Serde.oneOf(Card.ALL);

    /**
     * {@code Serde} relatif aux routes.
     */
    public static final Serde<Route> OF_ROUTE = Serde.oneOf(ChMap.routes());

    /**
     * {@code Serde} relatif aux billets.
     */
    public static final Serde<Ticket> OF_TICKET = Serde.oneOf(ChMap.tickets());

    /**
     * {@code Serde} relatif aux listes de chaînes de caractères.
     */
    public static final Serde<List<String>> OF_LIST_OF_STRINGS = Serde.listOf(Serdes.OF_STRING, COLL_DELIM);

    /**
     * {@code Serde} relatif aux listes de cartes.
     */
    public static final Serde<List<Card>> OF_LIST_OF_CARDS = Serde.listOf(Serdes.OF_CARD, COLL_DELIM);

    /**
     * {@code Serde} relatif aux listes de routes.
     */
    public static final Serde<List<Route>> OF_LIST_OF_ROUTES = Serde.listOf(Serdes.OF_ROUTE, COLL_DELIM);

    /**
     * {@code Serde} relatif aux multi-ensembles de cartes.
     */
    public static final Serde<SortedBag<Card>> OF_SORTED_BAG_OF_CARD = Serde.bagOf(Serdes.OF_CARD, COLL_DELIM);

    /**
     * {@code Serde} relatif aux multi-ensembles de billets.
     */
    public static final Serde<SortedBag<Ticket>> OF_SORTED_BAG_OF_TICKETS = Serde.bagOf(Serdes.OF_TICKET, COLL_DELIM);

    /**
     * {@code Serde} relatif aux listes de multi-ensembles de cartes.
     */
    public static final Serde<List<SortedBag<Card>>> OF_LIST_OF_SORTED_BAGS_OF_CARDS =
            Serde.listOf(OF_SORTED_BAG_OF_CARD, COLL_DELIM_DEG2);

    /**
     * {@code Serde} relatif aux {@code PublicCardState}.
     */
    public static final Serde<PublicCardState> OF_PUBLIC_CARD_STATE =
            Serde.of(serFuncPublicCardState(), deserFuncPublicCardState());

    /**
     * {@code Serde} relatif aux {@code PublicPlayerState}.
     */
    public static Serde<PublicPlayerState> ofPublicPlayerState(int initialCarCount){
            return Serde.of(serFuncPublicPlayerState(initialCarCount), deserFuncPublicPlayerState());
    }

    /**
     * {@code Serde} relatif aux {@code PlayerState}.
     */
    public static Serde<PlayerState> ofPlayerState(int initialCarCount) {
        return Serde.of(serFuncPlayerState(initialCarCount), deserFuncPlayerState());
    }

    /**
     * {@code Serde} relatif aux {@code PublicGameState}.
     */
    public static final Serde<PublicGameState> OF_PUBLIC_GAME_STATE =
            Serde.of(serFuncPublicGameState(), deserFuncPublicGameState());

    private Serdes() {
    }

    /**
     * Crée la fonction de sérialisation d'un {@code PublicCardState}.
     *
     * @return la fonction de sérialisation d'un {@code PublicCardState}
     */
    private static Function<PublicCardState, String> serFuncPublicCardState() {
        return pcs -> {
            StringJoiner joiner = new StringJoiner(String.valueOf(COLL_DELIM_DEG2));
            joiner.add(Serdes.OF_LIST_OF_CARDS.serialize(pcs.faceUpCards()));
            joiner.add(Serdes.OF_INTEGER.serialize(pcs.deckSize()));
            joiner.add(Serdes.OF_INTEGER.serialize(pcs.discardsSize()));
            return joiner.toString();
        };
    }

    /**
     * Crée la fonction de désérialisation d'un {@code PublicCardState}.
     *
     * @return la fonction de désérialisation d'un {@code PublicCardState}
     */
    private static Function<String, PublicCardState> deserFuncPublicCardState() {
        return s -> {
            String[] elements = s.split(Pattern.quote(String.valueOf(COLL_DELIM_DEG2)), -1);
            return new PublicCardState(Serdes.OF_LIST_OF_CARDS.deserialize(elements[0]),
                    Serdes.OF_INTEGER.deserialize(elements[1]),
                    Serdes.OF_INTEGER.deserialize(elements[2]));
        };
    }

    /**
     * Crée la fonction de sérialisation d'un {@code PublicPlayerState}.
     *
     * @return la fonction de sérialisation d'un {@code PublicPlayerState}
     */
    private static Function<PublicPlayerState, String> serFuncPublicPlayerState(int initialCarCount) {
        return publicPlayerState -> {
            StringJoiner joiner = new StringJoiner(String.valueOf(COLL_DELIM_DEG2));
            joiner.add(Serdes.OF_INTEGER.serialize(initialCarCount));
            joiner.add(Serdes.OF_INTEGER.serialize(publicPlayerState.ticketCount()));
            joiner.add(Serdes.OF_INTEGER.serialize(publicPlayerState.cardCount()));
            joiner.add(Serdes.OF_LIST_OF_ROUTES.serialize(publicPlayerState.routes()));

            return joiner.toString();
        };
    }

    /**
     * Crée la fonction de désérialisation d'un {@code PublicPlayerState}.
     *
     * @return la fonction de désérialisation d'un {@code PublicPlayerState}
     */
    private static Function<String, PublicPlayerState> deserFuncPublicPlayerState() {
        return s -> {
            String[] elements = s.split(Pattern.quote(String.valueOf(COLL_DELIM_DEG2)), -1);
            return new PublicPlayerState(Serdes.OF_INTEGER.deserialize(elements[0]),
                    Serdes.OF_INTEGER.deserialize(elements[1]),
                    Serdes.OF_INTEGER.deserialize(elements[2]),
                    Serdes.OF_LIST_OF_ROUTES.deserialize(elements[3]));
        };
    }

    /**
     * Crée la fonction de sérialisation d'un {@code PlayerState}.
     *
     * @return la fonction de sérialisation d'un {@code PlayerState}
     */
    private static Function<PlayerState, String> serFuncPlayerState(int initialCarCount) {
        return playerState -> {
            StringJoiner joiner = new StringJoiner(String.valueOf(COLL_DELIM_DEG2));
            joiner.add(Serdes.OF_INTEGER.serialize(initialCarCount));
            joiner.add(Serdes.OF_SORTED_BAG_OF_TICKETS.serialize(playerState.tickets()));
            joiner.add(Serdes.OF_SORTED_BAG_OF_CARD.serialize(playerState.cards()));
            joiner.add(Serdes.OF_LIST_OF_ROUTES.serialize(playerState.routes()));
            return joiner.toString();

        };
    }

    /**
     * Retourne la fonction de désérialisation d'un {@code PlayerState}.
     *
     * @return la fonction de désérialisation d'un {@code PlayerState}
     */
    private static Function<String, PlayerState> deserFuncPlayerState() {
        return s -> {
            String[] elements = s.split(Pattern.quote(String.valueOf(COLL_DELIM_DEG2)), -1);
            return new PlayerState(Serdes.OF_INTEGER.deserialize(elements[0]),
                    Serdes.OF_SORTED_BAG_OF_TICKETS.deserialize(elements[1]),
                    Serdes.OF_SORTED_BAG_OF_CARD.deserialize(elements[2]),
                    Serdes.OF_LIST_OF_ROUTES.deserialize(elements[3]));
        };
    }

    /**
     * Crée la fonction de sérialisation d'un {@code PublicGameState}.
     *
     * @return la fonction de sérialisation d'un {@code PublicGameState}
     */
    private static Function<PublicGameState, String> serFuncPublicGameState() {
        return publicGameState -> {
            StringJoiner joiner = new StringJoiner(String.valueOf(COLL_DELIM_DEG3));
            joiner.add(Serdes.OF_INTEGER.serialize(publicGameState.ticketsCount()));
            joiner.add(Serdes.OF_PUBLIC_CARD_STATE.serialize(publicGameState.cardState()));
            joiner.add(Serdes.OF_PLAYER_ID.serialize(publicGameState.currentPlayerId()));
            int playerNb = (int)PlayerId.ALL.stream()
                    .filter(id -> publicGameState.playerState(id) != null)
                    .count();
            int initialCarCount = INITIAL_CAR_COUNT - 10 * playerNb;

            for (PlayerId id : PlayerId.ALL) {
                PublicPlayerState playerState = publicGameState.playerState(id);
                joiner.add(playerState == null ? "" : Serdes.ofPublicPlayerState(initialCarCount).serialize(playerState));
            }
            joiner.add(publicGameState.lastPlayer() == null ?
                    "" : Serdes.OF_PLAYER_ID.serialize(publicGameState.lastPlayer()));
            return joiner.toString();
        };
    }

    /**
     * Crée la fonction de désérialisation d'un {@code PublicGameState}.
     *
     * @return la fonction de désérialisation d'un {@code PublicGameState}
     */
    private static Function<String, PublicGameState> deserFuncPublicGameState() {
        return s -> {
            String[] elements = s.split(Pattern.quote(String.valueOf(COLL_DELIM_DEG3)), -1);
            Map<PlayerId, PublicPlayerState> playerState = new EnumMap<>(PlayerId.class);
            int playerStateStartsAt = 3;
            int playerNb = 0;
            for (int i = 0; i < PlayerId.COUNT; i++) {
                String e = elements[i + playerStateStartsAt];
                if (!e.isEmpty())
                    playerNb += 1;
            }
            int initialCarCount = INITIAL_CAR_COUNT - 10 * playerNb;
            for (int i = 0; i < PlayerId.COUNT; i++) {
                String e = elements[i + playerStateStartsAt];
                if (!e.isEmpty())
                    playerState.put(PlayerId.ALL.get(i), Serdes.ofPublicPlayerState(initialCarCount).deserialize(e));
            }
            return new PublicGameState(
                    Serdes.OF_INTEGER.deserialize(elements[0]),
                    Serdes.OF_PUBLIC_CARD_STATE.deserialize(elements[1]),
                    Serdes.OF_PLAYER_ID.deserialize(elements[2]),
                    playerState,
                    elements[7].isEmpty() ? null : Serdes.OF_PLAYER_ID.deserialize(elements[7]));
        };
    }
}
















