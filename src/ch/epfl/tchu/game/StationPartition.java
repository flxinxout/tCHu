package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Représente une partition aplatie de gares.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class StationPartition implements StationConnectivity {

    private final int[] relations;

    /**
     * Construit une partition aplatie de gares à partir de {@code relations}
     * qui est un tableau d'entiers contenant les liens liant chaque élément au représentant de leur sous-ensemble.
     *
     * @param relations tableau ayant comme chaque entrée la relation index:représentant
     */
    private StationPartition(int[] relations) {
        this.relations = relations.clone();
    }

    @Override
    public boolean connected(Station s1, Station s2) {
        return s1.id() < relations.length && s2.id() < relations.length ?
                relations[s1.id()] == relations[s2.id()] : s1.id() == s2.id();
    }

    /**
     * Bâtisseur d'une StationPartition
     */
    public static final class Builder {

        private final int[] relations;

        /**
         * Construit un bâtisseur de partition d'un ensemble de gares dont l'identité est comprise
         * entre 0 (inclus) et {@code stationCount} (exclu). Chaque gare est représentante de son propre sous-ensemble.
         *
         * @param stationCount identité maximale
         * @throws IllegalArgumentException si {@code stationCount} < 0
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);

            this.relations = new int[stationCount];
            for (int i = 0; i < stationCount; i++) {
                relations[i] = i;
            }
        }

        /**
         * Joint les sous-ensembles contenant les deux gares passées en argument, en « élisant »
         * l'un des deux représentants comme représentant du sous-ensemble joint.
         *
         * @param s1 la première gare
         * @param s2 la seconde gare
         * @return {@code this}
         */
        public Builder connect(Station s1, Station s2) {
            relations[representative(s2.id())] = representative(s1.id());
            return this;
        }

        /**
         * Retourne la partition aplatie des gares
         * correspondant à la partition profonde en cours de construction par ce bâtisseur.
         *
         * @return la partition aplatie des gares ajoutées jusqu'à présent à {@code this}
         */
        public StationPartition build() {
            for (int i = 0; i < relations.length; i++) {
                relations[i] = representative(i);
            }
            return new StationPartition(relations);
        }

        /**
         * @return le numéro d'identification du représentant du sous-ensemble la contenant
         */
        private int representative(int stationId) {
            int parent = relations[stationId];
            while (parent != relations[parent]) {
                parent = relations[parent];
            }
            return parent;
        }
    }
}
