package ch.epfl.tchu.game;

import java.util.List;

/**
 * Énumération des différentes couleurs du jeu
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
     * Liste contenant toutes les différentes couleurs
     */
    public static final List<Color> ALL = List.of(values());
    /**
     * Nombre de couleurs distinctes disponibles
     */
    public static final int COUNT = ALL.size();
}
