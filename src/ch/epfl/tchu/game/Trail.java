package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Un chemin orienté reliant deux villes ou plus.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Trail {

    private final List<Route> routes;
    private final Station from;
    private final Station to;
    private final int length;

    private Trail(List<Route> routes, Station from, Station to) {
        this.routes = List.copyOf(routes);
        this.from = from;
        this.to = to;
        this.length = computeLength(routes, from, to);
    }

    /**
     * Retourne le plus long chemin du réseau constitué de {@code routes}.
     *
     * @param routes les routes composant le réseau
     * @return le plus long chemin du réseau
     */
    public static Trail longest(List<Route> routes) {
        List<Trail> allTrails = computeTrivialTrails(routes);

        Trail maxLengthTrail = allTrails.stream()
                .max(Comparator.comparing(Trail::length))
                .orElse(new Trail(List.of(), null, null));

        while (!allTrails.isEmpty()) {
            List<Trail> tempTrails = new ArrayList<>();

            for (Trail trail : allTrails) {
                final List<Route> routesNotInTrail = new ArrayList<>(routes);
                routesNotInTrail.removeAll(trail.routes);

                for (Route route : routesNotInTrail) {
                    final Trail newTrail = tryExtend(trail, route);
                    if (!newTrail.equals(trail))
                        tempTrails.add(newTrail);
                }
            }
            allTrails = tempTrails;

            final Trail possibleMaxLengthTrail = allTrails.stream()
                    .max(Comparator.comparing(Trail::length))
                    .orElse(maxLengthTrail);

            maxLengthTrail = maxLengthTrail.length() < possibleMaxLengthTrail.length() ?
                    possibleMaxLengthTrail : maxLengthTrail;
        }

        return maxLengthTrail;
    }

    /**
     * Retourne la représentation textuelle de ce chemin. Elle est de forme "Gare1 - Gare2 - ... (points)".
     *
     * @return la représentation textuelle de ce chemin
     */
    @Override
    public String toString() {
        final List<String> stationNames = new ArrayList<>();
        String lastStationName;

        if (from != null) {
            stationNames.add(from.name());
            lastStationName = from.name();
        } else
            return "Chemin inexistant";

        for (Route route : routes) {
            final String name = lastStationName.equals(route.station1().name()) ?
                    route.station2().name() : route.station1().name();
            stationNames.add(name);
            lastStationName = name;
        }

        final String names = String.join(" - ", stationNames);
        return String.format("%s (%s)", names, length());
    }

    /**
     * Retourne tous les chemins constitués d'une seule route de la liste {@code routes}.
     */
    private static List<Trail> computeTrivialTrails(List<Route> routes) {
        final List<Trail> trivialTrailsList = new ArrayList<>();

        for (Route route : routes) {
            trivialTrailsList.add(new Trail(List.of(route), route.station1(), route.station2()));
            trivialTrailsList.add(new Trail(List.of(route), route.station2(), route.station1()));
        }
        return trivialTrailsList;
    }

    /**
     * Retourne un chemin similaire à {@code trailToExtend}, seulement étendu avec {@code route},
     * retourne le nouveau chemin si l'extension a fonctionné, retourne le chemin initial {@code trailToExtend} sinon.
     */
    private static Trail tryExtend(Trail trailToExtend, Route route) {
        if (route.station1().equals(trailToExtend.station2()))
            return extend(trailToExtend, route, route.station2());
        else if (route.station2().equals(trailToExtend.station2()))
            return extend(trailToExtend, route, route.station1());
        else
            return trailToExtend;
    }

    /**
     * Retourne un nouveau chemin similaire à {@code trail}, seulement étendu avec la route {@code route}.
     * La gare d'arrivée du nouveau chemin est {@code endStation}.
     */
    private static Trail extend(Trail trailToExtend, Route route, Station endStation) {
        final List<Route> newRoads = new ArrayList<>(trailToExtend.routes);
        newRoads.add(route);
        return new Trail(newRoads, trailToExtend.station1(), endStation);
    }

    /**
     * Calcule la longueur du chemin constitué de ces routes (somme de la longueur des routes).
     */
    private static int computeLength(List<Route> routes, Station station1, Station station2) {
        if (station1 == null || station2 == null)
            return 0;

        final int length = routes.stream()
                .mapToInt(Route::length)
                .sum();

        return length;
    }

    /**
     * Retourne la longueur de ce chemin (somme de la longueur des routes le constituant)
     *
     * @return la longueur de ce chemin
     */
    public int length() {
        return this.length;
    }

    /**
     * @return la station de départ de ce chemin
     */
    public Station station1() {
        return length() == 0 ? null : this.from;
    }

    /**
     * @return la station d'arrivée de ce chemin
     */
    public Station station2() {
        return length() == 0 ? null : this.to;
    }
}

















