package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Un chemin orienté reliant deux villes ou plus.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Trail {

    private final List<Route> routes;
    private final int length;
    private final Station from;
    private final Station to;

    private Trail(List<Route> routes, Station from, Station to) {
        this.routes = routes;
        this.length = computeLength(routes, from, to);
        this.from = from;
        this.to = to;
    }

    /**
     * Retourne le chemin le plus long d'une liste de route données
     * @param routes
     *          la liste de routes à comparer
     * @return le chemin le plus long
     */
    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty())
            return new Trail(List.of(), null, null);

        List<Trail> allTrails = computeTrivialTrails(routes);
        Trail maxLengthTrail = allTrails.get(0);
        for(Trail trail : allTrails) {
            maxLengthTrail = trail.length() > maxLengthTrail.length() ? trail : maxLengthTrail;
        }

        while (!allTrails.isEmpty()) {
            List<Trail> tempTrails = new ArrayList<>();

            for (Trail trail : allTrails) {
                for (Route route : routes) {
                    Trail newTrail = tryExtend(trail, route);
                    if (newTrail != null) {
                        tempTrails.add(newTrail);
                        maxLengthTrail = newTrail.length() > maxLengthTrail.length() ? newTrail : maxLengthTrail;
                    }
                }
            }
            allTrails = tempTrails;
        }

        return maxLengthTrail;
    }

    /**
     * Retourne tous les chemins constitués d'une seule route
     * reliant les routes d'une liste de routes
     */
    private static List<Trail> computeTrivialTrails(List<Route> routes) {
        List<Trail> trivialTrailsList = new ArrayList<>();

        for(Route route : routes) {
            trivialTrailsList.add(new Trail(List.of(route), route.station1(), route.station2()));
            trivialTrailsList.add(new Trail(List.of(route), route.station2(), route.station1()));
        }
        return trivialTrailsList;
    }

    /**
     * Essaie d'étendre un chemin avec une route, retourne ce chemin si l'extension a fonctionné,
     * retourne null sinon.
     */
    private static Trail tryExtend(Trail trailToExtend, Route route) {
        //Si le chemin ne contient pas la route
        //TODO: changer contains par removeAll
        if (!trailToExtend.routes.contains(route)) {
            //Si elle peut prolonger le chemin, retourne le chemin prolongé
            if (route.station1().equals(trailToExtend.station2())){
                return extend(trailToExtend, route, route.station2());
            }
            else if (route.station2().equals(trailToExtend.station2())){
                return extend(trailToExtend, route, route.station1());
            }
            //Sinon retourne null
            else {
                return null;
            }
        }
        return null;
    }

    /**
     * Étend un chemin avec une route.
     */
    private static Trail extend(Trail trailToExtend, Route route, Station endStation) {
        List<Route> newRoads = new ArrayList<>(trailToExtend.routes);
        newRoads.add(route);

        return new Trail(newRoads, trailToExtend.station1(), endStation);
    }

    /**
     * Retourne la longueur du chemin (somme de la longueur des routes le constituant)
     * @return la longueur du chemin
     */
    public int length() {
        return length;
    }

    /**
     * Calcul la longueur du chemin constitué de ces routes (somme de la longueur des routes)
     * @return la longueur du chemin
     */
    private static int computeLength(List<Route> routes, Station station1, Station station2){
        if (station1 == null || station2 == null)
            return 0;

        int length = 0;
        for (Route route: routes) {
            length += route.length();
        }
        return length;
    }

    /**
     * Retourne la station de départ du chemin
     * @return la station de départ du chemin
     */
    public Station station1() {
        return length() == 0 ? null : from;
    }

    /**
     * Retourne la station d'arrivée du chemin
     * @return la station d'arrivée du chemin
     */
    public Station station2() {
        return length() == 0 ? null : to;
    }

    /**
     * Retourne une représentation textuelle du chemin.
     * @return une représentation textuelle du chemin
     */
    @Override
    public String toString() {
        List<String> stationNames = new ArrayList<>();
        String lastStationName;

        if (from != null) {
            stationNames.add(from.name());
            lastStationName = from.name();
        }
        else
            return "Chemin inexistant";

        for (Route route: routes) {
            String name = lastStationName.equals(route.station1().name()) ?
                            route.station2().name() : route.station1().name();
            stationNames.add(name);
            lastStationName = name;
        }

        String names = String.join(" - ", stationNames);
        return String.format("%s (%s)", names, length());
    }
}

















