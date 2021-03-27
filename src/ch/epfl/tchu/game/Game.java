package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.util.*;

/**
 *
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
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
        Collection<PlayerId> playersId = players.keySet();

        //Initialize game
        GameState gameState = GameState.initial(tickets, rng);
        sendInformation(new Info(playerNames.get(gameState.currentPlayerId())).willPlayFirst(), playersValues);

        //Display initial tickets choices and ask to choose three them
        for (Player player : playersValues) {
            player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        for (PlayerId id: playersId) {
            gameState = gameState.withInitiallyChosenTickets(id, players.get(id).chooseInitialTickets());
        }

        for (PlayerId id: playersId) {
            sendInformation(new Info(playerNames.get(id))
                    .keptTickets(gameState.playerState(id).ticketCount()), playersValues);
        }

        //Game starts
        //TODO: un peu deg ces 3 boolean, le mieux serait de faire une boucle infinie (for(;;))
        // et de break au bon moment
        boolean isPlaying = true;
        boolean lastTurnBegins = false;
        int lastTurnCountDown = 2;

        while (isPlaying) {

            Player currentPlayer = players.get(gameState.currentPlayerId());
            Info currentPlayerInfo = new Info(playerNames.get(gameState.currentPlayerId()));

            //Vérifie si on entre dans le dernier tour
            if (lastTurnBegins)
                --lastTurnCountDown;

            if(lastTurnCountDown == 1) {
                sendInformation(currentPlayerInfo
                        .lastTurnBegins(gameState.playerState(gameState.currentPlayerId()).carCount()), playersValues);
            }
            else if(lastTurnCountDown == 0)
                isPlaying = false;

            sendInformation(currentPlayerInfo.canPlay(), playersValues);
            sendStateUpdate(gameState, players);

            //Demande au joueur l'action qu'il souhaite effectuer
            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    sendInformation(currentPlayerInfo.drewTickets(drawnTickets.size()), playersValues);

                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(drawnTickets);
                    gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);
                    sendInformation(currentPlayerInfo.keptTickets(chosenTickets.size()), playersValues);
                    break;

                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);

                        if (i == 1)
                            sendStateUpdate(gameState, players);

                        int slot = currentPlayer.drawSlot();
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
                        sendInformation(currentPlayerInfo.attemptsTunnelClaim(claimedRoute, initialCards), playersValues);

                        //Tire des cartes du haut de la pioche
                        SortedBag.Builder<Card> drawnCardsBuilder = new SortedBag.Builder<>();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();

                        //Calcule les éventuelles cartes additionnelles
                        int additionalCardsCount = claimedRoute.additionalClaimCardsCount(initialCards, drawnCards);
                        sendInformation(currentPlayerInfo
                                .drewAdditionalCards(drawnCards, additionalCardsCount), playersValues);

                        if (additionalCardsCount > 0) {
                            SortedBag<Card> additionalCards = SortedBag.of();

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
                        } else {
                            gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                            sendInformation(currentPlayerInfo.claimedRoute(claimedRoute, initialCards), playersValues);
                        }
                    } else {
                        gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                        sendInformation(currentPlayerInfo.claimedRoute(claimedRoute, initialCards), playersValues);
                    }
            }

            lastTurnBegins = gameState.lastTurnBegins();
            gameState = gameState.forNextTurn();
            sendStateUpdate(gameState, players);
        }

        //End of the game
        Map<PlayerId, Trail> longestTrails = new EnumMap<>(PlayerId.class);
        for (PlayerId id: PlayerId.ALL) {
            longestTrails.put(id, Trail.longest(gameState.playerState(id).routes()));
        }

        //TODO: erreur dans le cas d'une égalité psk seulement 1 est retourné
        Map.Entry<PlayerId, Trail> longestTrailEntry = longestTrails.entrySet().stream()
                .max(Comparator.comparingInt(entry -> entry.getValue().length()))
                .orElseThrow();

        PlayerId longestId = longestTrailEntry.getKey();
        sendInformation(new Info(playerNames.get(longestId))
                .getsLongestTrailBonus(longestTrailEntry.getValue()), playersValues);

        Map<PlayerId, Integer> points = new EnumMap<>(PlayerId.class);
        points.put(longestId, gameState.playerState(longestId).finalPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS);
        points.put(longestId.next(), gameState.playerState(longestId.next()).finalPoints());

        sendStateUpdate(gameState, players);

        int player1Points = points.get(PlayerId.PLAYER_1);
        int player2Points = points.get(PlayerId.PLAYER_2);

        if (player1Points > player2Points)
            sendInformation(new Info(playerNames.get(PlayerId.PLAYER_1))
                    .won(player1Points, player2Points), playersValues);
        else if (player2Points > player1Points)
            sendInformation(new Info(playerNames.get(PlayerId.PLAYER_2))
                    .won(player2Points, player1Points), playersValues);
        else
            sendInformation(Info.draw(new ArrayList<>(playerNames.values()), player1Points), playersValues);

    }

    private static void sendInformation(String info, Collection<Player> players) {
        players.forEach(p -> p.receiveInfo(info));
    }

    private static void sendStateUpdate(GameState newState, Map<PlayerId, Player> players) {
        players.forEach((id, p) -> p.updateState(newState, newState.playerState(id)));
    }
}
