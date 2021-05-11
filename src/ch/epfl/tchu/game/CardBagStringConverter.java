package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.gui.Info.cardName;
import static ch.epfl.tchu.gui.StringsFr.AND_SEPARATOR;

/**
 * Convertit un multiensemble de cartes en une chaîne de caractères.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    /**
     * Le nombre maximal de types de cartes différents autorisé pour s'emparer d'une route.
     */
    private static int CARD_TYPES_COUNT = 2;

    /**
     * Retourne la représentation textuelle de l'ensemble de cartes donné. Il ne doit pas contenir plus de 2 types
     * de carte différents.
     *
     * @param cards l'ensemble de cartes
     * @return la représentation textuelle de l'ensemble de cartes donné
     * @throws IllegalArgumentException si l'ensemble de cartes contient plus de deux types de carte différents
     */
    @Override
    public String toString(SortedBag<Card> cards) {
        Preconditions.checkArgument(cards.toSet().size() <= CARD_TYPES_COUNT);
        List<String> singleCardNames = new ArrayList<>(CARD_TYPES_COUNT);

        for (Card c : cards.toSet()) {
            int n = cards.countOf(c);
            singleCardNames.add(n + " " + cardName(c, n));
        }

        return String.join(AND_SEPARATOR, singleCardNames);
    }

    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}