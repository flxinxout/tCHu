package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;
import java.util.stream.Collectors;

import static ch.epfl.tchu.game.ChMap.routes;
import static ch.epfl.tchu.game.Constants.*;

/**
 * L'état observable d'une partie de tCHu. Il inclut la partie publique de l'état du jeu ({@code PublicGameState})
 * ainsi que la totalité de l'état d'un joueur donné ({@code PlayerState})
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 * @see PublicGameState
 * @see PlayerState
 */
public final class ObservableGameState {

    private final PlayerId id;
    private final Collection<PlayerId> playerIds;

    // Groupe 1: propriétés de l'état public de la partie
    private final IntegerProperty ticketsPercentage, cardsPercentage;
    private final List<ObjectProperty<Card>> faceUpCards;
    private final Map<Route, ObjectProperty<PlayerId>> routesOwner;

    // Groupe 2: propriétés de l'état public de chacun des joueurs
    private final Map<PlayerId, IntegerProperty> ticketCount, cardCount, carCount, claimPoints;

    // Groupe 3: propriétés de l'état privé du joueur auquel cette instance correspond
    private final ObservableList<Ticket> tickets;
    private final Map<Card, IntegerProperty> cardOccurrences;
    private final Map<Route, BooleanProperty> routesClaimable;

    private PublicGameState gameState;
    private PlayerState playerState;

    /**
     * Construit un état de jeu observable correspondant au joueur d'identité donnée. À la création, la totalité des
     * propriétés de cet état ont leur valeur par défaut.
     *
     * @param playerIds la collection des identités de joueur utilisées durant la partie
     * @param id        l'identité du joueur attaché à cet état de jeu observable
     */
    public ObservableGameState(Collection<PlayerId> playerIds, PlayerId id) {
        this.id = id;
        this.playerIds = playerIds;

        // 1.
        this.ticketsPercentage = new SimpleIntegerProperty();
        this.cardsPercentage = new SimpleIntegerProperty();
        this.faceUpCards = createFaceUpCards();
        this.routesOwner = createRoutesOwner();

        // 2.
        this.ticketCount = createPlayerIdMap(playerIds);
        this.cardCount = createPlayerIdMap(playerIds);
        this.carCount = createPlayerIdMap(playerIds);
        this.claimPoints = createPlayerIdMap(playerIds);

        // 3.
        this.tickets = FXCollections.observableArrayList();
        this.cardOccurrences = createCardOccurrences();
        this.routesClaimable = createRoutesClaimable();
    }

    /**
     * Crée la liste des 5 propriétés des cartes face visible. Ces cartes sont initialisées à {@code null}.
     *
     * @return la liste des 5 propriétés des cartes face visible
     */
    private static List<ObjectProperty<Card>> createFaceUpCards() {
        List<ObjectProperty<Card>> faceUpCards = new ArrayList<>(FACE_UP_CARDS_COUNT);
        for (int i = 0; i < FACE_UP_CARDS_COUNT; i++)
            faceUpCards.add(new SimpleObjectProperty<>());
        return faceUpCards;
    }

    /**
     * Crée la table associative entre les routes du jeu et leur propriétaire, initialisé à {@code null}.
     *
     * @return la table associative entre les routes du jeu et leur propriétaire
     */
    private static Map<Route, ObjectProperty<PlayerId>> createRoutesOwner() {
        Map<Route, ObjectProperty<PlayerId>> routesMap = new HashMap<>();
        for (Route route : routes())
            routesMap.put(route, new SimpleObjectProperty<>());
        return routesMap;
    }

    /**
     * Crée la table associative entre les routes du jeu et leur disponibilité, initialisée à {@code false}.
     *
     * @return la table associative entre les routes du jeu et leur disponibilité
     */
    private static Map<Route, BooleanProperty> createRoutesClaimable() {
        Map<Route, BooleanProperty> routesMap = new HashMap<>();
        for (Route route : routes())
            routesMap.put(route, new SimpleBooleanProperty());
        return routesMap;
    }

