package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
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
            public String serialize(T obj) {
                return serialization.apply(obj);
            }

            @Override
            public T deserialize(String str) {
                return deserialization.apply(str);
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
        return Serde.of(i -> Integer.toString(values.indexOf(i)), s -> values.get(Integer.parseInt(s)));
    }

    /**
     * Retourne un serde capable de (dé)sérialiser une liste de valeurs (dé)sérialisées par le serde donné.
     *
     * @param serde le serde donné
     * @param character le caractère de séparation
     * @param <T> le paramètre de type de la méthode
     * @return un serde capable de (dé)sérialiser des listes de valeurs (dé)sérialisées par le serde donné.
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String character) {
        return new Serde<>() {
            @Override
            public String serialize(List<T> obj) {
                StringJoiner join = new StringJoiner(character);
                obj.forEach(o -> join.add(serde.serialize(o)));
                return join.toString();
            }

            @Override
            public List<T> deserialize(String str) {
                String[] strings = str.split(Pattern.quote(character), -1);
                final List<T> list = new ArrayList<>();
                for(String string : strings) {
                    list.add(serde.deserialize(string));
                }
                return list;
            }
        };
    }

    /**
     * Retourne un serde capable de (dé)sérialiser un sortedBag de valeurs (dé)sérialisées par le serde donné.
     *
     * @param serde le serde donné
     * @param character le caractère de séparation
     * @param <T> le paramètre de type de la méthode
     * @return un serde capable de (dé)sérialiser des listes de valeurs (dé)sérialisées par le serde donné.
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String character) {
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> obj) {
                StringJoiner join = new StringJoiner(character);
                obj.forEach(o -> join.add(serde.serialize(o)));
                return join.toString();
            }

            @Override
            public SortedBag<T> deserialize(String str) {
                String[] strings = str.split(Pattern.quote(character), -1);
                final SortedBag.Builder<T> sortedBag = new SortedBag.Builder<>();
                for(String string : strings) {
                    sortedBag.add(serde.deserialize(string));
                }
                return sortedBag.build();
            }
        };
    }
}
























