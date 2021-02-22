package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Collections;
import java.util.List;

public final class Ticket implements Comparable<Ticket> {

    private final List<Trip> trips;
    private final String computeText;

    public Ticket(List<Trip> trips) {
        // TODO BEST WAY TO DO THIS !!!
        boolean checkName = true;
        for(Trip trip : trips) {
            for(Trip tr : trips) {
                if(trip.from().name().equals(tr.from().name())) checkName = false;
            }
        }

        Preconditions.checkArgument(!trips.isEmpty() || checkName);
        this.trips = trips;
        this.computeText = computeText();
    }

    public Ticket(Station from, Station to, int points) {
        this(Collections.singletonList(new Trip(from, to, points)));
    }

    public String text() {
        return computeText;
    }

    private static String computeText() {
        //TODO METHODE POUR TEXT ET UTILE JE PENSE POUR LE FOREACH AU CONSTRUCTEUR

        return null;
    }

    @Override
    public int compareTo(Ticket o) {
        return 0;
    }

    @Override
    public String toString() {
        return computeText;
    }
}














