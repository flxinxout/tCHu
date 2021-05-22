package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.ChMap.routes;
import static ch.epfl.tchu.game.ChMap.tickets;
import static ch.epfl.tchu.game.PlayerId.PLAYER_1;
import static ch.epfl.tchu.game.PlayerId.PLAYER_2;
import static ch.epfl.tchu.gui.ActionHandlers.*;

public final class GraphicalPlayerTest extends Application {
    private void setState(GraphicalPlayer player) {
        PlayerState p1State = new PlayerState(2,SortedBag.of(tickets().subList(0, 4)),
                SortedBag.of(List.of(Card.WHITE, Card.RED, Card.RED, Card.LOCOMOTIVE, Card.BLUE, Card.BLACK)),
                routes().subList(0, 3));

        PublicPlayerState p2State = new PublicPlayerState(2,0, 0, routes().subList(3, 6));

        Map<PlayerId, PublicPlayerState> pubPlayerStates = Map.of(PLAYER_1, p1State, PLAYER_2, p2State);
        PublicCardState cardState = new PublicCardState(Card.ALL.subList(4, 9), 110 - 2 * 4 - 5, 0);
        PublicGameState publicGameState = new PublicGameState(36, cardState, PLAYER_1, pubPlayerStates, null);

        player.setState(publicGameState, p1State);
    }

    @Override
    public void start(Stage primaryStage) {
        Map<PlayerId, String> playerNames =
                Map.of(PLAYER_1, "Ada", PLAYER_2, "Charles");
        GraphicalPlayer p = new GraphicalPlayer(PLAYER_1, playerNames);
        setState(p);

        DrawTicketsHandler drawTicketsH =
                () -> p.receiveInfo("Je tire des billets !");
        DrawCardHandler drawCardH =
                s -> p.receiveInfo(String.format("Je tire une carte de %s !", s));
        ClaimRouteHandler claimRouteH =
                (r, cs) -> {
                    String rn = r.station1() + " - " + r.station2();
                    p.receiveInfo(String.format("Je m'empare de %s avec %s", rn, cs));
                };

        p.startTurn(drawTicketsH, drawCardH, claimRouteH);
    }
}