    /**
     * Crée la table associative entre les joueurs du jeu et leurs différents comptes publics
     * (compte des cartes, billets, wagons et points de construction).
     *
     * @return la table associative entre les joueurs du jeu et leurs différents comptes publics
     */
    private static Map<PlayerId, IntegerProperty> createPlayerIdMap(Collection<PlayerId> ids) {
        Map<PlayerId, IntegerProperty> map = new EnumMap<>(PlayerId.class);
        for (PlayerId id : ids)
            map.put(id, new SimpleIntegerProperty());
        return map;
    }

    /**
     * Crée la table associative entre les cartes du jeu et leur nombre d'occurrences dans la main de ce joueur.
     *
     * @return la table associative entre les cartes du jeu et leur nombre d'occurrences dans la main de ce joueur
     */
    private static Map<Card, IntegerProperty> createCardOccurrences() {
        Map<Card, IntegerProperty> cardOccurrences = new EnumMap<>(Card.class);
        for (Card card : Card.ALL)
            cardOccurrences.put(card, new SimpleIntegerProperty());
        return cardOccurrences;
    }

    /**
     * Met à jour la totalité des propriétés de cet état en fonction des deux états donnés.
     *
     * @param newGameState le nouvel état du jeu
     * @param playerState  le nouvel état du joueur associé à cet état de jeu
     */
    public void setState(PublicGameState newGameState, PlayerState playerState) {
        gameState = newGameState;
        this.playerState = playerState;

        // 1.
        ticketsPercentage.setValue(100 * newGameState.ticketsCount() / ChMap.tickets().size());
        cardsPercentage.setValue(100 * newGameState.cardState().deckSize() / computeTotalCardsCount(newGameState.playerCount()));

        for (int slot : FACE_UP_CARD_SLOTS)
            faceUpCards.get(slot).setValue(newGameState.cardState().faceUpCard(slot));

        for (Route claimedRoute : newGameState.claimedRoutes()) {
            ObjectProperty<PlayerId> ownerP = routesOwner.get(claimedRoute);
            if (ownerP.getValue() == null) {
                for (PlayerId pId : playerIds) {
                    if (newGameState.playerState(pId).routes().contains(claimedRoute))
                        ownerP.setValue(pId);
                }
            }
        }

        // 2.
        for (PlayerId pId : playerIds) {
            ticketCount.get(pId).setValue(newGameState.playerState(pId).ticketCount());
            cardCount.get(pId).setValue(newGameState.playerState(pId).cardCount());
            carCount.get(pId).setValue(newGameState.playerState(pId).carCount());
            claimPoints.get(pId).setValue(newGameState.playerState(pId).claimPoints());
        }

        // 3.
        tickets.setAll(playerState.tickets().toList());

        for (Card card : Card.ALL)
            cardOccurrences.get(card).setValue(playerState.cards().countOf(card));

        for (Route route : routes()) {
            Set<List<Station>> stations;
            stations = playerIds.size() == MINIMUM_NUMBER_PLAYERS ?
                    newGameState.claimedRoutes().stream()
                            .map(Route::stations)
                            .collect(Collectors.toSet()) :
                    newGameState.playerState(id).routes().stream()
                            .map(Route::stations)
                            .collect(Collectors.toSet());
            routesClaimable.get(route).setValue(newGameState.currentPlayerId() == id &&
                    !newGameState.claimedRoutes().contains(route) &&
                    !stations.contains(route.stations()) &&
                    playerState.canClaimRoute(route));
        }
    }

    /**
     * Retourne la propriété du pourcentage de billets restant dans la pioche.
     *
     * @return la propriété du pourcentage de billets restant dans la pioche
     */
    public ReadOnlyIntegerProperty ticketsPercentage() {
        return ticketsPercentage;
    }

    /**
     * Retourne la propriété du pourcentage de cartes restant dans la pioche.
     *
     * @return la propriété du pourcentage de cartes restant dans la pioche
     */
    public ReadOnlyIntegerProperty cardsPercentage() {
        return cardsPercentage;
    }

