package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * Partie publique (connue des deux joueurs) de l'état des cartes wagon/locomotive qui ne sont pas en main des joueurs.
 * Elle est composée des cartes face visible, de la taille de la pioche ainsi que la taille de la défausse.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Construit un état public des cartes dans lequel les cartes face visible sont {@code faceUpCards},
     * la pioche contient {@code deckSize} cartes et la défausse en contient {@code discardsSize}.
     *
     * @param faceUpCards  les cartes faces visibles
     * @param deckSize     la taille de la pioche
     * @param discardsSize la taille de défausse
     * @throws IllegalArgumentException si {@code faceUpCards} ne contient pas 5 éléments
     *                                  si {@code deckSize} ou {@code discardsSize} < 0
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >= 0 && discardsSize >= 0);

        this.faceUpCards = List.copyOf(faceUpCards);
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Retourne la carte face visible à l'index {@code slot}.
     *
     * @param slot l'index donné
     * @return la carte face visible à l'index {@code slot}
     * @throws IndexOutOfBoundsException si {@code slot} n'est pas compris entre 0 (inclus)
     *                                   et le nombre de cartes face visible (exclu)
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, this.faceUpCards.size());
        return this.faceUpCards.get(slot);
    }

    /**
     * Retourne le nombre total de cartes qui ne sont pas en main des joueurs,
     * à savoir les 5 dont la face est visible, celles de la pioche et celles de la défausse
     *
     * @return le nombre total de cartes qui ne sont pas en main des joueurs
     */
    public int totalSize() {
        return this.faceUpCards.size() + this.deckSize + this.discardsSize;
    }

    /**
     * Retourne les 5 cartes face visible, sous la forme d'une liste comportant exactement 5 éléments.
     *
     * @return les 5 cartes face visible
     */
    public List<Card> faceUpCards() {
        return this.faceUpCards;
    }

    /**
     * Retourne la taille de la pioche.
     *
     * @return la taille de la pioche
     */
    public int deckSize() {
        return this.deckSize;
    }

    /**
     * Retourne ssi la pioche est vide.
     *
     * @return ssi la pioche est vide
     */
    public boolean isDeckEmpty() {
        return this.deckSize == 0;
    }

    /**
     * Retourne la taille de la défausse.
     *
     * @return la taille de la défausse
     */
    public int discardsSize() {
        return this.discardsSize;
    }
}














