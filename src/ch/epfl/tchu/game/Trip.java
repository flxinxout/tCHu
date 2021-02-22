package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Return all trips between from and to
     * @param from the station from
     * @param to the station to
     * @param points the points of the trip
     * @return the list with all trips
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {
        Preconditions.checkArgument(!from.isEmpty() || !to.isEmpty() || points > 0);
        List<Trip> trips = new ArrayList<>();
        for(Station fr : from) {
            for(Station t : to) {
                trips.add(new Trip(fr, t, points));
            }
        }
        return trips;
    }

    /** Return the station from of the trip */
    public Station from() {
        return from;
    }

    /** Return the station to of the trip */
    public Station to() {
        return to;
    }

    /** Return the points of the trip */
    public int points() {
        return points;
    }

    /**
     * Return the the points of the trip if from and to are connected
     * @param connectivity the connectivity of the trip
     * @return the points of the trip
     */
    public int points(StationConnectivity connectivity) {
        return connectivity.connected(from, to) ? points : -points;
    }
}
