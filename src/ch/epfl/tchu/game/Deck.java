package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Un tas de cartes.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Deck<C extends Comparable<C>> {

    private final List<C> cards;

    /**
     * Construit un tas de cartes de type {@code <C>}.
     */
    private Deck(List<C> cards) {
        this.cards = List.copyOf(cards);
    }

    /**
     * Retourne un tas de cartes composé de {@code cards}, mélangées au moyen de {@code rng}.
     *
     * @param cards le multi-ensemble de cartes
     * @param rng   le générateur de nombres aléatoires
     * @param <C>   le type des cartes
     * @return le tas de cartes
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rng) {
        final List<C> newCardsList = cards.toList();
        Collections.shuffle(newCardsList, rng);
        return new Deck<>(newCardsList);
    }

    /**
     * @return le nombre de cartes que ce tas contient
     */
    public int size() {
        return cards.size();
    }

    /**
     * @return ssi ce tas est vide
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * @return la carte au sommet de ce tas
     * @throws IllegalArgumentException si le tas est vide
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());
        return cards.get(0);
    }

    /**
     * Retourne un tas identique au récepteur ({@code this}) mais sans la carte au sommet.
     *
     * @return un tas identique au récepteur ({@code this}) mais sans la carte au sommet
     * @throws IllegalArgumentException si le tas est vide
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!isEmpty());

        final List<C> newListOfCards = cards.subList(1, size());
        return new Deck<>(newListOfCards);
    }

    /**
     * Retourne un multi-ensemble contenant les {@code count} cartes se trouvant au sommet de ce tas
     *
     * @param count le nombre de cartes à retourner
     * @return un multi-ensemble contenant les {@code count} cartes se trouvant au sommet du tas
     * @throws IllegalArgumentException si {@code count} n'est pas compris entre 0 et la taille du tas (inclus)
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());

        final SortedBag.Builder<C> builder = new SortedBag.Builder<>();
        for (int i = 0; i < count; i++) {
            builder.add(cards.get(i));
        }
        return builder.build();
    }

    /**
     * Retourne un tas identique au récepteur ({@code this}) mais sans les {@code count} cartes du sommet
     *
     * @param count le nombre de cartes à enlever du sommet de ce tas
     * @return un tas identique au récepteur ({@code this}) mais sans les {@code count} cartes du sommet
     * @throws IllegalArgumentException si {@code count} n'est pas compris entre 0 et la taille du tas (inclus)
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= size());

        final List<C> newListOfCards = cards.subList(count, size());
        return new Deck<>(newListOfCards);
    }
}
