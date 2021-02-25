package ch.epfl.tchu.game;

import java.util.List;

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
        if(routes.isEmpty()) {
            return new Trail(0, null, null);
        }

        //TODO est-ce qu'on doit check si les routes sont connectées ? si oui on a l'interface stationconnectivity
        for(Route route : routes) {
            for(Route route1 : routes) {
                
            }
        }

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

















