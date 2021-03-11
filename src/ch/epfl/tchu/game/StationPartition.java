package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
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
     * Constructeur d'une partition aplatie de gares.
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
     * Bâtisseur d'une StationPartition
     */
    public static final class Builder {

        private final int[] relations;
        private final List<Station> stations;

        /**
         * Construit un bâtisseur de partition d'un ensemble de gares
         * dont l'identité est comprise entre 0 (inclus) et {@code stationCount} (exclus).
         * @param stationCount
         *          identité maximale
         * @throws IllegalArgumentException
         *          si {@code stationCount} < 0
         */
        public Builder(int stationCount) {
            Preconditions.checkArgument(stationCount >= 0);

            //TODO: ASK AUX ASSISTANTS SI UTILE DE PRENDRE LA LISTE ENTIERE DES GARES PUIS
            // VERIFIER SI LEUR INDICE EST CORRECT OU BIEN SI LA LISTE ChMap.stations() EST TJRS
            // ECRITE AVEC LES GARES DANS LE BON ORDRE (D'INDEX) ET DONC FAIRE BOUCLE FOR NORMALE:
            // L'AVANTAGE C'EST QUE Y A PLUS DE LISTE stations DONC CA ALLEGE LA CLASSE
            /*relations = new int[stationCount];
            for (int i = 0; i < stationCount; i++) {
                relations[i] = ChMap.stations().get(i).id();
            }*/

            stations = new ArrayList<>();

            for(Station station : ChMap.stations()) {
                if(station.id() < stationCount)
                    stations.add(station);
            }

            relations = new int[stations.size()];
            for (int i = 0; i < relations.length; i++) {
                relations[i] = i;
            }
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
            //TODO: elle est fausse
            relations[s2.id()] = representative(s1.id());
            return this;
        }

        /**
         * Retourne la partition aplatie des gares
         * correspondant à la partition profonde en cours de construction par ce bâtisseur.
         * @return la partition aplatie des gares ajoutées jusqu'à présent à {@code this}
         */
        public StationPartition build() {
            //TODO: on aplatit rien du tout la il le faut
            return new StationPartition(relations);
        }

        /**
         * Retourne le numéro d'identification de la gare représentant
         * celle qui est attachée à {@code idStation}.
         * @return le numéro d'identification de la gare représentant
         * celle qui est attachée à {@code idStation}
         */
        private int representative(int idStation) {
            return relations[idStation];
        }
    }
}
