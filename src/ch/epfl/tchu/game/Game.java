package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;
import java.util.stream.Collectors;

public final class Game {

    private Game() {
    }

    /**
     * Fait jouer une partie de tCHu aux joueurs donnés, dont les noms figurent dans la table {@code playerNames} ;
     * les billets disponibles pour cette partie sont ceux de {@code tickets},
     * et le générateur aléatoire {@code rng} est utilisé pour créer l'état initial du jeu et pour mélanger les cartes
     * de la défausse pour en faire une nouvelle pioche quand cela est nécessaire.
     *
     * @param players     table associant les joueurs à leur identité
     * @param playerNames table associant le nom des joueurs à leur identité
     * @param tickets     les billets disponibles pour cette partie
     * @param rng         générateur aléatoire utilisé pour créer l'état initial du jeu et pour mélanger les cartes de la défausse
     * @throws IllegalArgumentException si {@code players} ou {@code playerNames} ont une taille différente de 2
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,
                            SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        //Initialize players
        players.forEach((id, player) -> player.initPlayers(id, playerNames));

        Collection<Player> playersValues = players.values();

        //Initialize game
        GameState gameState = GameState.initial(tickets, rng);
        sendInformation(new Info(playerNames.get(gameState.currentPlayerId())).willPlayFirst(), playersValues);

        //Display initial tickets choices and ask to choose three them
        for (Player player : playersValues) {
            player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        //TODO: better than map
        Map<PlayerId, SortedBag<Ticket>> playerTickets = new HashMap<>();
        sendStateUpdate(gameState, players);
        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            playerTickets.put(entry.getKey(), entry.getValue().chooseInitialTickets());
        }

        playerTickets.forEach((id, ticketsBag) -> sendInformation(new Info(playerNames.get(id))
                .keptTickets(ticketsBag.size()), playersValues));

        //Game starts
        boolean isPlaying = true;
        while (isPlaying) {

            Player currentPlayer = players.get(gameState.currentPlayerId());
            Info currentPlayerInfo = new Info(playerNames.get(gameState.currentPlayerId()));

            //Check if it's the last turn
            if (gameState.lastTurnBegins()) {
                sendInformation(currentPlayerInfo
                        .lastTurnBegins(gameState.playerState(gameState.currentPlayerId()).carCount()), playersValues);
                isPlaying = false;
            }

            sendInformation(currentPlayerInfo.canPlay(), playersValues);
            sendStateUpdate(gameState, players);

            //Ask what the player wants to do during its turn
            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    //TODO: check size?
                    SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    sendInformation(currentPlayerInfo.drewTickets(drawnTickets.size()), playersValues);

                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(drawnTickets);
                    gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);
                    sendInformation(currentPlayerInfo.keptTickets(chosenTickets.size()), playersValues);
                    break;

                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        if (i == 1)
                            sendStateUpdate(gameState, players);
                        int slot = currentPlayer.drawSlot();
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
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

                    if (claimedRoute.level() == Route.Level.UNDERGROUND) {
                        SortedBag<Card> additionalCards = SortedBag.of();
                        sendInformation(currentPlayerInfo.attemptsTunnelClaim(claimedRoute, initialCards), playersValues);

                        //Draw cards from top of the deck
                        SortedBag.Builder drawnCardsBuilder = new SortedBag.Builder();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();
                        int additionalCardsCount = claimedRoute.additionalClaimCardsCount(initialCards, drawnCards);
                        sendInformation(currentPlayerInfo.drewAdditionalCards(drawnCards, additionalCardsCount), playersValues);

                        if (additionalCardsCount > 0) {
                            List<SortedBag<Card>> options = gameState
                                    .currentPlayerState()
                                    .possibleAdditionalCards(additionalCardsCount, initialCards, drawnCards);

                            if (!options.isEmpty())
                                additionalCards = currentPlayer.chooseAdditionalCards(options);

                            if (additionalCards.isEmpty())
                                sendInformation(currentPlayerInfo.didNotClaimRoute(claimedRoute), playersValues);
                            else {
                                SortedBag<Card> usedCards = initialCards.union(additionalCards);
                                gameState = gameState.withClaimedRoute(claimedRoute, usedCards);
                                sendInformation(currentPlayerInfo.claimedRoute(claimedRoute, usedCards), playersValues);
                            }
                            //TODO: ce else correct? comme c'est quand même tunnel même si 0 carte additionnelles
                        } else {
                            gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                            sendInformation(currentPlayerInfo.claimedRoute(claimedRoute, initialCards), playersValues);
                        }

                    } else {
                        gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                        sendInformation(currentPlayerInfo.claimedRoute(claimedRoute, initialCards), playersValues);
                    }
            }

            gameState = gameState.forNextTurn();
            sendStateUpdate(gameState, players);
        }

        Trail[] trails = new Trail[PlayerId.COUNT];
        for (int i = 0; i < trails.length; i++) {
            trails[i] = Trail.longest(gameState.playerState(PlayerId.values()[i]).routes());
        }

        int points1 = points(PlayerId.values()[0], trails[0], trails[1], gameState, playerNames, playersValues);
        int points2 = points(PlayerId.values()[1], trails[1], trails[0], gameState, playerNames, playersValues);

        sendStateUpdate(gameState, players);

        if (points1 > points2)
            sendInformation(new Info(playerNames.get(gameState.currentPlayerId())).won(points1, points2), playersValues);
        else if (points2 > points1)
            sendInformation(new Info(playerNames.get(gameState.playerState(gameState.currentPlayerId().next()))).won(points2, points1), playersValues);
        else
            sendInformation(Info.draw(playerNames.values().stream().collect(Collectors.toList()), points1), playersValues);

    }

    private static int points(PlayerId playerId, Trail trail1, Trail trail2, GameState gameState, Map<PlayerId, String> playerNames, Collection<Player> players){
        if (trail1.length() > trail2.length()) {
            sendInformation(new Info(playerNames.get(playerId)).getsLongestTrailBonus(trail1), players);
            return gameState.playerState(playerId).finalPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS;
        }
        else
            return gameState.playerState(playerId).finalPoints();
    }

    private static void sendInformation(String info, Collection<Player> players) {
        players.forEach(p -> p.receiveInfo(info));
    }

    private static void sendStateUpdate(GameState newState, Map<PlayerId, Player> players) {
        players.forEach((id, p) -> p.updateState(newState, newState.playerState(id)));
    }
}
