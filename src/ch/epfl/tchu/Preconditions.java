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
     * Vérifie la validité d'un argument
     * @param shouldBeTrue
     *                  la condition à remplir si l'argument est valide
     * @throws IllegalArgumentException
     *                  si la condition est fausse
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if(!shouldBeTrue)
            throw new IllegalArgumentException();
    }

}
