package ch.epfl.tchu.game;

import java.util.List;

/**
 * Énumération représentant l'identité un joueur.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public enum PlayerId {

    PLAYER_1,
    PLAYER_2;

    /**
     * La liste contenant les différentes identités joueurs.
     */
    public static final List<PlayerId> ALL = List.of(values());

    /**
     * Le nombre de joueurs disponibles.
     */
    public static final int COUNT = ALL.size();

    /**
     * Retourne l'identité du joueur qui suit celui auquel on l'applique.
     * @return l'identité du joueur qui suit celui auquel on l'applique
     */
    public PlayerId next() {
        return this == PLAYER_1 ? PLAYER_2 : PLAYER_1;
    }
}