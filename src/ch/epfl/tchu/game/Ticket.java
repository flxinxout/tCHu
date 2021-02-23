package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

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
     *
     * @param tripList
     *                la list de trajets du billet
     * @return la représentation textuelle du billet.
     */
    private static String computeText(List<Trip> tripList) {

        //TODO: attribut "stationFrom" ??
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

    @Override
    public int compareTo(Ticket o) {

        // Mais du coup faut juste comparer et voir si c'est dans l'ordre ?
        for (int j = 0; j < trips.size(); j++) {
            if(trips.get(j).from().name().compareTo(trips.get(j + 1).from().name()) > 0) {
                
            }
        }

        // TODO: en fait j'en tenté de trier la list dans l'ordre alphabétique mais c'est pas trop ça qu'il faut faire mdr
       /* int size = trips.size();

        for (int j = 1; j < size; j++) {
            Trip key = trips.get(j);
            int i = j - 1;
            while (i >= 0) {
                if (key.from().name().compareTo(trips.get(i).from().name()) > 0) {//here too
                    break;
                }
                trips.set(i + 1, trips.get(i));
                i--;
            }
            trips.set(i + 1, key);
        } */
    }

    @Override
    public String toString() {
        return text;
    }
}














