package ch.epfl.tchu.net;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Représente un serializer-deserializer, un objet capable de sérialiser et désérialiser des valeurs d'un type donné.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public interface Serde<E> {

    /**
     * Sérialise {@code obj} et retourne la chaîne correspondante.
     *
     * @param obj l'objet à sérialiser
     * @return la chaîne correspondant à la sérialisation de {@code obj}
     */
    String serialize(E obj);

    /**
     * Désérialise {@code str} et retourne l'objet correspondant.
     *
     * @param str la chaîne à désérialiser
     * @return l'objet correspondant à la désérialisation de {@code str}
     */
    E deserialize(String str);

    /**
     * Retourne la Serde correspondant aux fonctions de sérialisation et désérialisation données {@code serialization}
     * and {@code deserialization}.
     *
     * @param serialization la fonction de sérialisation
     * @param deserialization la fonction de désérialisation
     * @param <T> le paramètre de type de la méthode
     * @return la serde associée aux 2 fonctions.
     */
    static <T> Serde<T> of(Function<T, String> serialization, Function<String, T> deserialization) {
        return new Serde<T>() {
            @Override
            public String serialize(T objectToSerialize) {
                return serialization.apply(objectToSerialize);
            }

            @Override
            public T deserialize(String stringToDeserialize) {
                return deserialization.apply(stringToDeserialize);
            }
        };
    }

    /**
     * Retourne le serde correspondant à la sérialisation et la désérialisation du paramètre donné.
     *
     * @param values les valeurs à sérialiser et désérialiser
     * @param <T> le paramètre de type de la méthode
     * @return le serde correspondant à la sérialisation et la désérialisation du paramètre donné.
     */
    static <T> Serde<T> oneOf(List<T> values) {
        return new Serde<T>() {
            @Override
            public String serialize(T objectToSerialize) {
                //TODO conseil: utiliser join de String
                StringBuilder string = new StringBuilder();
                values.forEach(e -> string.append(serialize(e)));
                return string.toString();
            }

            //TODO PAS TROP SUR...
            @Override
            public T deserialize(String stringToDeserialize) {
                return deserialize(stringToDeserialize);
            }
        };
    }

    /**
     * Retourne un serde capable de (dé)sérialiser une liste de valeurs (dé)sérialisées par le serde donné.
     *
     * @param serde le serde donné
     * @param character le caractère de séparation
     * @param <T> le paramètre de type de la méthode
     * @return un serde capable de (dé)sérialiser des listes de valeurs (dé)sérialisées par le serde donné.
     */
    static <T extends Collection<T>> Serde<T> listOf(Serde<T> serde, String character) {
        return new Serde<T>() {
            @Override
            public String serialize(T objectToSerialize) {
                return String.join(Pattern.quote(character), serde.serialize(objectToSerialize));
            }

            //TODO PAS TROP COMPRIS NON PLUS
            @Override
            public T deserialize(String stringToDeserialize) {
                String[] strings = stringToDeserialize.split(Pattern.quote(character), -1);
                return null;
            }
        };
    }

    //TODO: TOUT COMME L'AUTRE...
    /**
     * Retourne un serde capable de (dé)sérialiser un sortedBag de valeurs (dé)sérialisées par le serde donné.
     *
     * @param serde le serde donné
     * @param character le caractère de séparation
     * @param <T> le paramètre de type de la méthode
     * @return un serde capable de (dé)sérialiser des listes de valeurs (dé)sérialisées par le serde donné.
     */
    static <T extends Collection<T>> Serde<T> bagOf(Serde<T> serde, String character) {
        return new Serde<T>() {
            @Override
            public String serialize(T objectToSerialize) {
                return String.join(Pattern.quote(character), serde.serialize(objectToSerialize));
            }

            //TODO PAS TROP COMPRIS NON PLUS
            @Override
            public T deserialize(String stringToDeserialize) {
                String[] strings = stringToDeserialize.split(Pattern.quote(character), -1);
                return null;

            }
        };
    }

}























