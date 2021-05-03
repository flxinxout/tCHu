package ch.epfl.tchu.game;

import ch.epfl.tchu.SortedBag;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

import static ch.epfl.tchu.gui.Info.cardName;
import static ch.epfl.tchu.gui.StringsFr.AND_SEPARATOR;

/**
 * Classe permettant de convertir certaines chaînes de caractères en d'autres.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
//TODO: c'est bien de la définir à l'extérieur de GraphicalPlayer? On pourrait l'utiliser dans Info aussi
public class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

    /**
     * Retourne la représentation textuelle de l'ensemble de cartes donné.
     *
     * @param cards l'ensemble de cartes
     * @return la représentation textuelle de l'ensemble de cartes donné
     */
    @Override
    public String toString(SortedBag<Card> cards) {
        final List<String> singleCardNames = new ArrayList<>();

        for (Card c : cards.toSet()) {
            int n = cards.countOf(c);
            singleCardNames.add(n + " " + cardName(c, n));
        }

        final int lastCardIndex = singleCardNames.size() - 1;
        String joined;
        if (singleCardNames.size() == 1) {
            joined = singleCardNames.get(0);
        } else {
            joined = String.join(AND_SEPARATOR,
                    String.join(", ", singleCardNames.subList(0, lastCardIndex)),
                    singleCardNames.get(lastCardIndex));
        }

        return joined;
    }

    @Override
    public SortedBag<Card> fromString(String string) {
        throw new UnsupportedOperationException();
    }
}