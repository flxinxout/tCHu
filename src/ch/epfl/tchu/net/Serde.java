package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
     * Retourne le {@code Serde} correspondant aux fonctions de (dé)sérialisation
     * {@code serialization} et {@code deserialization}.
     *
     * @param serialization la fonction de sérialisation
     * @param deserialization la fonction de désérialisation
     * @param <T> le paramètre de type de la méthode
     * @return le serde correspondant aux 2 fonctions
     */
    static <T> Serde<T> of(Function<T, String> serialization, Function<String, T> deserialization) {
        return new Serde<>() {
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
     * Retourne le {@code Serde} correspondant à la (dé)sérialisation de {@code values} qui représente
     * la liste de toutes les valeurs d'un ensemble de valeurs énuméré.
     *
     * @param values les valeurs à sérialiser
     * @param <T> le paramètre de type de la méthode
     * @return le serde correspondant à la sérialisation et la désérialisation de {@code values}
     */
    static <T> Serde<T> oneOf(List<T> values) {
        Preconditions.checkArgument(!values.isEmpty());
        return Serde.of(i -> Integer.toString(values.indexOf(i)), s -> values.get(Integer.parseInt(s)));
    }

    /**
     * Retourne un serde capable de (dé)sérialiser une liste de valeurs (dé)sérialisées par le serde {@code serde}.
     *
     * @param serde le serde donné
     * @param delimiter le caractère de séparation
     * @param <T> le paramètre de type de la méthode
     * @return un serde capable de (dé)sérialiser des listes de valeurs (dé)sérialisées par le serde donné
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, String delimiter) {
        return new Serde<>() {
            @Override
            public String serialize(List<T> list) {
                return list.stream()
                        .map(t -> serde.serialize(t))
                        .collect(Collectors.joining(delimiter));
            }

            @Override
            public List<T> deserialize(String str) {
                String[] splits = str.split(Pattern.quote(delimiter), -1);
                return Arrays.stream(splits)
                        .map(s -> serde.deserialize(s))
                        .collect(Collectors.toList());
            }
        };
    }

    /**
     * Retourne un serde capable de (dé)sérialiser un {@code SortedBag} de valeurs (dé)sérialisées par le serde donné.
     *
     * @param serde le serde donné
     * @param separationChar le caractère de séparation
     * @param <T> le paramètre de type de la méthode
     * @return un serde capable de (dé)sérialiser des listes de valeurs (dé)sérialisées par le serde donné.
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separationChar) {
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> obj) {
                StringJoiner join = new StringJoiner(separationChar);
                obj.forEach(o -> join.add(serde.serialize(o)));
                return join.toString();
            }

            @Override
            public SortedBag<T> deserialize(String str) {
                String[] strings = str.split(Pattern.quote(separationChar), -1);
                final SortedBag.Builder<T> sortedBag = new SortedBag.Builder<>();
                for(String string : strings) {
                    sortedBag.add(serde.deserialize(string));
                }
                return sortedBag.build();
            }
        };
    }
}