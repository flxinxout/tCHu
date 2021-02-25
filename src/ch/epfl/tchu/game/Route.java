package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Une route reliant deux villes voisines.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Route {

    /**
     * Énumération des deux niveaux possibles pour une route
     */
    public enum Level {

        OVERGROUND,
        UNDERGROUND
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Level level;
    private final Color color;

    /**
     * Construit une route avec l'identité, les gares, la longueur,
     * le niveau et la couleur donnée.
     *
     * @param id
     *            l'identité de la route (non nulle)
     * @param station1
     *            la première gare (non nulle)
     * @param station2
     *            la deuxième gare (non nulle)
     * @param length
     *            la longueur de la route (doit être comprise dans les limites acceptables)
     * @param level
     *            le niveau de la route (sous/sur-terrain) (non nul)
     * @param color
     *            la couleur de la route (peut être nulle)
     * @throws IllegalArgumentException
     *            si les deux gares sont les mêmes
     * @throws NullPointerException
     *            si l'identité est nulle,
     *            ou si la première gare est nulle,
     *            ou si la deuxième gare est nulle,
     *            ou si le niveau est nul
     */
    public Route(String id, Station station1, Station station2, int length, Level level, Color color) {
        Preconditions.checkArgument(!station1.equals(station2));

        Objects.requireNonNull(this.id = id);
        Objects.requireNonNull(this.station1 = station1);
        Objects.requireNonNull(this.station2 = station2);
        this.length = length;
        Objects.requireNonNull(this.level = level);
        this.color = color;
    }

    /**
     * Retourne l'identité de cette route.
     * @return l'identité de cette route
     */
    public String id() {
        return id;
    }

    /**
     * Retourne la première gare de cette route.
     * @return la première gare de cette route
     */
    public Station station1() {
        return station1;
    }

    /**
     * Retourne la deuxième gare de cette route.
     * @return la deuxième gare de cette route
     */
    public Station station2() {
        return station2;
    }

    /**
     * Retourne la longueur de cette route.
     * @return la longueur de cette route
     */
    public int length() {
        return length;
    }

    /**
     * Retourne le niveau de cette route.
     * @return le niveau de cette route
     */
    public Level level() {
        return level;
    }

    /**
     * Retourne la couleur de cette route (peut être null)
     * @return la couleur de cette route
     */
    public Color color() {
        return color;
    }

    /**
     * Retourne la liste des deux gares de cette route,
     * dans l'ordre dans lequel elles ont été passées au constructeur.
     * @return la liste des deux gares de cette route
     */
    public List<Station> stations() {
        return List.of(station1, station2);
    }

    /**
     * Retourne la gare de la route qui n'est pas celle donnée.
     *
     * @throws IllegalArgumentException
     *            si la gare donnée n'est égale à aucune des deux de cette route
     * @return la gare de la route qui n'est pas celle donnée
     */
    public Station stationOpposite(Station station){
        Preconditions.checkArgument(station1.equals(station) || station2.equals(station));
        return station1.equals(station) ? station2 : station1;
    }

    /**
     * Retourne la liste de tous les ensembles de cartes qui pourraient être joués
     * pour (tenter de) s'emparer de la route,
     * trié par ordre croissant de nombre de cartes locomotive, puis par couleur.
     *
     * @return la liste de tous les ensembles de cartes qui pourraient être joués
     * pour (tenter de) s'emparer de la route
     */
    public List<SortedBag<Card>> possibleClaimCards(){
        List<SortedBag<Card>> possibleClaimCardsList = new ArrayList<>();

        //Si la route n'est pas de couleur neutre
        if (color != null) {
            //Premier ensemble : color - color - ... - color
            SortedBag<Card> colorCards = SortedBag.of(length, Card.of(color));
            possibleClaimCardsList.add(colorCards);

            //Créer les ensembles :
            // color - color - ... - locomotive - ...
            // ...
            // color - locomotive - ...
            // locomotive - locomotive - ...
            if (level == Level.UNDERGROUND){
                for (int i = 1; i <= length; i++) {
                    SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();

                    cardsBuilder.add(length - i, Card.of(color));
                    cardsBuilder.add(i, Card.LOCOMOTIVE);
                    possibleClaimCardsList.add(cardsBuilder.build());
                }
            }
        }
        //Si la route est de couleur neutre
        else {
            if (level == Level.OVERGROUND) {

                //Créer les ensembles:
                // BLACK - ... - BLACK
                // BLUE - ... - BLUE
                // ...
                for (Card car : Card.CARS) {
                    SortedBag<Card> colorCards = SortedBag.of(length, car);
                    possibleClaimCardsList.add(colorCards);
                }
            }
            else {
                //Créer les ensembles:
                // BLACK - BLACK - ... - locomotive - ...
                // BLUE - BLUE - ... - locomotive - ...
                // ...
                // BLACK - locomotive - ...
                // BLUE - locomotive - ...
                // ...
                // locomotive - locomotive - ...
                for (int i = 1; i <= length; i++) {
                    for (Card car: Card.CARS) {
                        SortedBag.Builder<Card> cardsBuilder = new SortedBag.Builder<>();
                        cardsBuilder.add(length - i, car);
                        cardsBuilder.add(i, Card.LOCOMOTIVE);
                        possibleClaimCardsList.add(cardsBuilder.build());
                    }
                }
            }
        }

        return possibleClaimCardsList;
    }

    /**
     * Retourne le nombre de cartes additionnelles à jouer
     * pour s'emparer de la route (en tunnel).
     *
     * @return le nombre de cartes additionnelles à jouer pour s'emparer de la route
     */
    public int additionalClaimCardsCount(SortedBag<Card> claimCards, SortedBag<Card> drawnCards){
        Preconditions.checkArgument(level == Level.UNDERGROUND || drawnCards.size() == 3);
    }

    /**
     * Retourne le nombre de points de construction
     * qu'un joueur obtient lorsqu'il s'empare de cette route.
     *
     * @return le nombre de points de construction de cette route
     */
    public int claimPoints(){
        return Constants.ROUTE_CLAIM_POINTS.get(length);
    }
}
