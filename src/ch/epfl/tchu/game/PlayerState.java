package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Partie complète (publique et privée) de l'état d'un joueur. En plus de la partie publique, elle possède également
 * les tickets et les cartes du joueur, inconnus des autres joueurs.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class PlayerState extends PublicPlayerState {

    private final SortedBag<Ticket> tickets;
    private final SortedBag<Card> cards;

    /**
     * Construit l'état d'un joueur possédant les billets {@code tickets},
     * les cartes {@code cards} et les routes {@code routes}.
     *
     * @param tickets les tickets que possède le joueur
     * @param cards   les cartes que possède le joueur
     * @param routes  les routes dont le joueur s'est emparées
     */
    public PlayerState(SortedBag<Ticket> tickets, SortedBag<Card> cards, List<Route> routes) {
        super(tickets.size(), cards.size(), routes);

        this.tickets = tickets;
        this.cards = cards;
    }

    /**
     * Retourne l'état initial de ce joueur auquel les cartes initiales {@code initialCards} ont été distribuées;
     * dans cet état initial, ce joueur ne possède encore aucun billet, et ne s'est emparé d'aucune route.
     *
     * @param initialCards les cartes initiales de ce joueur
     * @return l'état initial de ce joueur auquel les cartes initiales données ont été distribuées
     * @throws IllegalArgumentException si {@code initialCards} ne contient pas exactement 4 éléments
     */
    public static PlayerState initial(SortedBag<Card> initialCards) {
        Preconditions.checkArgument(initialCards.size() == Constants.INITIAL_CARDS_COUNT);
        return new PlayerState(SortedBag.of(), initialCards, List.of());
    }

    /**
     * Retourne un état identique à celui-ci, si ce n'est que ce joueur possède en plus les billets {@code newTickets}.
     *
     * @param newTickets les billets données
     * @return un état identique à celui-ci, si ce n'est que ce joueur possède en plus les billets {@code newTickets}
     */
    public PlayerState withAddedTickets(SortedBag<Ticket> newTickets) {
        return new PlayerState(this.tickets.union(newTickets), this.cards, routes());
    }

    /**
     * Retourne un état identique à celui-ci, si ce n'est que ce joueur possède en plus la carte {@code card}.
     *
     * @param card la carte donnée
     * @return un état identique à celui-ci, si ce n'est que ce joueur possède en plus la carte {@code card}
     */
    public PlayerState withAddedCard(Card card) {
        return new PlayerState(tickets(), this.cards.union(SortedBag.of(card)), routes());
    }

    /**
     * Retourne un état identique à celui-ci,
     * si ce n'est que ce joueur possède en plus les cartes {@code additionalCards}.
     *
     * @param additionalCards les cartes additionnelles
     * @return un état identique à celui-ci,
     * si ce n'est que ce joueur possède en plus les cartes {@code additionalCards}
     */
    public PlayerState withAddedCards(SortedBag<Card> additionalCards) {
        return new PlayerState(tickets(), this.cards.union(additionalCards), routes());
    }

    /**
     * Retourne ssi le joueur peut s'emparer de la route {@code route}, c-à-d s'il lui
     * reste assez de wagons et s'il possède les cartes nécessaires.
     *
     * @param route la route concernée
     * @return ssi le joueur peut s'emparer de la route {@code route}
     */
    public boolean canClaimRoute(Route route) {
        return carCount() >= route.length() && route.possibleClaimCards().stream()
                .anyMatch(this.cards::contains);
    }

    /**
     * Retourne la liste de tous les ensembles de cartes
     * que ce joueur pourrait utiliser pour prendre possession de la route {@code route}.
     *
     * @param route la route donnée
     * @return la liste de tous les ensembles de cartes que ce joueur
     * pourrait utiliser pour prendre possession de la route {@code route}
     * @throws IllegalArgumentException si le nombre de wagon que possède ce joueur est inférieur
     *                                  à la longueur de la route
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        Preconditions.checkArgument(carCount() >= route.length());

        return route.possibleClaimCards().stream()
                .filter(this.cards::contains)
                .collect(Collectors.toList());
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que ce joueur pourrait utiliser pour s'emparer d'un tunnel,
     * trié par ordre croissant du nombre de cartes locomotives, sachant qu'il a initialement posé les cartes
     * {@code initialCards}, que les 3 cartes tirées du sommet de la pioche sont {@code drawnCards},
     * et que ces dernières forcent le joueur à poser encore {@code additionalCardsCount} cartes.
     *
     * @param additionalCardsCount le nombre de cartes additionnelles pour s'emparer du tunnel
     * @param initialCards         les cartes initialement posées par le joueur pour s'emparer du tunnel
     * @param drawnCards           les cartes tirées du sommet de la pioche
     * @return la liste de tous les ensembles de cartes que ce joueur pourrait utiliser pour s'emparer d'un tunnel
     * @throws IllegalArgumentException si le nombre de cartes additionnelles n'est pas compris entre 1 et 3 (inclus),
     *                                  si l'ensemble des cartes initiales est vide ou contient plus de 2 types de cartes différents,
     *                                  ou si l'ensemble des cartes tirées ne contient pas exactement 3 cartes.
     */
    public List<SortedBag<Card>> possibleAdditionalCards(int additionalCardsCount,
                                                         SortedBag<Card> initialCards,
                                                         SortedBag<Card> drawnCards) {
        Preconditions.checkArgument(additionalCardsCount >= 1 &&
                additionalCardsCount <= Constants.ADDITIONAL_TUNNEL_CARDS);
        Preconditions.checkArgument(!initialCards.isEmpty() && initialCards.toSet().size() <= 2);
        Preconditions.checkArgument(drawnCards.size() == Constants.ADDITIONAL_TUNNEL_CARDS);

        //1. Create a set of all possible cards in our hands (minus the initial cards)
        final Card initialCardType = initialCards.stream()
                .distinct()
                .filter(c -> c != Card.LOCOMOTIVE)
                .findAny()
                .orElse(Card.LOCOMOTIVE);

        final SortedBag<Card> possibleCardsInHand = SortedBag.of(cards.stream()
                .filter(c -> c == Card.LOCOMOTIVE || c == initialCardType)
                .collect(Collectors.toList()))
                .difference(initialCards);

        //2. Create all possible subsets and put it in a list
        Set<SortedBag<Card>> optionsSet = new HashSet<>();
        if (possibleCardsInHand.size() >= additionalCardsCount)
            optionsSet = possibleCardsInHand.subsetsOfSize(additionalCardsCount);

        final List<SortedBag<Card>> optionsList = new ArrayList<>(optionsSet);

        //3. Sort the list
        optionsList.sort(Comparator.comparingInt(cs -> cs.countOf(Card.LOCOMOTIVE)));

        return optionsList;
    }

    /**
     * Retourne un état identique à celui-ci,
     * si ce n'est que ce joueur s'est de plus emparé de la route {@code route} au moyen des cartes {@code claimCards}.
     *
     * @param route      la route dont le joueur s'est emparée
     * @param claimCards cartes posées pour s'emparer de la route
     * @return un état identique à celui-ci,
     * si ce n'est que ce joueur s'est de plus emparé de la route {@code route} au moyen des cartes {@code claimCards}
     */
    public PlayerState withClaimedRoute(Route route, SortedBag<Card> claimCards) {
        //TODO: Après rendu intermédiaire, vérifier que le joueur ait bien les cartes en main
        final SortedBag<Card> newCards = this.cards.difference(claimCards);

        final List<Route> newRoutes = new ArrayList<>(routes());
        newRoutes.add(route);

        return new PlayerState(tickets(), newCards, newRoutes);
    }

    /**
     * Retourne le nombre de points (éventuellement négatif) obtenus par ce joueur grâce à ses billets.
     *
     * @return le nombre de points obtenus par ce joueur grâce à ses billets
     */
    public int ticketPoints() {
        final int maxIndex = Math.max(routes().stream()
                        .mapToInt(r -> r.station1().id())
                        .max()
                        .orElse(0),
                routes().stream()
                        .mapToInt(r -> r.station2().id())
                        .max()
                        .orElse(0));

        final StationPartition.Builder connectivityBuilder = new StationPartition.Builder(maxIndex + 1);
        routes().forEach(c -> connectivityBuilder.connect(c.station1(), c.station2()));
        final StationPartition connectivity = connectivityBuilder.build();

        final int points = tickets().stream()
                .mapToInt(t -> t.points(connectivity))
                .sum();

        return points;
    }

    /**
     * Retourne la totalité des points obtenus par ce joueur à la fin de la partie.
     *
     * @return la totalité des points obtenus par ce joueur à la fin de la partie
     */
    public int finalPoints() {
        return claimPoints() + ticketPoints();
    }

    /**
     * Retourne les billets de ce joueur.
     *
     * @return les billets de ce joueur
     */
    public SortedBag<Ticket> tickets() {
        return this.tickets;
    }

    /**
     * Retourne les cartes wagon/locomotive de ce joueur.
     *
     * @return les cartes wagon/locomotive de ce joueur
     */
    public SortedBag<Card> cards() {
        return this.cards;
    }
}

















