package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CardState extends PublicCardState {

    private final Deck<Card> deckCards;
    private final SortedBag<Card> discardsDeck;

    /**
     * Constructeur d'un objet constitué de la partie privée des cartes du jeu
     *
     * @param faceUpCards  les cartes faces visibles à côté du plateau de jeu
     * @param cardsDeck    la pioche
     * @param discardsDeck la défausse
     */
    private CardState(List<Card> faceUpCards, Deck<Card> cardsDeck, SortedBag<Card> discardsDeck) {
        super(faceUpCards, cardsDeck.size(), discardsDeck.size());
        this.deckCards = cardsDeck;
        this.discardsDeck = discardsDeck;
    }

    /**
     * Retourne un état dans lequel les 5 cartes disposées faces visibles sont les 5 premières du tas donné,
     * la pioche est constituée des cartes du tas restantes, et la défausse est vide
     * @param deck le deck contenant les cartes possibles cartes utilisées pour construire le CardState
     * @return un cardState
     */
    public static CardState of(Deck<Card> deck) {
        Preconditions.checkArgument(deck.size() >= 5);
        Deck<Card> cardsDeck = deck.withoutTopCards(5);
        return new CardState(deck.topCards(5).toList(), cardsDeck, SortedBag.of());
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), si ce n'est que la carte face visible
     * d'index slot a été remplacée par celle se trouvant au sommet de la pioche, qui en est du même coup retirée 
     * @param slot le slot en question des cartes visibles
     * @return un nouvelle ensemble de cartes
     */
    public CardState withDrawnFaceUpCard(int slot) {
        Objects.checkIndex(slot, faceUpCards().size());
        Preconditions.checkArgument(deckSize() > 0);

        Card cardFromIndex = deckCards.topCard();
        List<Card> newCardsFaceUp = new ArrayList<>();
        newCardsFaceUp.set(slot, cardFromIndex);
        Deck<Card> newDeckCard = deckCards.withoutTopCard();
        for(int s : Constants.FACE_UP_CARD_SLOTS) {
            if(s != slot) newCardsFaceUp.add(faceUpCards().get(s));
        }
        return new CardState(newCardsFaceUp, newDeckCard, discardsDeck);
    }

    /**
     * Retourne la carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     * @return la carte se trouvant au sommet de la pioche,
     */
    public Card topDeckCard() {
        Preconditions.checkArgument(isDeckEmpty());
        return deckCards.topCard();
    }

    /**
     * Retourne un ensemble de cartes identique au récepteur (this), mais sans la
     * carte se trouvant au sommet de la pioche
     * @throws IllegalArgumentException si la pioche est vide
     * @return un ensemble de cartes identique au récepteur (this), mais sans la carte se trouvant au sommet de la pioche
     */
    public CardState withoutTopDeckCard() {
        List<Card> newCards = new ArrayList<>();
         //TODO JME SUIS ARRETER Là
        return new CardState(faceUpCards(), );
    }
}














