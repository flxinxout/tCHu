package ch.epfl.tchu;

/**
 * Sert à vérifier la validité des arguments passés à une méthode.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Preconditions {
    private Preconditions() { }

    /**
     * Vérifie la validité d'un argument.
     *
     * @param shouldBeTrue la condition que l'argument doit remplir
     * @throws IllegalArgumentException si {@code shouldBeTrue} est faux
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue)
            throw new IllegalArgumentException();
    }
}