    /**
     * Retourne la propriété de la carte face visible à l'emplacement donné.
     *
     * @param slot l'emplacement de la carte face visible
     * @return la propriété de la carte face visible à l'emplacement donné
     */
    public ReadOnlyObjectProperty<Card> faceUpCardAt(int slot) {
        return faceUpCards.get(slot);
    }

    /**
     * Retourne la propriété du propriétaire de la route donnée.
     *
     * @param route la route
     * @return la propriété du propriétaire de la route donnée
     */
    public ReadOnlyObjectProperty<PlayerId> ownerOf(Route route) {
        return routesOwner.get(route);
    }

    /**
     * Retourne la propriété du nombre de billets du joueur d'identité donnée.
     *
     * @param id l'identité du joueur
     * @return la propriété du  nombre de billets du joueur d'identité donnée
     */
    public ReadOnlyIntegerProperty ticketsCountOf(PlayerId id) {
        return ticketCount.get(id);
    }

    /**
     * Retourne la propriété du nombre de cartes du joueur d'identité donnée.
     *
     * @param id l'identité du joueur
     * @return la propriété du  nombre de cartes du joueur d'identité donnée
     */
    public ReadOnlyIntegerProperty cardsCountOf(PlayerId id) {
        return cardCount.get(id);
    }

    /**
     * Retourne la propriété du nombre de wagons du joueur d'identité donnée.
     *
     * @param id l'identité du joueur
     * @return la propriété du  nombre de wagons du joueur d'identité donnée
     */
    public ReadOnlyIntegerProperty carsCountOf(PlayerId id) {
        return carCount.get(id);
    }

    /**
     * Retourne la propriété du nombre de points de construction du joueur d'identité donnée.
     *
     * @param id l'identité du joueur
     * @return la propriété du  nombre de points de construction du joueur d'identité donnée
     */
    public ReadOnlyIntegerProperty claimPointsOf(PlayerId id) {
        return claimPoints.get(id);
    }

    /**
     * Retourne les billets du joueur de cet état de jeu, sous forme de liste observable.
     *
     * @return les billets du joueur de cet état de jeu
     */
    public ObservableList<Ticket> tickets() {
        return FXCollections.unmodifiableObservableList(tickets);
    }

    /**
     * Retourne la propriété du nombre d'occurrences de la carte donnée dans la main du joueur de cet état de jeu.
     *
     * @param card la carte
     * @return la propriété du nombre d'occurrences de la carte donnée dans la main du joueur de cet état de jeu
     */
    public ReadOnlyIntegerProperty occurrencesOf(Card card) {
        return cardOccurrences.get(card);
    }

    /**
     * Retourne la propriété de la possibilité du joueur de cet état de jeu de s'emparer de la route donnée.
     *
     * @param route la route
     * @return la propriété de la possibilité du joueur de cet état de jeu de s'emparer de la route donnée
     */
    public ReadOnlyBooleanProperty claimable(Route route) {
        return routesClaimable.get(route);
    }

    /**
     * Retourne si et seulement s'il est possible de tirer des billets de la pioche.
     *
     * @return si et seulement s'il est possible de tirer des billets de la pioche
     */
    public boolean canDrawTickets() {
        return gameState != null && gameState.canDrawTickets();
    }

    /**
     * Retourne si et seulement s'il est possible de tirer une carte de la pioche.
     *
     * @return si et seulement s'il est possible de tirer une carte de la pioche
     */
    public boolean canDrawCards() {
        return gameState != null && gameState.canDrawCards();
    }

    /**
     * Retourne la liste de tous les ensembles de cartes que le joueur de cet état de jeu pourrait utiliser pour prendre
     * possession de la route donnée.
     *
     * @return la liste de tous les ensembles de cartes que le joueur de cet état de jeu pourrait utiliser pour prendre
     * possession de la route donnée
     */
    public List<SortedBag<Card>> possibleClaimCards(Route route) {
        return playerState == null ? List.of() : playerState.possibleClaimCards(route);
    }
}
