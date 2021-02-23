package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Un billet.
 *
 * @author Dylan Vairoli (//TODO SCIPER)
 * @author Giovanni Ranieri (//TODO SCIPER)
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
     *             le nombre de points à gagner (doit être strictement positif)
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
     * @param tripList
     *                la list de trip en question
     * @return la représentation textuelle du billet.
     */
    private static String computeText(List<Trip> tripList) {

        // La précondition passée, nous pouvons prendre la première gare de dpéart car elle est identique partout
        String stationFrom = tripList.get(0).from().name();

        // Si la list a un seul élément, alors il faut juste return le text
        if(tripList.size() == 1) {
            return stationFrom + " - " + tripList.get(0).to().name() +
                    " (" + tripList.get(0).points() + ")";
        }

        // Tu me diras, mais logiquement c'est pour si jamais y'a la meme gare d'arrivée plusieurs fois
        TreeSet<String> stationsTo = new TreeSet<>();

        // Création des chaines avec le nom d'arrivée et le nombre de points attribué
        for(Trip trip : tripList) {
            stationsTo.add(String.format("%s (%s)", trip.to().name(), trip.points()));
        }

        // String qui sera dans les accolades avec les différentes station d'arrivées
        String main = String.join(", ", stationsTo);

        // Etant donné que la Precondition a déjà check si y'a bien une seul gare de départ, on peut prendre
        // la première trip pour le départ, puis add le reste.
        return stationFrom + " - {" + main + "}";
    }

    @Override
    public int compareTo(Ticket o) {
        return 0;
    }

    @Override
    public String toString() {
        return text;
    }
}














