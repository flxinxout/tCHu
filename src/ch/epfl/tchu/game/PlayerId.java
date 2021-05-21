package ch.epfl.tchu.game;

import java.util.ArrayList;
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
     * Retourne une liste triée de toutes les identités de joueur possibles. Le premier élément de la liste
     * est {@code this}, puis les autres sont triés dans leur ordre de déclaration à partir de {@code this}.
     *
     * @return une liste triée de toutes les identités de joueur possibles
     */
    public List<PlayerId> sorted() {
        List<PlayerId> ids = new ArrayList<>(COUNT);
        ids.add(this);

        PlayerId lastId = this;
        for (int i = 0; i < COUNT - 1; i++) {
            ids.add(lastId.next());
        }
        return ids;
    }

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
}