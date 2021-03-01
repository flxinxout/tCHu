package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Deck<C extends Comparable<C>> {

    private final List<C> cards;

    /**
     * Constructeur d'un tas de cartes d'un type spécifique
     * @param cards la liste de cartes constituant le tas
     */
    private Deck(List<C> cards) {
        this.cards = cards;
    }

    /**
     * Méthode retournant un tas de cartes ayant les mêmes cartes que
     * le multiensemble cards, mélangées au moyen du générateur de nombres aléatoires rng
     * @param cards le multi-ensemble de cartes
     * @param rgn un générateur de nombres aléatoires
     * @param <C> le type de cartes contenu dans cards et retourner
     * @return n tas de cartes ayant les mêmes cartes que
     *      * le multiensemble cards, mélangées au moyen du générateur de nombres aléatoires rng
     */
    public static <C extends Comparable<C>> Deck<C> of(SortedBag<C> cards, Random rgn) {
        List<C> newCardsList = cards.toList();
        Collections.shuffle(newCardsList, rgn);
        return new Deck<>(newCardsList);
    }

    /**
     * Retourne le nombre de cartes contenu dans la liste
     * @return le nombre de cartes contenu dans la liste
     * @throws IllegalArgumentException si la size de la liste est vide
     */
    public int size() {
        Preconditions.checkArgument(!cards.isEmpty());
        return cards.size();
    }

    /**
     * Retourne true:= si la liste est vide, sinon false
     * @return true:= si la liste est vide, sinon false
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Retourne la carte au dessus du deck
     * @return la carte au dessus du deck
     * @throws IllegalArgumentException si la size de la liste est vide
     */
    public C topCard() {
        Preconditions.checkArgument(isEmpty());
        return cards.get(0);
    }

    /**
     * Retourne un tas identique au récepteur (this) mais sans la carte au sommet
     * @throws IllegalArgumentException si le tas est vide
     * @return un tas identique au récepteur (this) mais sans la carte au sommet
     */
    public Deck<C> withoutTopCard() {
        Preconditions.checkArgument(isEmpty());
        //TODO est-ce que c'est mieux de use le subList que le topCard ? Je pense que oui car on retourne rapidement une nouvelle liste là
        return new Deck<>(cards.subList(1, cards.size()));
    }

    /**
     * Retourne un multiensemble contenant les count cartes se trouvant au sommet du tas
     * @param count le nombre de cartes retournées
     * @throws IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     * @return un multiensemble contenant les count cartes se trouvant au sommet du tas
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
     * Retourne un tas identique au récepteur (this) mais sans les count cartes du sommet
     * @param count le nombre de cartes retournées
     * @throws IllegalArgumentException si count n'est pas compris entre 0 (inclus) et la taille du tas (incluse)
     * @return un tas identique au récepteur (this) mais sans les count cartes du sommet
     */
    public Deck<C> withoutTopCards(int count) {
        List<C> newCardList = new ArrayList<>();
        for(int i = count; i < cards.size(); i++) {
            newCardList.add(cards.get(i));
        }
        return new Deck<>(newCardList);
    }

}
