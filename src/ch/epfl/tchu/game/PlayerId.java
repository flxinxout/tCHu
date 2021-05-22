package ch.epfl.tchu.game;

import java.util.Collection;
import java.util.List;

/**
 * Énumération représentant l'identité un joueur.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public enum PlayerId {

    PLAYER_1,
    PLAYER_2,
    PLAYER_3,
    PLAYER_4;

    /**
     * La liste contenant les différentes identités des joueurs.
     */
    public static final List<PlayerId> ALL = List.of(values());

    /**
     * Le nombre de joueurs disponibles.
     */
    public static final int COUNT = ALL.size();

    /**
     * Retourne l'identité du joueur qui suit celui auquel on l'applique.
     *
     * @return l'identité du joueur qui suit celui auquel on l'applique
     */
    public PlayerId next() {
        switch (this) {
            case PLAYER_1:
                return PLAYER_2;
            case PLAYER_2:
                return PLAYER_3;
            case PLAYER_3:
                return PLAYER_4;
            case PLAYER_4:
                return PLAYER_1;
            default:
                throw new Error();
        }
    }

    /**
     * Retourne l'identité du joueur qui suit celui auquel on l'applique dans la collection donnée.
     *
     * @return l'identité du joueur qui suit celui auquel on l'applique dans la collection donnée
     */
    public PlayerId next(Collection<PlayerId> ids) {
        return ids.contains(this.next()) ? this.next() : PLAYER_1;
    }
}