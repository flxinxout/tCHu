package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Représente une partition aplatie de gares.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class StationPartition implements StationConnectivity {

    private final int[] relations;

    /**
     * Constructeur d'une StationPartition, c-à-d une partition aplatie de gares
     * @param relations
     *               tableau avec comme chaque entrée la relation index:représentant
     */
    private StationPartition(int[] relations) {
        this.relations = relations;
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        if (s1.id() < relations.length && s2.id() < relations.length)
            return relations[s1.id()] == relations[s2.id()];
        else
            return s1.id() == s2.id();
    }

    /**
     * Représente le bâtisseur d'une StationPartition
     */
    public static final class Builder {

        private int[] relations;
        private List<Station> stations;

        /**
         * Construit un bâtisseur de partition d'un ensemble de gares
         * dont l'identité est comprise entre 0 (inclus) et stationCount (exclus)
         * @param stationCount
         *          identité maximale
         * @throws IllegalArgumentException
         *          si {@code stationCount} < 0
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);

            for(Station station : ChMap.stations()) {
                if(station.id() >= 0 && station.id() < stationCount)
                    stations.add(station);
            }

            relations = new int[stations.size()];
            for (int i = 0; i < relations.length; i++) {
                relations[i] = i;
            }
        }

        /**
         * Retourne le numéro d'identification de la gare représentant
         * celle qui est attachée à {@code idStation}
         * @return le numéro d'identification de la gare représentant
         */
        private int representative(int idStation) {
            return relations[idStation];
        }

        /**
         *  Joint les sous-ensembles contenant les deux gares passées en argument, en « élisant »
         *  l'un des deux représentants comme représentant du sous-ensemble joint.
         * @param s1
         *          la première gare
         * @param s2
         *          la seconde gare
         * @return le bâtisseur ({@code this})
         */
        public Builder connect(Station s1, Station s2) {
            relations[s2.id()] = representative(s1.id());
            return this;
        }

        /**
         * @return une StationPartition
         */
        public StationPartition build() {
            return new StationPartition(relations);
        }
    }
}
