package ch.epfl.tchu;

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
