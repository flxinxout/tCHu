package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

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

        //TODO: stream ou set
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
     * @return la représentation textuelle du billet
     */
    public String text() {
        return text;
    }

    /**
     * Crée la représentation textuelle du billet.
     *
     * @param tripList les trajets du billet
     * @return la représentation textuelle du billet.
     */
    private static String computeText(List<Trip> tripList) {
        Trip firstTrip = tripList.get(0);

        //Billet d'un trajet
        if (tripList.size() == 1)
            return String.format("%s - %s (%s)", firstTrip.from(), firstTrip.to(), firstTrip.points());

        //Billet de plus d'un trajet
        final TreeSet<String> stationsToTexts = new TreeSet<>();
        tripList.forEach(trip -> stationsToTexts.add(String.format("%s (%s)", trip.to().name(), trip.points())));

        return String.format("%s - {%s}", firstTrip.from(), String.join(", ", stationsToTexts));
    }

    /**
     * Retourne le nombre de points que vaut le billet,
     * sachant que la connectivité donnée est celle du joueur possédant le billet.
     *
     * @param connectivity connectivité du joueur possédant le billet
     * @return le nombre de points du billet pour la connectivité donnée
     */
    public int points(StationConnectivity connectivity) {
        List<Trip> connectedTrips = trips.stream()
                .filter(trip -> connectivity.connected(trip.from(), trip.to()))
                .collect(Collectors.toList());

        final int minPoints = trips.stream()
                .mapToInt(trip -> trip.points())
                .min()
                .getAsInt();

        if (connectedTrips.isEmpty())
            return -minPoints;

        final int maxPoints = connectedTrips.stream()
                .mapToInt(trip -> trip.points())
                .max()
                .getAsInt();

        return maxPoints;
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
}














