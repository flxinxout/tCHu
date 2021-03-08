package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.HashSet;

/**
 * Représente une partition aplatie de gares
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class StationPartition implements StationConnectivity {

    private final int[] relations;

    /**
     * Constructeur d'une StationPartition, c-à-d une partition aplatie de gares
     * @param relations
     */
    private StationPartition(int[] relations) {
        this.relations = relations;
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        return false;
    }

    /**
     * Représente le bâtisseur d'une StationPartition
     */
    public static final class Builder {

        private int[] relations;
        private HashSet<Station> stations;

        /**
         * Bâtisseur d'une StationPartition. Il permet de créer une partition profonde de gares
         * @param stationCount
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);
            for(Station station : ChMap.stations()) {
                if(station.id() >= 0 && station.id() < stationCount) stations.add(station);
            }
        }

        /**
         * Retourne le numéro d'identification de la gare représentant celle qui est attachée à {@note idStation}
         * @param idStation l'id de la gare dont on aimerait trouvé le représentant
         * @return le numéro d'identification de la gare représentant
         */
        private int representative(int idStation) {

        }

        /**
         *  joint les sous-ensembles contenant les deux gares passées en argument,
         *  en « élisant » l'un des deux représentants comme représentant du sous-ensemble joint 
         * @param s1 la première station
         * @param s2 la seconde station
         * @return Le bâtisseur (this)
         */
        public Builder connect(Station s1, Station s2) {

        }

        /**
         * @return une StationPartition
         */
        public StationPartition build() {
            return new StationPartition(relations);
        }

    }

}
