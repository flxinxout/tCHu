package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Un trajet (orienté).
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Construit un nouveau trajet entre {@code s1} et {@code s2} et valant {@code points} points.
     *
     * @param from   la gare de départ (non null)
     * @param to     la gare d'arrivée (non null)
     * @param points le nombre de points à gagner (> 0)
     * @throws IllegalArgumentException si {@code points} <= 0
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Fournit la liste de tous les trajets possibles allant d'une des gares de {@code from}
     * à l'une des gares de {@code to}, chacun valant {@code points} points.
     *
     * @param from   les gares de départ
     * @param to     les gares d'arrivée
     * @param points le nombre de points assignés aux trajets
     * @return la liste de tous les trajets possibles entre les gares de {@code from} et celles de {@code to}
     * @throws IllegalArgumentException si {@code from} ou {@code to} est vide ou
     *                                  si {@code points} <= 0
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(!from.isEmpty() && !to.isEmpty() && points > 0);

        //TODO: better the lambda or the double foreach?
        List<Trip> trips = new ArrayList<>();
        from.forEach(f -> to.forEach(t -> trips.add(new Trip(f, t, points))));

        /*for (Station f : from) {
            for (Station t : to) {
                trips.add(new Trip(f, t, points));
            }
        }*/

        return trips;
    }

    /**
     * @return la gare de départ de ce trajet
     */
    public Station from() {
        return from;
    }

    /**
     * @return la gare d'arrivée de ce trajet
     */
    public Station to() {
        return to;
    }

    /**
     * @return le nombre de points de ce trajet
     */
    public int points() {
        return points;
    }

    /**
     * Retourne le nombre de points du trajet si les deux gares du trajet sont bien connectées,
     * et la négation de ce nombre de points sinon.
     *
     * @param connectivity connectivité d'un joueur
     * @return le nombre de points du trajet par rapport à {@code connectivity}
     */
    public int points(StationConnectivity connectivity) {
        return connectivity.connected(from, to) ? points : -points;
    }
}
