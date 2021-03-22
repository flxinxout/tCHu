package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.gui.Info;

import java.security.cert.CRLReason;
import java.util.Map;
import java.util.Random;

public final class Game {

    private Game() {}

    public static void play(Map<PlayerId, Player> players, Map<PlayerId, String> playerNames, SortedBag<Ticket> tickets,
                            Random rng) {

        Preconditions.checkArgument(players.size() == 2 && playerNames.size() == 2);

        GameState gameState = GameState.initial(tickets, rng);

        players.forEach((key, value) -> {
            value.initPlayers(key, playerNames);
            sendInformation(value, new Info(playerNames.get(key)).willPlayFirst();
            value.setInitialTicketChoice(gameState.topTickets(Constants.INITIAL_TICKETS_COUNT));
            value.chooseInitialTickets();
            sendInformation(value, new Info(playerNames.get(key)).keptTickets(Constants.IN_GAME_TICKETS_COUNT)));
        });

        Player currentPlayer = players.get(gameState.currentPlayerId());

        switch (currentPlayer.nextTurn()) {
            case DRAW_TICKETS:
                gameState.withChosenAdditionalTickets(, currentPlayer.chooseTickets())
                break;

            case DRAW_CARDS:
                int firstCard = currentPlayer.drawSlot();
                int secondCard = currentPlayer.drawSlot();
                break;

            case CLAIM_ROUTE:
                Route claimedRoute = currentPlayer.claimedRoute();
                SortedBag<Card> initialCards = currentPlayer.initialClaimCards();

                if(claimedRoute.level() == Route.Level.UNDERGROUND &&
                    gameState.playerState(gameState.currentPlayerId()).canClaimRoute(claimedRoute) &&
                    ) {
                    if()
                    currentPlayer.chooseAdditionalCards(gameState.playerState(gameState.currentPlayerId())
                            .possibleAdditionalCards(, initialCards, ));
                }

        }






    }

    private static void sendInformation(Player player, String info) {
        player.receiveInfo(info);
    }

}
