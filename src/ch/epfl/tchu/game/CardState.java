package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * Partie totale (publique et privée) de l'état des cartes wagon/locomotive qui ne sont pas en main des joueurs.
 * En plus de la partie publique, elle possède également la pioche et la défausse, inconnues des joueurs.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class CardState extends PublicCardState {

    private final Deck<Card> deck;
    private final SortedBag<Card> discards;

    /**
     * Construit un état total des cartes dans lequel
     * les cartes face visible, la pioche et la défausse sont celles données.
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discards) {
        super(faceUpCards, deck.size(), discards.size());
        this.deck = deck;
        this.discards = discards;
    }

    /**
     * Retourne un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières de {@code deck},
     * la pioche est constituée des cartes de {@code deck} restantes, et la défausse est vide.
     *
     * @param deck le tas de cartes donné
     * @return un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières de {@code deck},
     * la pioche est constituée des cartes de {@code deck} restantes, et la défausse est vide.
     * @throws IllegalArgumentException si {@code deck} contient moins de 5 cartes
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= Constants.FACE_UP_CARDS_COUNT);

        //On n'appelle pas deck.topCards(Constants.FACE_UP_CARDS_COUNT) car ça trierait les cartes automatiquement
        //à cause du SortedBag, ce n'est pas le comportement voulu.
        List<Card> newTopCards = new ArrayList<>();
        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            newTopCards.add(deck.topCard());
            deck = deck.withoutTopCard();
        }

        return new CardState(newTopCards, deck, SortedBag.of());
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur ({@code this}),
     * si ce n'est que la carte face visible d'index {@code slot} a été remplacée
     * par celle se trouvant au sommet de la pioche, qui en est du même coup retirée.
     *
     * @param slot l'index des cartes face visible à remplacer
     * @return un ensemble de cartes identique au récepteur ({@code this}), si ce n'est que la carte face visible
     * d'index {@code slot} a été remplacée par celle se trouvant au sommet de la pioche
     * @throws IndexOutOfBoundsException si {@code slot} ne se trouve pas entre 0 (inclus) et 5 (exclu)
     * @throws IllegalArgumentException  si la pioche est vide
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, faceUpCards().size());
        Preconditions.checkArgument(!isDeckEmpty());

        List<Card> newCardsFaceUp = new ArrayList<>(faceUpCards());
        newCardsFaceUp.set(slot, deck.topCard());

        return new CardState(newCardsFaceUp, deck.withoutTopCard(), discards);
    }

    /**
     * @return la carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return deck.topCard();
    }

    /**
     * @return un ensemble de cartes identique au récepteur ({@code this}),
     * mais sans la carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());

        return new CardState(faceUpCards(), deck.withoutTopCard(), discards);
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur ({@code this}), si ce n'est que les cartes de la défausse
     * ont été mélangée au moyen du générateur aléatoire {@code rng} afin de constituer la nouvelle pioche.
     *
     * @return un ensemble de cartes identique au récepteur ({@code this}), si ce n'est que les cartes de la défausse
     * ont été mélangée au moyen du générateur aléatoire {@code rng} afin de constituer la nouvelle pioche.
     * @throws IllegalArgumentException si la pioche n'est pas vide
     */
    public CardState withDeckRecreatedFromDiscards(Random rng) {
        Preconditions.checkArgument(isDeckEmpty());

        Deck<Card> newDeck = Deck.of(discards, rng);
        return new CardState(faceUpCards(), newDeck, SortedBag.of());
    }

    /**
     * @return un ensemble de cartes identique au récepteur ({@code this}),
     * mais avec les cartes données ajoutées à la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards(), deck, discards.union(additionalDiscards));
    }
}














