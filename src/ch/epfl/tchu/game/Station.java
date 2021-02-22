package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

/**
 * Une gare.
 *
 * @author Dylan Vairoli (//TODO SCIPER)
 * @author Giovanni Ranieri (//TODO SCIPER)
 */
public final class Station {

    private final int id;
    private final String name;

    /**
     * Construit une gare avec le numéro d'identification
     * et le nom donnés.
     *
     * @param id
     *            le numéro d'identification (doit être positif)
     * @param name
     *            le nom
     * @throws IllegalArgumentException
     *             si le numéro d'identification est strictement négatif
     */
    public Station(int id, String name) {
        Preconditions.checkArgument(id >= 0);
        this.id = id;
        this.name = name;
    }

    /**
     * Retourne le numéro d'identification de cette gare.
     * @return le numéro d'identification de cette gare
     */
    public int id() {
        return id;
    }

    /**
     * Retourne le nom de cette gare.
     * @return le nom de cette gare
     */
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name();
    }
}
