package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 * Une partie de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Game {
    private final static int CARDS_DRAWN_COUNT = 2;

    private Game() {
    }

    /**
     * Fait jouer une partie de tCHu aux joueurs donnés, dont les noms figurent dans la table {@code playerNames};
     * les billets disponibles pour cette partie sont ceux de {@code tickets},
     * et le générateur aléatoire {@code rng} est utilisé pour créer l'état initial du jeu et pour mélanger les cartes
     * de la défausse pour en faire une nouvelle pioche quand cela est nécessaire.
     *
     * @param players     table associant les joueurs à leur identité
     * @param playerNames table associant le nom des joueurs à leur identité
     * @param tickets     les billets disponibles pour cette partie
     * @param rng         générateur aléatoire utilisé
     * @throws IllegalArgumentException si {@code players} ou {@code playerNames} ont une taille différente de 2
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,
                            SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == PlayerId.COUNT && playerNames.size() == PlayerId.COUNT);

        final Collection<Player> playersValues = players.values();
        final Map<PlayerId, Info> infos = new EnumMap<>(PlayerId.class);
        playerNames.forEach((id, name) -> infos.put(id, new Info(name)));

        //1. Initialisation des joueurs
        players.forEach((id, player) -> player.initPlayers(id, playerNames));

        //Initialisation de la partie
        GameState gameState = GameState.initial(tickets, rng);
        sendInformation(infos.get(gameState.currentPlayerId()).willPlayFirst(), playersValues);

        //Affichage et choix des tickets initiaux
        for (Player player : playersValues) { //Affichage
            player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        sendStateUpdate(gameState, players); //Adapter l'interface graphique

        for (PlayerId id : PlayerId.ALL) //Choix
            gameState = gameState.withInitiallyChosenTickets(id, players.get(id).chooseInitialTickets());

        for (PlayerId id : PlayerId.ALL) //Envoi de l'information
            sendInformation(infos.get(id).keptTickets(gameState.playerState(id).ticketCount()), playersValues);

        //2. Début de la partie
        boolean gameHasEnded = false;
        while(!gameHasEnded) {

            Player currentPlayer = players.get(gameState.currentPlayerId());
            Info currentPlayerInfo = infos.get(gameState.currentPlayerId());

            sendInformation(currentPlayerInfo.canPlay(), playersValues);
            sendStateUpdate(gameState, players);

            //Choix de l'action du joueur
            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    sendInformation(currentPlayerInfo.drewTickets(drawnTickets.size()), playersValues);

                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(drawnTickets);
                    gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);
                    sendInformation(currentPlayerInfo.keptTickets(chosenTickets.size()), playersValues);
                    break;

                case DRAW_CARDS:
                    for (int i = 0; i < CARDS_DRAWN_COUNT; i++) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                        if (i == 1)
                            sendStateUpdate(gameState, players);

                        final int slot = currentPlayer.drawSlot();

                        if (slot != Constants.DECK_SLOT) {
                            gameState = gameState.withDrawnFaceUpCard(slot);
                            sendInformation(currentPlayerInfo
                                    .drewVisibleCard(gameState.cardState().faceUpCard(slot)), playersValues);
                        } else {
                            gameState = gameState.withBlindlyDrawnCard();
                            sendInformation(currentPlayerInfo.drewBlindCard(), playersValues);
                        }
                    }
                    break;

                case CLAIM_ROUTE:
                    Route claimedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> initialCards = currentPlayer.initialClaimCards();

                    if(claimedRoute.level() == Route.Level.OVERGROUND) {
                        sendInformation(currentPlayerInfo.claimedRoute(claimedRoute, initialCards), playersValues);
                        gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                    } else {
                        sendInformation(currentPlayerInfo.attemptsTunnelClaim(claimedRoute, initialCards), playersValues);

                        //Tire des cartes du haut de la pioche
                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();

                        //Calcule le nombre éventuel de cartes additionnelles
                        int additionalCardsCount = claimedRoute.additionalClaimCardsCount(initialCards, drawnCards);
                        sendInformation(currentPlayerInfo
                                .drewAdditionalCards(drawnCards, additionalCardsCount), playersValues);

                        //S'il y a des cartes additionnelles, calcule les cartes que le joueur pourrait jouer
                        SortedBag<Card> additionalCards = SortedBag.of();

                        if (additionalCardsCount > 0) {
                            List<SortedBag<Card>> options = gameState
                                    .currentPlayerState()
                                    .possibleAdditionalCards(additionalCardsCount, initialCards, drawnCards);

                            if (!options.isEmpty())
                                additionalCards = currentPlayer.chooseAdditionalCards(options);
                        }

                        if (additionalCardsCount == 0 || !additionalCards.isEmpty()) {
                            SortedBag<Card> usedCards = initialCards.union(additionalCards);
                            gameState = gameState.withClaimedRoute(claimedRoute, usedCards);
                            sendInformation(currentPlayerInfo.claimedRoute(claimedRoute, usedCards), playersValues);
                        } else
                            sendInformation(currentPlayerInfo.didNotClaimRoute(claimedRoute), playersValues);

                        gameState = gameState.withMoreDiscardedCards(drawnCards);
                    }
                    break;

                default:
                    break;
            }

            if (gameState.lastTurnBegins())
                sendInformation(currentPlayerInfo.lastTurnBegins(gameState.currentPlayerState().carCount()), playersValues);

            gameHasEnded = gameState.lastPlayer() == gameState.currentPlayerId();

            gameState = gameState.forNextTurn();
        }

        //3. Fin de la partie
        GameState finalGameState = gameState;
        sendStateUpdate(finalGameState, players);

        final Map<PlayerId, Integer> points = new EnumMap<>(PlayerId.class);
        final Map<PlayerId, Trail> longestTrails = new EnumMap<>(PlayerId.class);

        //Calcul du chemin le plus long de chacun des joueurs
        for (PlayerId id : PlayerId.ALL)
            longestTrails.put(id, Trail.longest(gameState.playerState(id).routes()));

        //Nous avons choisi d'utiliser des streams afin que ça facilite l'ajout de joueurs au jeu (> 2),
        //Bien que cela complique légèrement le code qui aurait été nécessaire dans le cas spécifique à deux joueurs.
        final int maxLength = longestTrails.values().stream()
                .mapToInt(Trail::length)
                .max()
                .orElse(0);

        longestTrails.forEach((id, tr) -> {
            if (tr.length() == maxLength) {
                sendInformation(infos.get(id).getsLongestTrailBonus(tr), playersValues);
                points.put(id, finalGameState.playerState(id).finalPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS);
            } else
                points.put(id, finalGameState.playerState(id).finalPoints());
        });

        final boolean playersAreEqual = points.values().stream()
                .distinct()
                .count() == 1;

        if (playersAreEqual)
            sendInformation(Info.draw(new ArrayList<>(playerNames.values()), points.get(PlayerId.PLAYER_1)), playersValues);
        else {
            PlayerId winnerId = points.entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .orElseThrow()
                    .getKey();

            sendInformation(infos.get(winnerId).won(points.get(winnerId), points.get(winnerId.next())), playersValues);
        }
    }

    /**
     * Envoie une information à tous les joueurs de la partie.
     */
    private static void sendInformation(String info, Collection<Player> players) {
        players.forEach(p -> p.receiveInfo(info));
    }

    /**
     * Informe tous les joueurs d'un changement d'état.
     */
    private static void sendStateUpdate(GameState newState, Map<PlayerId, Player> players) {
        players.forEach((id, p) -> p.updateState(newState, newState.playerState(id)));
    }
}
