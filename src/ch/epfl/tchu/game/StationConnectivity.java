package ch.epfl.tchu.game;

/**
 * Représente la connectivité du réseau d'un joueur,
 * c-à-d le fait que deux gares du réseau de tCHu soient reliées ou non par le réseau du joueur en question.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public interface StationConnectivity {

    /**
     * Indique ssi {@code s1} et {@code s2} sont connectées entre elles par le réseau du joueur.
     *
     * @param s1 la première gare
     * @param s2 la deuxième gare
     * @return ssi {@code s1} et {@code s2} sont connectées entre elles par le réseau du joueur
     */
    boolean connected(Station s1, Station s2);
}
