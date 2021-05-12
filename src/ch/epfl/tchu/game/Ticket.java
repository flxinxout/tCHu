package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;

/**
 * Un billet.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;
    private final String text;

    /**
     * Construit un billet constitué de la liste de trajets donnée.
     *
     * @param trips liste des trajets (non vide)
     * @throws IllegalArgumentException si {@code trips} est vide ou
     *                                  si tous les trajets n'ont pas la même gare de départ
     */
    public Ticket(List<Trip> trips) {
        Preconditions.checkArgument(!trips.isEmpty());
        Preconditions.checkArgument(trips.stream()
                .map(t -> t.from().name())
                .distinct()
                .count() == 1);

        this.trips = List.copyOf(trips);
        this.text = computeText(trips);
    }

    /**
     * Construit un billet constitué d'un unique trajet entre {@code from} et {@code to} et valant {@code points}.
     *
     * @param from   la gare de départ (non null)
     * @param to     la gare d'arrivée (non null)
     * @param points le nombre de points à gagner > 0
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * Crée la représentation textuelle de ce billet.
     *
     * @param trips les trajets du billet
     * @return la représentation textuelle de ce billet.
     */
    private static String computeText(List<Trip> trips) {
        Trip firstTrip = trips.get(0);

        // Même si on nous a dit que l'on pouvait enlever ce check, nous le laissons car si c'est le cas cela évite
        // le foreach qui vient par la suite.
        if (trips.size() == 1)
            return String.format("%s - %s (%s)", firstTrip.from(), firstTrip.to(), firstTrip.points());

        TreeSet<String> stationsToTexts = new TreeSet<>();
        trips.forEach(trip -> stationsToTexts.add(String.format("%s (%s)", trip.to().name(), trip.points())));

        return String.format("%s - {%s}", firstTrip.from(), String.join(", ", stationsToTexts));
    }

    /**
     * Retourne le nombre de points que vaut ce billet,
     * sachant que la connectivité donnée est celle du joueur possédant le billet.
     *
     * @param connectivity connectivité du joueur possédant ce billet
     * @return le nombre de points du billet pour la connectivité donnée
     */
    public int points(StationConnectivity connectivity) {
        boolean isAnyConnection = trips.stream()
                .anyMatch(trip -> connectivity.connected(trip.from(), trip.to()));

        if (!isAnyConnection)
            return trips.stream()
                    .mapToInt(t -> t.points(connectivity))
                    .max()
                    .getAsInt();

        return trips.stream()
                .mapToInt(t -> t.points(connectivity))
                .max()
                .getAsInt();
    }

    /**
     * Compare {@code this} et {@code that} par ordre alphabétique de leur représentation textuelle.
     *
     * @param that billet avec lequel comparer {@code this}
     * @return un entier < 0 si {@code this} < {@code that},
     * un entier > 0 si {@code this} > {@code that},
     * et 0 si {@code this} = {@code that}
     */
    @Override
    public int compareTo(Ticket that) {
        return text.compareTo(that.text());
    }

    @Override
    public String toString() {
        return text;
    }

    /**
     * Retourne la représentation textuelle de ce billet.
     *
     * @return la représentation textuelle de ce billet
     */
    public String text() {
        return text;
    }
}














