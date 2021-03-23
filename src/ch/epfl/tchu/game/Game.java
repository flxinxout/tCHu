package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.security.cert.CRLReason;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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
            player.receiveInfo(new Info(playerNames.get(id)).keptTickets(chosenTickets.size()));
        }

        boolean isPlaying = true;

        //2. In game
        while (isPlaying){

            Player currentPlayer = players.get(gameState.currentPlayerId());

            switch (currentPlayer.nextTurn()) {
                case DRAW_TICKETS:
                    SortedBag<Ticket> drawnTickets = gameState.topTickets(Constants.IN_GAME_TICKETS_COUNT);
                    SortedBag<Ticket> chosenTickets = currentPlayer.chooseTickets(drawnTickets);
                    gameState = gameState.withChosenAdditionalTickets(drawnTickets, chosenTickets);
                    break;

                case DRAW_CARDS:
                    for (int i = 0; i < 2; i++) {
                        int slot = currentPlayer.drawSlot();
                        gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                        gameState = slot != Constants.DECK_SLOT ?
                                gameState.withDrawnFaceUpCard(slot) : gameState.withBlindlyDrawnCard();
                    }
                    break;

                case CLAIM_ROUTE:
                    Route claimedRoute = currentPlayer.claimedRoute();
                    SortedBag<Card> initialCards = currentPlayer.initialClaimCards();

                    if (claimedRoute.level() == Route.Level.UNDERGROUND) {
                        SortedBag<Card> additionalCards = SortedBag.of();

                        //Draw cards from top of the deck
                        SortedBag.Builder drawnCardsBuilder = new SortedBag.Builder();
                        for (int i = 0; i < Constants.ADDITIONAL_TUNNEL_CARDS; i++) {
                            gameState = gameState.withCardsDeckRecreatedIfNeeded(rng);
                            drawnCardsBuilder.add(gameState.topCard());
                            gameState = gameState.withoutTopCard();
                        }
                        SortedBag<Card> drawnCards = drawnCardsBuilder.build();

                        int additionalCardsCount = claimedRoute.additionalClaimCardsCount(initialCards, drawnCards);
                        if (additionalCardsCount > 0) {
                            List<SortedBag<Card>> options = gameState
                                    .currentPlayerState()
                                    .possibleAdditionalCards(additionalCardsCount, initialCards, drawnCards);

                            if (!options.isEmpty())
                                additionalCards = currentPlayer.chooseAdditionalCards(options);
                        }

                        if (!additionalCards.isEmpty())
                            gameState = gameState.withClaimedRoute(claimedRoute, initialCards.union(additionalCards));
                    } else
                        gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
            }

            if (gameState.lastPlayer() != null)
                isPlaying = false;

            gameState = gameState.forNextTurn();
        }
    }

    private static void sendInformation(Player player, String info) {
        player.receiveInfo(info);
    }

    private static void sendStateUpdate() {

    }

}
