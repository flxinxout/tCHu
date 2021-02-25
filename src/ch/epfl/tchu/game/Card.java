package ch.epfl.tchu.game;

import java.util.List;

/**
 * Énumération des différentes cartes du jeu
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    private final Color color;

    /**
     * Liste contenant tous les différents types de cartes
     */
    public static final List<Card> ALL = List.of(values());
    /**
     * Nombre de cartes distinctes disponibles
     */
    public static final int COUNT = ALL.size();
    /**
     * Liste des cartes colorées (non-locomotive) distinctes disponibles
     */
    public static final List<Card> CARS = ALL.subList(0, COUNT - 1);

    Card(Color color) {
        this.color = color;
    }

    /**
     * Retourne le type de carte correspondant à cette couleur.
     *
     * @param color
     *              la couleur du type de carte désiré
     * @return le type de carte correspondant à cette couleur
     */
    public static Card of(Color color) {
        return Card.valueOf(color.name());
    }

    /**
     * Retourne la couleur de ce type de carte.
     * @return la couleur de cette carte
     */
    public Color color() {
        return color;
    }

}
