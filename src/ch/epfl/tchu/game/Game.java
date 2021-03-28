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
     * @param rng         générateur aléatoire utilisé pour créer l'état initial du jeu et pour mélanger les cartes de la défausse
     * @throws IllegalArgumentException si {@code players} ou {@code playerNames} ont une taille différente de 2
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames,
                            SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        final Collection<Player> playersValues = players.values();
        final Collection<PlayerId> playersId = players.keySet();

        //Initialize players
        players.forEach((id, player) -> player.initPlayers(id, playerNames));

        //Initialize game
        GameState gameState = GameState.initial(tickets, rng);
        sendInformation(new Info(playerNames.get(gameState.currentPlayerId())).willPlayFirst(), playersValues);

        //Display initial tickets choices and ask to choose three them
        for (Player player : playersValues) {
            player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
        }

        sendStateUpdate(gameState, players);
        for (PlayerId id: playersId) {
            gameState = gameState.withInitiallyChosenTickets(id, players.get(id).chooseInitialTickets());
        }

        for (PlayerId id: playersId) {
            sendInformation(new Info(playerNames.get(id))
                    .keptTickets(gameState.playerState(id).ticketCount()), playersValues);
        }

        //Game starts
        //TODO: un peu deg ces 3 variables, le mieux serait de faire une boucle infinie (for(;;))
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
                            List<SortedBag<Card>> options = gameState
                                    .currentPlayerState()
                                    .possibleAdditionalCards(additionalCardsCount, initialCards, drawnCards);

                            SortedBag<Card> additionalCards = SortedBag.of();
                            if (!options.isEmpty()) {
                                additionalCards = currentPlayer.chooseAdditionalCards(options);

                                if (!additionalCards.isEmpty()) {
                                    SortedBag<Card> usedCards = initialCards.union(additionalCards);
                                    gameState = gameState.withClaimedRoute(claimedRoute, usedCards);
                                    sendInformation(currentPlayerInfo.claimedRoute(claimedRoute, usedCards), playersValues);
                                }
                            }

                            if(additionalCards.isEmpty())
                                sendInformation(currentPlayerInfo.didNotClaimRoute(claimedRoute), playersValues);

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

        //Fin du jeu
        Map<PlayerId, Integer> points = new EnumMap<>(PlayerId.class);
        Map<PlayerId, Trail> longestTrails = new EnumMap<>(PlayerId.class);

        //Calcul du chemin le plus long
        for (PlayerId id: playersId)
            longestTrails.put(id, Trail.longest(gameState.playerState(id).routes()));

        //Nous avons choisi d'utiliser des streams afin que ça reste compatible en cas d'ajout de joueurs au jeu (> 2),
        //Bien que cela complique légèrement le code qui aurait été nécessaire dans le cas spécifique à deux joueurs.
        int maxLength = longestTrails.values().stream()
                .mapToInt(Trail::length)
                .max()
                .orElse(0);

        for (Map.Entry<PlayerId, Trail> idTrail : longestTrails.entrySet()){
            PlayerId id = idTrail.getKey();
            Trail tr = idTrail.getValue();

            if (tr.length() == maxLength) {
                sendInformation(new Info(playerNames.get(id)).getsLongestTrailBonus(tr), playersValues);
                points.put(id, gameState.playerState(id).finalPoints() + Constants.LONGEST_TRAIL_BONUS_POINTS);
            }
            else
                points.put(id, gameState.playerState(id).finalPoints());
        }

        sendStateUpdate(gameState, players);

        boolean playersAreEqual = points.values().stream()
                .distinct()
                .count() == 1;

        if (playersAreEqual)
            sendInformation(Info.draw(new ArrayList<>(playerNames.values()), points.get(PlayerId.PLAYER_1)), playersValues);
        else {
            Map.Entry<PlayerId, Integer> winnerEntry = points.entrySet().stream()
                    .max(Comparator.comparingInt(Map.Entry::getValue))
                    .orElseThrow();

            PlayerId winnerId = winnerEntry.getKey();

            sendInformation(new Info(playerNames.get(winnerId))
                    .won(points.get(winnerId), points.get(winnerId.next())), playersValues);
        }
    }

    private static void sendInformation(String info, Collection<Player> players) {
        players.forEach(p -> p.receiveInfo(info));
    }

    private static void sendStateUpdate(GameState newState, Map<PlayerId, Player> players) {
        players.forEach((id, p) -> p.updateState(newState, newState.playerState(id)));
    }
}
