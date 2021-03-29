package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Une route non-orientée reliant deux villes voisines.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Route {

    /**
     * Énumération des deux niveaux possibles pour une route
     */
    public enum Level {
        OVERGROUND,
        UNDERGROUND
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Construit une route avec l'identité, les gares, la longueur,
     * le niveau et la couleur donnée.
     *
     * @param id       l'identité de la route (non null)
     * @param station1 la première gare (non null)
     * @param station2 la deuxième gare (non null)
     * @param length   la longueur de la route
     * @param level    le niveau de la route (non null)
     * @param color    la couleur de la route (peut être nulle)
     * @throws IllegalArgumentException si {@code station1} = {@code station2} ou
     *                                  si la longueur n'est pas comprise dans les limites acceptables
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!station1.equals(station2)
                && length >= Constants.MIN_ROUTE_LENGTH
                && length <= Constants.MAX_ROUTE_LENGTH);

        this.id = Objects.requireNonNull(id);
        this.station1 = Objects.requireNonNull(station1);
        this.station2 = Objects.requireNonNull(station2);
        this.level = Objects.requireNonNull(level);
        this.length = length;
        this.color = color;
    }

    /**
     * @return l'identité de cette route
     */
    public String id() {
        return id;
    }

    /**
     * @return la première gare de cette route
     */
    public Station station1() {
        return station1;
    }

    /**
     * @return la deuxième gare de cette route
     */
    public Station station2() {
        return station2;
    }

    /**
     * @return la longueur de cette route
     */
    public int length() {
        return length;
    }

    /**
     * @return le niveau de cette route
     */
    public Level level() {
        return level;
    }

    /**
     * @return la couleur de cette route
     */
    public Color color() {
        return color;
    }

    /**
     * Retourne la liste des deux gares de cette route,
     * dans l'ordre dans lequel elles ont été passées au constructeur.
     *
     * @return la liste des deux gares de cette route
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Retourne la gare de la route qui n'est pas {@code station}.
     *
     * @param station gare à ne pas retourner
     * @return la gare de la route qui n'est pas {@code station}
     * @throws IllegalArgumentException si {@code station} n'est égale à aucune des deux gares de cette route
     */
    public Station stationOpposite(Station station) {
        Preconditions.checkArgument(station1.equals(station) || station2.equals(station));
        return station1.equals(station) ? station2 : station1;
    }

    /**
     * Retourne la liste de tous les ensembles de cartes qui pourraient être joués pour (tenter de)
     * s'emparer de la route, triée par ordre croissant de nombre de cartes locomotive, puis par couleur.
     *
     * @return la liste de tous les ensembles de cartes qui pourraient être joués
     * pour s'emparer de la route
     */
    public List<SortedBag<Card>> possibleClaimCards() {
        final List<SortedBag<Card>> possibleClaimCardsList = new ArrayList<>();

        //Si la route n'est pas de couleur neutre
        if (color != null) {
            possibleClaimCardsList.add(SortedBag.of(length, Card.of(color)));

            //Si la route est un tunnel
            if (level == Level.UNDERGROUND) {
                for (int i = 1; i <= length; i++) {
                    SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();

                    cardsBuilder.add(length - i, Card.of(color));
                    cardsBuilder.add(i, Card.LOCOMOTIVE);
                    possibleClaimCardsList.add(cardsBuilder.build());
                }
            }
        }
        //Si la route est de couleur neutre
        else {
            Card.CARS.forEach(car -> possibleClaimCardsList.add(SortedBag.of(length, car)));

            //Si la route est un tunnel
            if (level == Level.UNDERGROUND) {
                for (int i = 1; i < length; i++) {
                    //TODO: lambda or foreach?
                    for (Card car : Card.CARS) {
                        SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();
                        cardsBuilder.add(length - i, car);
                        cardsBuilder.add(i, Card.LOCOMOTIVE);
                        possibleClaimCardsList.add(cardsBuilder.build());
                    }
                }

                possibleClaimCardsList.add(SortedBag.of(length, Card.LOCOMOTIVE));
            }
        }

        return possibleClaimCardsList;
    }

    /**
     * Retourne le nombre de cartes additionnelles à jouer pour s'emparer de la route (en tunnel).
     *
     * @param claimCards cartes initialement posées par le joueur
     * @param drawnCards cartes tirées du sommet de la pioche
     * @return le nombre de cartes additionnelles à jouer pour s'emparer de la route
     * @throws IllegalArgumentException si {@code this} n'est pas un tunnel ou
     *                                  si {@code drawnCards} ne contient pas exactement 3 cartes
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(level == Level.UNDERGROUND
                && drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        int additionalCards;
        Color claimColor = null;

        if (!claimCards.isEmpty())
            claimColor = claimCards.get(0).color();

        //TODO: stream or foreach below
        Color finalClaimColor = claimColor;
        additionalCards = (int) drawnCards.stream()
                .filter(card -> card == Card.LOCOMOTIVE || card.color() == finalClaimColor)
                .count();

        /*for (Card card : drawnCards) {
            if (card == Card.LOCOMOTIVE)
                additionalCards++;
            else {
                if (card.color() == claimColor)
                    additionalCards++;
            }
        }*/

        return additionalCards;

    }

    /**
     * @return le nombre de points de construction de cette route
     */
    public int claimPoints() {
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }
}
