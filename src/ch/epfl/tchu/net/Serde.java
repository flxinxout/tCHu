package ch.epfl.tchu.net;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
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
     * Crée le {@code Serde} correspondant aux fonctions de (dé)sérialisation données.
     *
     * @param serialization   la fonction de sérialisation
     * @param deserialization la fonction de désérialisation
     * @param <T>             le type des objets à (dé)sérialiser
     * @return le serde correspondant aux deux fonctions données
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
     * Crée le {@code Serde} correspondant à la (dé)sérialisation des valeurs de la liste de valeurs donnée.
     * Cette liste contient toutes les valeurs d'un ensemble de valeurs énuméré et le {@code Serde} retourné se base sur
     * l'index des valeurs au sein de cette liste pour les (dé)sérialiser.
     *
     * @param values la liste de toutes les valeurs de l'ensemble de valeurs énuméré
     * @param <T>    le type des objets contenus dans la liste
     * @return le serde correspondant à la (dé)sérialisation des valeurs de la liste donnée
     * @throws IllegalArgumentException si la liste est vide
     */
    static <T> Serde<T> oneOf(List<T> values) {
        Preconditions.checkArgument(!values.isEmpty());
        return Serde.of(i -> Integer.toString(values.indexOf(i)), s -> values.get(Integer.parseInt(s)));
    }

    /**
     * Crée un {@code Serde} capable de (dé)sérialiser une liste de valeurs du type donné.
     * Ces dernières sont (dé)sérialisées à l'aide du {@code Serde} donné.
     *
     * @param serde     le {@code Serde} utilisé pour (dé)sérialiser les valeurs du type donné
     * @param delimiter le caractère de séparation utilisé pour délimiter les valeurs de la liste
     * @param <T>       le type des valeurs contenues dans les listes à (dé)sérialiser
     * @return un {@code Serde} capable de (dé)sérialiser des listes de valeurs du type donné
     */
    static <T> Serde<List<T>> listOf(Serde<T> serde, char delimiter) {
        return Serde.of(list -> list.stream()
                        .map(serde::serialize)
                        .collect(Collectors.joining(String.valueOf(delimiter))),
                str -> {
                    String[] splits = str.split(Pattern.quote(String.valueOf(delimiter)), -1);
                    return str.isEmpty() ?
                            List.of() :
                            Arrays.stream(splits)
                                    .map(serde::deserialize)
                                    .collect(Collectors.toList());
                });
    }

    /**
     * Crée un {@code Serde} capable de (dé)sérialiser un multi-ensemble de valeurs du type donné.
     * Ces dernières sont (dé)sérialisées à l'aide du {@code Serde} donné.
     *
     * @param serde     le {@code Serde} utilisé pour (dé)sérialiser les valeurs du type donné
     * @param delimiter le caractère de séparation utilisé pour délimiter les valeurs du multi-ensemble
     * @param <T>       le type des valeurs contenues dans les multi-ensembles à (dé)sérialiser
     * @return un {@code Serde} capable de (dé)sérialiser des multi-ensembles de valeurs du type donné
     */
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, char delimiter) {
        Serde<List<T>> listSerde = Serde.listOf(serde, delimiter);
        return Serde.of(bag -> listSerde.serialize(bag.toList()), str -> SortedBag.of(listSerde.deserialize(str)));
    }

    /**
     * Sérialise l'objet donné et retourne la chaîne de caractères correspondante.
     *
     * @param obj l'objet à sérialiser
     * @return la chaîne de caractères correspondant à la sérialisation de l'objet donné
     */
    String serialize(E obj);

    /**
     * Désérialise la chaîne de caractères donnée et retourne l'objet correspondant.
     *
     * @param str la chaîne de caractères à désérialiser
     * @return l'objet correspondant à la désérialisation de la chaîne de caractères donnée
     */
    E deserialize(String str);
}