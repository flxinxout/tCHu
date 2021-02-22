package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Un trajet.
 *
 * @author Dylan Vairoli (//TODO SCIPER)
 * @author Giovanni Ranieri (//TODO SCIPER)
 */
public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Construit un trajet entre deux gares valant un nombre de points.
     *
     * @param from
     *            la gare de départ
     * @param to
     *            la gare d'arrivée
     * @param points
     *             le nombre de points à gagner (doit être strictement positif)
     * @throws IllegalArgumentException
     *             si les points sont négatifs
     */
    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Fournit la liste de tous les trajets possibles allant
     * d'une des gares de la première liste (from)
     * à l'une des gares de la seconde liste (to),
     * chacun valant le nombre de points donné.
     *
     * @param from
     *            les gares de départ
     * @param to
     *            les gares d'arrivée
     * @param points
     *             //TODO PAS COMPRIS LA REELLE UTILITE, A VOIR PLUS TARD
     * @throws IllegalArgumentException
     *             si l'une des listes est vide ou si le nombre de points est négatif.
     * @return la liste de tous les trajets possibles
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(!from.isEmpty() || !to.isEmpty() || points > 0);
        List<Trip> trips = new ArrayList<>();
        for(Station f : from) {
            for(Station t : to) {
                trips.add(new Trip(f, t, points));
            }
        }
        return trips;
    }

    /**
     * Retourne la gare de départ de ce trajet.
     * @return la gare de départ de ce trajet
     */
    public Station from() {
        return from;
    }

    /**
     * Retourne la gare d'arrivée de ce trajet.
     * @return la gare d'arrivée de ce trajet
     */
    public Station to() {
        return to;
    }

    /**
     * Retourne le nombre de points du trajet.
     * @return le nombre de points du trajet
     */
    public int points() {
        return points;
    }

    /**
     * Retourne le nombre de points du traje si les deux gares du trajet sont bien connectées,
     * et la négation de ce nombre de points sinon.
     *
     * @param connectivity
     *              //TODO pas bien compris son utilité, a voir plus tard
     * @throws IllegalArgumentException
     *             si l'une des listes est vide ou si le nombre de points est négatif.
     * @return le nombre de points du trajet pour la connectivité donnée
     */
    public int points(StationConnectivity connectivity) {
        return connectivity.connected(from, to) ? points : -points;
    }
}
