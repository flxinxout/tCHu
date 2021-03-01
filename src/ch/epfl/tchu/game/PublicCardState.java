package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * L'état public des cartes wagon/locomotive qui ne sont pas en main des joueurs
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Constructeur d'un objet constitué de la partie publique des cartes du jeu
     * @param faceUpCards
     *          les cartes faces visibles à côté du plateau de jeu
     * @param deckSize
     *          la taille de la pioche
     * @param discardsSize
     *          la taille de défausse
     * @throws IllegalArgumentException
     *          si il n'y a pas {@code Constants.FACE_UP_CARDS_COUNT} cartes faces visibles ou
     *          si {@code deckSize} ou {@code discardsSize} < 0
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() == Constants.FACE_UP_CARDS_COUNT);
        Preconditions.checkArgument(deckSize >= 0 && discardsSize >= 0);

        this.faceUpCards = faceUpCards;
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Retourne le nombre total de cartes qui ne sont pas en main des joueurs,
     * à savoir les 5 dont la face est visible, celles de la pioche et celles de la défausse
     * @return le nombre total de cartes qui ne sont pas en main des joueurs
     */
    public int totalSize() {
        return faceUpCards.size() + deckSize + discardsSize;
    }

    /**
     * Retourne les 5 cartes face visible, sous la forme d'une liste comportant exactement 5 éléments.
     * @return les 5 cartes face visible, sous la forme d'une liste comportant exactement 5 éléments
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * Retourne la carte face visible à l'index donné.
     * @param slot
     *          l'index en question
     * @return la carte face visible à l'index donné
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, faceUpCards.size());
        return faceUpCards.get(slot);
    }

    /**
     * Retourne la taille de la pioche.
     * @return la taille de la pioche
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Retourne vrai ssi la pioche est vide.
     * @return vrai ssi la pioche est vide
     */
    public boolean isDeckEmpty() {
        return deckSize == 0;
    }

    /**
     * Retourne la taille de la défausse.
     * @return la taille de la défausse
     */
    public int discardsSize() {
        return discardsSize;
    }
}














