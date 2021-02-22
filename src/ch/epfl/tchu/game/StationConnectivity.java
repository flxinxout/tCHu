package ch.epfl.tchu.game;

public interface StationConnectivity {

    /**
     * @param s1 the first station
     * @param s2 the second station
     * @return true iff the s1 and s2 are connected by the player network
     */
    boolean connected(Station s1, Station s2);

}
