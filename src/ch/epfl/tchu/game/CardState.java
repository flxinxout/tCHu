package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;

/**
 * L'état total des cartes wagon/locomotive qui ne sont pas en main des joueurs
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class CardState extends PublicCardState {

    private final Deck<Card> deck;
    private final SortedBag<Card> discards;

    /**
     * Constructeur d'un objet constitué de la partie privée des cartes du jeu
     *  @param faceUpCards
     *          les cartes faces visibles à côté du plateau de jeu
     * @param deck
     *          la pioche
     * @param discards
     *          la défausse
     */
    private CardState(List<Card> faceUpCards, Deck<Card> deck, SortedBag<Card> discards) {
        super(faceUpCards, deck.size(), discards.size());
        this.deck = deck;
        this.discards = discards;
    }

    /**
     * Retourne un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné,
     * la pioche est constituée des cartes du tas restantes, et la défausse est vide
     * @param deck
     *          la pioche
     * @throws IllegalArgumentException
     *          si le deck a moins de 5 éléments
     * @return l'état décrit ci-dessus
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= 5);
        Deck<Card> newDeck = deck.withoutTopCards(5);
        return new CardState(deck.topCards(5).toList(), newDeck, SortedBag.of());
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur ({@code this}),
     * si ce n'est que la carte face visible d'index {@code slot} a été remplacée
     * par celle se trouvant au sommet de la pioche, qui en est du même coup retirée.
     * @param slot
     *          l'index des cartes face visible à remplacer
     * @throws IndexOutOfBoundsException
     *          si {@code slot} ne se trouve pas entre 0 et 5 (exclu)
     * @throws IllegalArgumentException
     *          si la pioche est vide
     * @return le nouvel ensemble de cartes
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, faceUpCards().size());
        Preconditions.checkArgument(!isDeckEmpty());

        Card topCard = deck.topCard();

        List<Card> newCardsFaceUp = new ArrayList<>();
        for (int s: Constants.FACE_UP_CARD_SLOTS) {
            newCardsFaceUp.add(faceUpCard(s));
        }
        newCardsFaceUp.set(slot, topCard);

        Deck<Card> newDeck = deck.withoutTopCard();

        return new CardState(newCardsFaceUp, newDeck, discards);
    }

    /**
     * Retourne la carte se trouvant au sommet de la pioche.
     * @throws IllegalArgumentException
     *          si la pioche est vide
     * @return la carte se trouvant au sommet de la pioche,
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());
        return deck.topCard();
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur ({@code this}), mais sans la
     * carte se trouvant au sommet de la pioche.
     * @throws IllegalArgumentException
     *          si la pioche est vide
     * @return un ensemble de cartes identique au récepteur ({@code this}),
     * mais sans la carte se trouvant au sommet de la pioche
     */
    public CardState withoutTopDeckCard() {
        Preconditions.checkArgument(!isDeckEmpty());

        Deck<Card> newDeck = deck.withoutTopCard();
        return new CardState(faceUpCards(), newDeck, discards);
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur ({@code this}),
     * si ce n'est que les cartes de la défausse ont été mélangée au moyen du générateur aléatoire donné
     * afin de constituer la nouvelle pioche.
     * @throws IllegalArgumentException
     *          si la pioche n'est pas vide
     * @return Retourne un ensemble de cartes identique au récepteur ({@code this}),
     * si ce n'est que les cartes de la défausse ont été mélangée au moyen du générateur aléatoire donné
     */
    public CardState withDeckRecreatedFromDiscards(Random rng){
        Preconditions.checkArgument(isDeckEmpty());

        Deck<Card> newDeck = Deck.of(discards, new Random());
        return new CardState(faceUpCards(), newDeck, SortedBag.of());
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur ({@code this),
     * mais avec les cartes données ajoutées à la défausse.
     * @return un ensemble de cartes identique au récepteur ({@code this),
     * mais avec les cartes données ajoutées à la défausse
     */
    public CardState withMoreDiscardedCards(SortedBag<Card> additionalDiscards) {
        return new CardState(faceUpCards(), deck, discards.union(additionalDiscards));
    }
}














