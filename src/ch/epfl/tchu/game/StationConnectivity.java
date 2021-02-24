package ch.epfl.tchu.game;

/**
 * Représente la connectivité du réseau d'un joueur,
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public interface StationConnectivity {

    /**
     * Indique ssi les deux gares sont connectées entre elles par le réseau du joueur.
     *
     * @param s1
     *          la première gare
     * @param s2
     *          la deuxième gare
     *
     * @return ssi les deux gares sont connectées entre elles par le réseau du joueur
     */
    boolean connected(Station s1, Station s2);

}
