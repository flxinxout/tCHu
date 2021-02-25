package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
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
     * @param trips
     *            liste des trajets (doit être non vide et chaque trajet
     *            doit avoir la même gare de départ)
     * @throws IllegalArgumentException
     *             si la liste est vide ou si tous les trajets n'ont pas la même gare de départ
     */
    public Ticket(List<Trip> trips) {
        // TODO BEST WAY TO DO THIS !!!
        Preconditions.checkArgument(!trips.isEmpty());

        String fromStation = trips.get(0).from().name();
        boolean checkName = true;
        for(Trip trip : trips) {
            if (!trip.from().name().equals(fromStation)) {
                checkName = false;
                break;
            }
        }
        Preconditions.checkArgument(checkName);

        this.trips = trips;
        this.text = computeText(trips);
    }

    /**
     * Construit un billet constitué du trajet correspondant aux gares et points donnés.
     *
     * @param from
     *            la gare de départ
     * @param to
     *            la gare d'arrivée
     * @param points
     *            le nombre de points à gagner (doit être strictement positif)
     */
    public Ticket(Station from, Station to, int points) {
        this(List.of(new Trip(from, to, points)));
    }

    /**
     * Retourne la représentation textuelle du billet.
     * @return la représentation textuelle du billet
     */
    public String text() {
        return text;
    }

    /**
     * Crée la représentation textuelle du billet.
     *
     * @param tripList
     *                la list de trajets du billet
     * @return la représentation textuelle du billet.
     */
    private static String computeText(List<Trip> tripList) {

        Trip firsTrip = tripList.get(0);
        String stationFromName = firsTrip.from().name();

        // Liste des textes liés aux gares d'arrivée
        TreeSet<String> stationsTo = new TreeSet<>();

        for(Trip trip : tripList) {
            stationsTo.add(String.format("%s (%s)", trip.to().name(), trip.points()));
        }

        //Billet à un trajet
        if (tripList.size() == 1) {
            return String.format("%s - %s", stationFromName, stationsTo.first());
        }

        //Partie du texte des gares d'arrivée
        String stationToText = String.join(", ", stationsTo);

        return String.format("%s - {%s}", stationFromName, stationToText);
    }

    /**
     * Retourne le nombre de points maximal des trajets du billet connectés entre eux
     * par le réseau du joueur et la négation du nombre de point minimal du billet si
     * aucun des trajets ne sont connectés entre eux.
     *
     * @param connectivity
     *                  connectivité du réseau du joueur
     * @return le nombre de points du billet pour la connectivité donnée (comme décrit dans la description)
     */
    public int points(StationConnectivity connectivity){
        //Liste des trajet connectés par le joueur
        final List<Trip> connectedTrips = new ArrayList<>();
        //Garde une trace des points minimums des trajets du billet
        int min = trips.get(0).points();

        for (Trip trip: trips) {
            if (connectivity.connected(trip.from(), trip.to())){
                connectedTrips.add(trip);
            }
            if (trip.points() < min)
                min = trip.points();
        }

        if (connectedTrips.isEmpty()) {
            return -min;
        }

        int max = 0;
        for (Trip connectedTrip: connectedTrips) {
            if (connectedTrip.points() > max)
                max = connectedTrip.points();
        }
        return max;
    }

    /**
     * Compare l'ordre alphabétique de la représentation textuelle de deux billets.
     * Retourne un entier strictement négatif si ce billet est strictement plus petit que l'autre,
     * un entier strictement positif si ce billet est strictement plus grand que l'autre,
     * et zéro si les deux sont égaux.
     *
     * @param that
     *            l'autre billet avec lequel comparer ce billet
     * @return un entier en fonction de l'ordre des billets (comme décrit dans la description)
     */
    @Override
    public int compareTo(Ticket that) {
        return this.text.compareTo(that.text());
    }

    /**
     * Retourne la représentation textuelle du billet.
     * @return la représentation textuelle du billet
     */
    @Override
    public String toString() {
        return text;
    }
}














