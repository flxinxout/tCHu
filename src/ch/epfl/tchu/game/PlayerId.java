package ch.epfl.tchu.game;

import java.util.List;

/**
 * Enumération permettant de représenter un joueur
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public enum PlayerId {

    PLAYER_1,
    PLAYER_2;

    /**
     * @return la Liste contenant tous les différents joueurs
     */
    public static final List<PlayerId> ALL = List.of(values());
    /**
     * @return le Nombre de joueurs
     */
    public static final int COUNT = ALL.size();

    /**
     * @return le joueur contre qui le joueur appellant cette méthode joue
     */
    public PlayerId next() {
        //TODO PAS SUR :/
        return this.name().equals(PLAYER_1) ? PLAYER_2 : PLAYER_1;
    }

}
