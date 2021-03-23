package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.lang.management.PlatformLoggingMXBean;
import java.security.cert.CRLReason;
import java.util.*;

public final class Game {

    private Game() {
    }

    /**
     * Fait jouer une partie de tCHu aux joueurs donnés, dont les noms figurent dans la table playerNames.
     *
     * @param players     table associant les joueurs à leur identité
     * @param playerNames table associant le nom des joueurs à leur identité
     * @param tickets     les billets disponibles pour cette partie
     * @param rng         générateur aléatoire utilisé pour créer l'état initial du jeu et pour mélanger les cartes de la défausse
     * @throws IllegalArgumentException si l'une des deux tables associatives a une taille différente de 2.
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        //1. Initialization
        GameState gameState = GameState.initial(tickets, rng);

        for (Map.Entry<PlayerId, Player> entry : players.entrySet()) {
            PlayerId id = entry.getKey();
            Player player = entry.getValue();
            player.initPlayers(id, playerNames);
            player.receiveInfo(new Info(playerNames.get(id)).willPlayFirst());
            player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            SortedBag<Ticket> chosenTickets = player.chooseInitialTickets();

            //TODO écrit que c'est uniquement lorsqe tout les joueurs ont faire leur choix ! donc faut sortir de la boucle
            player.receiveInfo(new Info(playerNames.get(id)).keptTickets(chosenTickets.size()));
        }

        boolean isPlaying = true;

        //2. In game
        while (isPlaying){

            Player currentPlayer = players.get(gameState.currentPlayerId());
            String nameOfPlayer = playerNames.get(gameState.currentPlayerId());
            Collection<Player> values = players.values();
            sendInformation(new Info(nameOfPlayer).canPlay(), values);

            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    sendInformation(new Info(nameOfPlayer).drewTickets(drawnTickets.size()), values);

                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(drawnTickets);
                    gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);
                    sendInformation(new Info(nameOfPlayer).keptTickets(chosenTickets.size()), values);
                    break;

                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        int slot = currentPlayer.drawSlot();
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        if(slot != Constants.DECK_SLOT) {
                            gameState = gameState.withDrawnFaceUpCard(slot);
                            sendInformation(new Info(nameOfPlayer).drewVisibleCard(gameState.cardState().faceUpCard(slot)),
                                    values);
                        } else {
                            gameState = gameState.withBlindlyDrawnCard();
                            sendInformation(new Info(nameOfPlayer).drewBlindCard(), values);
                        }
                    }
                    break;

                case CLAIM_ROUTE:
                    Route claimedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> initialCards = currentPlayer.initialClaimCards();

                    //TODO ENCORE METTRE LE FAIL DE CLAIM JSAIS PAS OU MDR

                    if (claimedRoute.level() == Route.Level.UNDERGROUND) {
                        SortedBag<Card> additionalCards = SortedBag.of();
                        sendInformation(new Info(nameOfPlayer).attemptsTunnelClaim(claimedRoute, initialCards), values);

                        //Draw cards from top of the deck
                        SortedBag.Builder drawnCardsBuilder = new SortedBag.Builder();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();
                        int additionalCardsCount = claimedRoute.additionalClaimCardsCount(initialCards, drawnCards);
                        sendInformation(new Info(nameOfPlayer).drewAdditionalCards(drawnCards, additionalCardsCount), values);

                        if (additionalCardsCount > 0) {
                            List<SortedBag<Card>> options = gameState
                                    .currentPlayerState()
                                    .possibleAdditionalCards(additionalCardsCount, initialCards, drawnCards);

                            if (!options.isEmpty())
                                additionalCards = currentPlayer.chooseAdditionalCards(options);
                        }

                        if (!additionalCards.isEmpty())
                            gameState = gameState.withClaimedRoute(claimedRoute, initialCards.union(additionalCards));
                    } else {
                        gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                        sendInformation(new Info(nameOfPlayer).claimedRoute(claimedRoute, initialCards), values);
                    }
            }

            if (gameState.lastPlayer() != null)
                sendInformation(new Info(nameOfPlayer)
                        .lastTurnBegins(gameState.playerState(gameState.currentPlayerId()).carCount()), values);
                isPlaying = false;

            gameState = gameState.forNextTurn();
        }

        //TODO LAST 2 INFORMATIONS
    }

    private static void sendInformation(String info, Collection<Player> players) {
        players.forEach(p -> p.receiveInfo(info));
    }

    private static void sendStateUpdate() {

    }

}
