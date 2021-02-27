package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

public final class Trail {

    private final List<Route> routes;
    private final Station from;
    private final Station to;
    private final int length;

    private Trail(List<Route> routes, Station from, Station to, int length) {
        this.routes = routes;
        this.from = from;
        this.to = to;
        this.length = length;
    }

    /**
     * Retourne le chemin le plus long d'une liste de route données
     * @param routes
     *          la liste de routes à comparer
     * @return le chemin le plus long
     */
    public static Trail longest(List<Route> routes) {
        if (routes.isEmpty()) return new Trail(List.of(), null, null, 0);

        List<Trail> trivialTrails = computeTrivialTrails(routes);
        Trail maxLengthTrail = null;

        while (!trivialTrails.isEmpty()) {
            List<Trail> trails = new ArrayList<>();
            for (Trail trail : trivialTrails) {
                // rs = (routes appartenant au joueur, n'appartenant pas à c, et pouvant prolonger c)
                List<Route> routesToExtend = new ArrayList<>();

                for (Route route : routes) {
                    //S'il ne contient pas la route
                    if (!trail.routes.contains(route)) {
                        for (Route trailRoute : trail.routes) {
                            boolean canExtend = tryExtend(trailRoute, route);
                            //Si elle peut la prolonger
                            if (canExtend) {
                                routesToExtend.add(route);
                            }
                        }
                    }
                }

                for (Route route : routesToExtend) {

                    // Création d'une nouvelle list car si on recupère directement celle de trail.routes, et qu'on
                    // lui ajoute une route, alors on va se heurter à une UnSupportedOperationException car cette liste
                    // et immutable.
                    List<Route> newRoadsToGet = new ArrayList<>();
                    newRoadsToGet.addAll(trail.routes);
                    newRoadsToGet.add(route);

                    int newLength = trail.length + route.length();
                    Trail newTrail;

                    if (trail.station2().equals(route.station1()))
                        newTrail = new Trail(newRoadsToGet, trail.station1(), route.station2(), newLength);
                    else
                        newTrail = new Trail(newRoadsToGet, trail.station1(), route.station1(), newLength);

                    trails.add(newTrail);
                    if(maxLengthTrail == null) {
                        maxLengthTrail = newTrail;
                    } else {
                        maxLengthTrail = newTrail.length > maxLengthTrail.length() ? newTrail : maxLengthTrail;
                    }
                }
            }
            trivialTrails = trails;
        }

        return maxLengthTrail;
    }

    /**
     * Retourne tous les chemins constitués d'une seule route
     * reliant les routes d'une liste de routes
     */
    private static List<Trail> computeTrivialTrails(List<Route> routes) {
        final List<Trail> trivialTrailsList = new ArrayList<>();
        //Pour chaque route
        for(Route route : routes) {
            //Ajouter un chemin n'ayant que cette route dans un sens (gare1 - gare2)
            trivialTrailsList.add(new Trail(List.of(route), route.station1(), route.station2(), route.length()));
            //La même dans l'autre sens (gare2 - gare1)
            trivialTrailsList.add(new Trail(List.of(route), route.station2(), route.station1(), route.length()));
        }
        return trivialTrailsList;
    }

    /**
     * Retourne tous les chemins constitués d'une seule route
     * reliant les routes d'une liste de routes
     */
    private static boolean tryExtend(Route trailRoute, Route route) {
        return route.station1().equals(trailRoute.station2()) ||
                route.station2().equals(trailRoute.station2());
    }

    /**
     * Retourne la longueur du chemin (somme de la longueur des routes le constituant)
     * @return la longueur du chemin
     */
    public int length() {
        return length;
    }

    /**
     * Retourne la station de départ du chemin
     * @return la station de départ du chemin
     */
    public Station station1() {
        return length == 0 ? null : from;
    }

    /**
     * Retourne la station d'arrivée du chemin
     * @return la station d'arrivée du chemin
     */
    public Station station2() {
        return length == 0 ? null : to;
    }

    /**
     * Retourne une représentation textuelle du chemin qui devra retourner
     * au moins la gare de départ et d'arrivée, avec les points
     * @return une représentation textuelle du chemin
     */
    @Override
    public String toString() {
        List<String> stationNames = new ArrayList<>();

        for (Route route: routes) {
            stationNames.add(route.station1().name());
        }

        String names = String.join(" - ", stationNames);
        return String.format("%s (%s)", names, this.length);
    }
}

















