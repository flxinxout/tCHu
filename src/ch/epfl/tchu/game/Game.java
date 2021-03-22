package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.security.cert.CRLReason;
import java.util.Map;
import java.util.Random;

public final class Game {
    private Game() {}

    /**
     * Fait jouer une partie de tCHu aux joueurs donnés, dont les noms figurent dans la table playerNames.
     * @param players
     *          table associant les joueurs à leur identité
     * @param playerNames
     *          table associant le nom des joueurs à leur identité
     * @param tickets
     *          les billets disponibles pour cette partie
     * @param rng
     *          générateur aléatoire utilisé pour créer l'état initial du jeu et pour mélanger les cartes de la défausse
     * @throws IllegalArgumentException
     *          si l'une des deux tables associatives a une taille différente de 2.
     */
    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets, Random rng) {
        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        //1. Initialization
        GameState gameState = GameState.initial(tickets, rng);

        players.forEach((id, player) -> {
            player.initPlayers(id, playerNames);
            player.receiveInfo(new Info(playerNames.get(id)).willPlayFirst());
            player.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            gameState = gameState.withoutTopTickets(Constants.INITIAL_TICKETS_COUNT);
            SortedBag<Ticket> chosenTickets = player.chooseInitialTickets();
            player.receiveInfo(new Info(playerNames.get(id)).keptTickets(chosenTickets.size()));
        });

        //2. In game
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
                    gameState = slot >= 0 && slot <= 4 ?
                            gameState.withDrawnFaceUpCard(slot) : gameState.withBlindlyDrawnCard();
                }
                break;

            case CLAIM_ROUTE:
                Route claimedRoute = currentPlayer.claimedRoute();
                SortedBag<Card> initialCards = currentPlayer.initialClaimCards();

                gameState = gameState.withClaimedRoute(claimedRoute, initialCards);
                //if(claimedRoute.level() == Route.Level.UNDERGROUND && claimedRoute.additionalClaimCardsCount())
        }
    }

    private static void sendInformation(Player player, String info) {
        player.receiveInfo(info);
    }

    private static void stateChange(){

    }

}
