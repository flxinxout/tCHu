package ch.epfl.tchu.game;

import java.util.List;

/**
 * Énumération des différentes couleurs des éléments du jeu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public enum Color {

    BLACK,
    VIOLET,
    BLUE,
    GREEN,
    YELLOW,
    ORANGE,
    RED,
    WHITE;

    /**
     * Une liste contenant toutes les différentes couleurs.
     */
    public static final List<Color> ALL = List.of(values());

    /**
     * Le nombre de couleurs distinctes disponibles.
     */
    public static final int COUNT = ALL.size();
}
