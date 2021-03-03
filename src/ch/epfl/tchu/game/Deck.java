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
     * Constructeur d'un tas de cartes d'un type spécifique
     * @param cards
     *          la liste de cartes constituant le tas
     */
    private Deck(List<C> cards) {
        this.cards = cards;
    }

    /**
     * Méthode retournant un tas de cartes ayant les mêmes cartes que
     * le multi-ensemble de cartes donné, mélangées au moyen d'un générateur de nombres aléatoires.
     * @param cards
     *          le multi-ensemble de cartes
     * @param rgn
     *          un générateur de nombres aléatoires
     * @param <C>
     *           le type des cartes
     * @return le tas de cartes
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rgn) {
        List<C> newCardsList = cards.toList();
        Collections.shuffle(newCardsList, rgn);
        return new Deck<>(newCardsList);
    }

    /**
     * Retourne le nombre de cartes que ce tas contient.
     * @return le nombre de cartes que ce tas contient
     */
    public int size() {
        return cards.size();
    }

    /**
     * Retourne vrai ssi le tas est vide.
     * @return  vrai ssi le tas est vide
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Retourne la carte au sommet du tas.
     * @throws IllegalArgumentException
     *          si le tas est vide
     * @return la carte au sommet du tas
     */
    public C topCard() {
        Preconditions.checkArgument(!isEmpty());
        return cards.get(0);
    }

    /**
     * Retourne un tas identique au récepteur ({@code this}) mais sans la carte au sommet
     * @throws IllegalArgumentException
     *          si le tas est vide
     * @return un tas identique au récepteur ({@code this}) mais sans la carte au sommet
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(!isEmpty());

        //TODO interesant, compare to dans enum pour une comparaison bien fait.
        List<C> newListOfCards = new ArrayList<>();
        for(C card : cards)
            if(!card.equals(topCard())) newListOfCards.add(card);


        return new Deck<>(newListOfCards);
    }

    /**
     * Retourne un multi-ensemble contenant les {@code count} cartes se trouvant au sommet du tas
     * @param count
     *          le nombre de cartes retournées
     * @throws IllegalArgumentException
     *          si {@code count} n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     * @return un multi-ensemble contenant les {@code count} cartes se trouvant au sommet du tas
     */
    public SortedBag<C> topCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());

        SortedBag.Builder<C> builder = new SortedBag.Builder<>();
        for(int i = 0; i < count; i++) {
            builder.add(cards.get(i));
        }
        return builder.build();
    }

    /**
     * Retourne un tas identique au récepteur ({@code this}) mais sans les {@code count} cartes du sommet
     * @param count
     *          le nombre de cartes retournées
     * @throws IllegalArgumentException
     *          si {@code count} n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     * @return un tas identique au récepteur ({@code this}) mais sans les {@code count} cartes du sommet
     */
    public Deck<C> withoutTopCards(int count) {
        Preconditions.checkArgument(count >= 0 && count <= cards.size());

        //TODO interesant, compare to dans enum pour une comparaison bien fait.
        List<C> newListOfCards = new ArrayList<>();
        SortedBag<C> cardsFromTheTop = topCards(count);
        for(C card : cards) {
            if(!cardsFromTheTop.contains(card)) newListOfCards.add(card);
        }

        return new Deck<>(newListOfCards);
    }

}
