package ch.epfl.tchu;

/**
 * Sert à vérifier la validité des arguments passés à une méthode
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Preconditions {

    private Preconditions() {}

    /**
     * Check the validity of an argument
     * @param shouldBeTrue
     * @throws IllegalArgumentException if shouldBeTrue is false
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if(!shouldBeTrue)
            throw new IllegalArgumentException();
    }

}
