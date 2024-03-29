package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

import static ch.epfl.tchu.game.Constants.*;
import static java.lang.Math.max;

/**
 * Une partie de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class Game {
    private final static int CARDS_DRAWN_COUNT = 2;
    private final static int MAX_MESSAGE_COUNT = 6;

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
     * @throws IllegalArgumentException si {@code players} ou {@code playerNames} ont une taille plus grande que 4 ou plus petite que 2
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,
                            SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() <= PlayerId.COUNT && players.size() >= MINIMUM_PLAYER_COUNT);
        Preconditions.checkArgument(playerNames.size() <= PlayerId.COUNT && playerNames.size() >= MINIMUM_PLAYER_COUNT);

        Collection<Player> playersValues = players.values();
        Set<PlayerId> playerIds = players.keySet();

        Map<PlayerId, Info> infos = new EnumMap<>(PlayerId.class);
        playerNames.forEach((id, name) -> infos.put(id, new Info(name)));

        // 1. Initialisation de la partie
        players.forEach((id, player) -> player.initPlayers(id, playerNames));

        GameState gameState = GameState.initial(playerIds, tickets, rng);
        sendInformation(infos.get(gameState.currentPlayerId()).willPlayFirst(), playersValues);

        for (Player player : playersValues) {
            player.setInitialTicketChoice(gameState.topTickets(INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(INITIAL_TICKETS_COUNT);
        }
        sendStateUpdate(gameState, players);

        for (PlayerId id : playerIds)
            gameState = gameState.withInitiallyChosenTickets(id, players.get(id).chooseInitialTickets());

        for (PlayerId id : playerIds)
            sendInformation(infos.get(id).keptTickets(gameState.playerState(id).ticketCount()), playersValues);

        //2. Début de la partie
        boolean gameHasEnded = false;
        while (!gameHasEnded) {

            Player currentPlayer = players.get(gameState.currentPlayerId());
            Info currentPlayerInfo = infos.get(gameState.currentPlayerId());

            sendInformation(currentPlayerInfo.canPlay(), playersValues);
            sendStateUpdate(gameState, players);

            // Choix de l'action du joueur
            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    SortedBag<Ticket> drawnTickets = gameState.topTickets(IN_GAME_TICKETS_COUNT);
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

                        int slot = currentPlayer.drawSlot();

                        if (slot != DECK_SLOT) {
                            sendInformation(currentPlayerInfo
                                    .drewVisibleCard(gameState.cardState().faceUpCard(slot)), playersValues);
                            gameState = gameState.withDrawnFaceUpCard(slot);
                        } else {
                            sendInformation(currentPlayerInfo.drewBlindCard(), playersValues);
                            gameState = gameState.withBlindlyDrawnCard();
                        }
                    }
                    break;

                case CLAIM_ROUTE:
                    Route claimedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> initialCards = currentPlayer.initialClaimCards();

                    if (claimedRoute.level() == Route.Level.OVERGROUND) {
                        sendInformation(currentPlayerInfo.claimedRoute(claimedRoute, initialCards), playersValues);
                        gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                    } else {
                        sendInformation(currentPlayerInfo.attemptsTunnelClaim(claimedRoute, initialCards), playersValues);

                        // Tire des cartes du haut de la pioche
                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                        for (int i = 0; i < ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();

                        // Calcule le nombre éventuel de cartes additionnelles
                        int additionalCardsCount = claimedRoute.additionalClaimCardsCount(initialCards, drawnCards);
                        sendInformation(currentPlayerInfo
                                .drewAdditionalCards(drawnCards, additionalCardsCount), playersValues);

                        // S'il y a des cartes additionnelles, calcule les cartes que le joueur pourrait jouer
                        SortedBag<Card> additionalCards = SortedBag.of();

                        if (additionalCardsCount > 0) {
                            List<SortedBag<Card>> options = gameState
                                    .currentPlayerState()
                                    .possibleAdditionalCards(additionalCardsCount, initialCards);

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

            gameState = gameState.forNextTurn(playerIds);
        }

        // 3. Fin de la partie
        sendStateUpdate(gameState, players);

        Map<Integer, String> points = new HashMap<>();
        Map<String, Trail> longestTrails = new HashMap<>();

        // Calcul du chemin le plus long de chacun des joueurs
        int maxLength = 0;
        for (PlayerId id : playerIds) {
            Trail longest = Trail.longest(gameState.playerState(id).routes());
            maxLength = max(maxLength, longest.length());
            longestTrails.put(playerNames.get(id), longest);
        }

        for (int i = 0; i < MAX_MESSAGE_COUNT; i++)
            sendInformation("", playersValues);


        for (PlayerId id : playerIds) {
            String name = playerNames.get(id);
            Trail tr = longestTrails.get(name);
            if (tr.length() == maxLength) {
                sendInformation(infos.get(id).getsLongestTrailBonus(tr), playersValues);
                points.put(gameState.playerState(id).finalPoints() + LONGEST_TRAIL_BONUS_POINTS, name);
            } else
                points.put(gameState.playerState(id).finalPoints(), name);
        }

        sendInformation(Info.classement(points), playersValues);
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
