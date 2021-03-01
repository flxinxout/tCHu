package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.List;
import java.util.Objects;

public class PublicCardState {

    private final List<Card> faceUpCards;
    private final int deckSize;
    private final int discardsSize;

    /**
     * Constructeur d'un objet constitué de la partie publique des cartes du jeu
     * @param faceUpCards les cartes faces visibles à côté du plateau de jeu
     * @param deckSize la taille de la pioche
     * @param discardsSize la taille de défausse
     */
    public PublicCardState(List<Card> faceUpCards, int deckSize, int discardsSize) {
        Preconditions.checkArgument(faceUpCards.size() != 5 && deckSize > 0 && discardsSize > 0);

        this.faceUpCards = faceUpCards;
        this.deckSize = deckSize;
        this.discardsSize = discardsSize;
    }

    /**
     * Retourne le nombre total de cartes qui ne sont pas en main des joueurs,
     * à savoir les 5 dont la face est visible, celles de la pioche et celles de la défausse
     * @return la size totale
     */
    public int totalSize() {
        return faceUpCards.size() + deckSize + discardsSize;
    }

    /**
     * Retourne les 5 cartes face visible, sous la forme d'une liste comportant exactement 5 éléments
     * @return les 5 cartes face visible, sous la forme d'une liste comportant exactement 5 éléments
     */
    public List<Card> faceUpCards() {
        return faceUpCards;
    }

    /**
     * Retourne la carte face visible à l'index donné
     * @param slot l'index en question
     * @return la carte face visible à l'index donné
     */
    public Card faceUpCard(int slot) {
        Objects.checkIndex(slot, faceUpCards.size());
        return faceUpCards.get(slot);
    }

    /**
     * Retourne la taille de la pioche
     * @return la taille de la pioche
     */
    public int deckSize() {
        return deckSize;
    }

    /**
     * Retourne true:= si la liste est vide, false sinon
     * @return true:= si la liste est vide, false sinon
     */
    public boolean isDeckEmpty() {
        return faceUpCards.isEmpty();
    }

    /**
     * Retourne la taille de la défausse
     * @return la taille de la défausse
     */
    public int discardsSize() {
        return discardsSize;
    }
}














