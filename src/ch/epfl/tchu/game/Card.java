package ch.epfl.tchu.game;

import java.util.List;

public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLACK),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    private final Color color;
    public static final List<Card> ALL = List.of(values());
    public static final int COUNT = ALL.size();
    public static final List<Card> CARS = ALL.subList(0, COUNT - 1);

    Card(Color color) {
        this.color = color;
    }

    /**
     * @param color the color of the card
     * @return the card with this color
     */
    public static Card of(Color color) {
        return Card.valueOf(color.name());
    }

    /** Return the color of the card */
    public Color color() {
        return color;
    }

}
