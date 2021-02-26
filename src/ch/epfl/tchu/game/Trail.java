package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Trail {

    private final int length;
    private final Station from;
    private final Station to;

    private Trail(int length, Station from, Station to) {
        this.length = length;
        this.from = from;
        this.to = to;
    }

    /**
     * Retourne le chemin le plus long d'une liste de route données
     * @param routes la liste de routes à itérer
     * @return le chemin le plus long
     */
    public static Trail longest(List<Route> routes) {
        if(routes.isEmpty()) return new Trail(0, null, null);

        /*
        cs = liste des chemins constitués d'une seule route
        tant que cs n'est pas vide :
        cs' = liste vide
        pour tout chemin c de cs :
            rs = (routes appartenant au joueur, n'appartenant pas à c, et pouvant prolonger c) //TODO ?????????
            pour toute route r de rs :
                ajouter c prolongé de r à cs'
        cs = cs'
         */

        int maxLength = 0;
        List<Trail> singleTrailList = getSingleTrailList(routes);

        while (!singleTrailList.isEmpty()) {
            List<Trail> trails = new ArrayList<>();

            for(Trail trail : singleTrailList) {
                List<Route> routePlayer = new ArrayList<>();
                //TODO pas compris l'étape

                for(Route route : routePlayer) {
                    trails.add()
                }
            }

            singleTrailList = trails;

        }

    }

    private static List<Trail> getSingleTrailList(List<Route> routes) {
        final List<Trail> singleTrailList = new ArrayList<>();
        for(Route route : routes) {
            singleTrailList.add(new Trail(route.length(), route.station1(), route.station2()));
        }
        return singleTrailList;
    }

    /**
     * Retourne la longueur du chemin
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
    public Station station12() {
        return length == 0 ? null : to;
    }

    /**
     * Retourne une représentation textuelle du chemin qui devra retourner au moins la gare de départ et d'arrivée,
     * avec les points
     * @return une représentation textuelle du chemin
     */
    @Override
    public String toString() {

    }
}

















