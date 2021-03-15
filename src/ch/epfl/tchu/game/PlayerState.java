package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Représente l'état complet d'un joueur.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * Construit l'état d'un joueur possédant les billets, cartes et routes donnés.
     *
     * @param tickets
     *          les tickets que possède le joueur
     * @param cards
     *          les cartes que possède le joueur
     * @param routes
     *          les routes que le joueur s'est emparé
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);

        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * Retourne l'état initial d'un joueur auquel les cartes initiales données ont été distribuées.
     *
     * @param initialCards
     *          les cartes initiales du joueur
     * @throws IllegalArgumentException
     *          si le nombre de cartes initiales ne vaut pas 4
     * @return l'état initial d'un joueur auquel les cartes initiales données ont été distribuées
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), SortedBag.of(initialCards), List.of());
    }

    /**
     * Retourne les tickets du joueur.
     * @return les tickets du joueur
     */
    public SortedBag<Ticket> tickets() {
        return tickets;
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les billets donnés.
     *
     * @param newTickets
     *          les billets données
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus les billets donnés
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(tickets.union(newTickets), cards, routes());
    }

    /**
     * Retourne les cartes wagon/locomotive du joueur.
     * @return les cartes wagon/locomotive du joueur
     */
    public SortedBag<Card> cards() {
        return cards;
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus la carte donnée.
     *
     * @param card
     *          la carte donnée
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus la carte donnée
     */
    public PlayerState withAddedCards(Card card) {
        return new PlayerState(tickets, cards.union(SortedBag.of(card)), routes());
    }

    /**
     * Retourne un état identique au récepteur, si ce n'est que le joueur possède en plus les cartes données.
     *
     * @param additionalCards
     *          les cartes données
     * @return un état identique au récepteur, si ce n'est que le joueur possède en plus les cartes données
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets, cards.union(additionalCards), routes());
    }

    /**
     * Retourne vrai ssi le joueur peut s'emparer de la route donnée, c-à-d s'il lui
     * reste assez de wagons et s'il possède les cartes nécessaires.
     *
     * @param route
     *          la route donnée
     * @return vrai ssi le joueur peut s'emparer de la route donnée
     */
    public boolean canClaimRoute(Route route) {
        if (carCount() >= route.length()) {
            for (SortedBag<Card> oneOfAllPossibleCards : route.possibleClaimCards()) {
                if (cards.contains(oneOfAllPossibleCards))
                    return true;
            }
        }

        return false;
    }

    /**
     * Retourne la liste de tous les ensembles de cartes
     * que le joueur pourrait utiliser pour prendre possession de la route donnée.
     *
     * @param route
     *          la route donnée
     * @throws IllegalArgumentException
     *          si le nombre de wagon que possède le joueur est inférieur à la longueur de la route
     * @return la liste de tous les ensembles de cartes que le joueur
     * pourrait utiliser pour prendre possession de la route donnée
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());

        List<SortedBag<Card>> possibleCards = new ArrayList<>();
        for (SortedBag<Card> oneOfAllPossibleCards : route.possibleClaimCards()) {
            if (this.cards.contains(oneOfAllPossibleCards))
                possibleCards.add(oneOfAllPossibleCards);
        }

        return possibleCards;
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur pourrait utiliser pour s'emparer d'un tunnel.
     *
     * @param additionalCardsCount
     *          le nombre de cartes additionnelles que le joueurs devrait poser en plus pour claim le tunnel
     * @param initialCards
     *          les cartes initialement posées par le joueur pour s'emparer du tunnel
     * @param drawnCards
     *          les cartes tirées au sommet de la pioche
     * @throws IllegalArgumentException si le nombre de cartes additionnelles n'est pas compris entre 1 et 3 (inclus),
     *                                  si l'ensemble des cartes initiales est vide ou contient plus de 2 types de cartes différents,
     *                                  ou si l'ensemble des cartes tirées ne contient pas exactement 3 cartes.
     * @return la liste de cartes mentionnée
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount, SortedBag<Card> initialCards, SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount > 0 && additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        //1. Create a set of all possible cards in our hands (minus the initial cards)
        Card initialCardType = Card.LOCOMOTIVE;
        for (Card initialCard : initialCards.toSet()) {
            if (initialCard != Card.LOCOMOTIVE)
                initialCardType = initialCard;
        }

        SortedBag.Builder<Card> possibleCardsBuilder = new SortedBag.Builder<>();
        for (Card card: cards) {
            if (card == Card.LOCOMOTIVE || card == initialCardType)
                possibleCardsBuilder.add(card);
        }

        SortedBag<Card> possibleCardsInHand = possibleCardsBuilder.build().difference(initialCards);

        //2. Create all possible subsets and put it in a list
        Set<SortedBag<Card>> optionsSet = possibleCardsInHand.subsetsOfSize(additionalCardsCount);
        List<SortedBag<Card>> optionsList = new ArrayList<>(optionsSet);

        //3. Sort the list
        optionsList.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return optionsList;
    }

    /**
     * Retourne un état identique au récepteur,
     * si ce n'est que le joueur s'est de plus emparé de la route donnée au moyen des cartes données.
     * @param route
     *          la route dont le joueur s'est emparée
     * @param claimCards
     *          carte posée pour s'emparer de la route
     * @return un état identique au récepteur,
     * si ce n'est que le joueur s'est de plus emparé de la route donnée au moyen des cartes données
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards){
        //TODO: après rendu sem. 6: vérifier que le joueur ait bien les cartes.
        SortedBag<Card> newCards = cards.difference(claimCards);

        List<Route> newRoutes = new ArrayList<>(routes());
        newRoutes.add(route);

        return new PlayerState(tickets, newCards, newRoutes);
    }

    /**
     * Retourne le nombre de points (éventuellement négatif) obtenus par le joueur grâce à ses billets.
     * @return le nombre de points obtenus par le joueur grâce à ses billets
     */
    public int ticketPoints(){
        int maxIndex = 0;
        for (Route route: routes()) {
            maxIndex = route.station1().id() > maxIndex ? route.station1().id() : maxIndex;
            maxIndex = route.station2().id() > maxIndex ? route.station2().id() : maxIndex;
        }

        StationPartition.Builder connectivityBuilder = new StationPartition.Builder(maxIndex + 1);
        StationPartition connectivity = connectivityBuilder.build();

        int points = 0;
        for (Ticket ticket: tickets) {
            points += ticket.points(connectivity);
        }

        return points;
    }

    /**
     * Retourne la totalité des points obtenus par le joueur à la fin de la partie.
     * @return la totalité des points obtenus par le joueur à la fin de la partie
     */
    public int finalPoints(){
        return claimPoints() + ticketPoints();
    }
}

















