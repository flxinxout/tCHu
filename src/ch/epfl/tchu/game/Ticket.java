package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Collections;
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
        Preconditions.checkArgument(!trips.isEmpty())

        String fromStation = trips.get(0).from().name();
        boolean checkName = true;
        for(Trip trip : trips) {
            if(!trip.from().name().equals(fromStation))
                checkName = false;
        }
        Preconditions.checkArgument(checkName);

        this.trips = trips;
        this.text = computeText();
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

    private static String computeText() {
        //TODO METHODE POUR TEXT ET UTILE JE PENSE POUR LE FOREACH AU CONSTRUCTEUR
        //Ça brainfuck le static, comment accéder à la liste de trips spécifique?

        return null;
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














