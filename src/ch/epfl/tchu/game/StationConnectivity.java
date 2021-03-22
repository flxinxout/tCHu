package ch.epfl.tchu.game;

/**
 * Représente ce que nous appellerons la « connectivité » du réseau d'un joueur,
 * c-à-d le fait que deux gares du réseau de tCHu soient reliées ou non par le réseau du joueur en question.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public interface StationConnectivity {

    /**
     * Indique si {@code s1} et {@code s2} sont connectées entre elles par le réseau du joueur.
     *
     * @param s1 la première gare
     * @param s2 la deuxième gare
     * @return si {@code s1} et {@code s2} sont connectées entre elles par le réseau du joueur
     */
    boolean connected(Station s1, Station s2);
}
