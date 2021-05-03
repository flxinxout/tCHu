package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Map;

class InfoViewCreator {

    private final static String PLAYER_STATS_ID = "player-stats";
    private final static String GAME_INFO_ID = "game-info";

    private final static String FILLED_SC = "filled";

    private final static double CIRCLE_RADIUS = 5D;

    private InfoViewCreator() {
    }

    public static Node createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames, ObservableGameState gameState, ObservableList<Text> texts) {

        VBox root = new VBox();
        root.getStylesheets().addAll("info.css", "colors.css");

        VBox playerStats = new VBox();
        playerStats.setId(PLAYER_STATS_ID);

        Separator separator = new Separator(Orientation.HORIZONTAL);

        TextFlow messages = new TextFlow();
        messages.setId(GAME_INFO_ID);

        for(Map.Entry<PlayerId, String> entries : playerNames.entrySet()) {

            PlayerId id = entries.getKey();

            TextFlow playerTextFlow = new TextFlow();
            playerTextFlow.getStyleClass().add(id.name());

            Circle circle = new Circle(CIRCLE_RADIUS);
            circle.getStyleClass().add(FILLED_SC);

            Text text = new Text();

            text.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS,
                    id, gameState.ticketsCountOf(id).getValue(), gameState.cardsCountOf(id).getValue(), gameState.carsCountOf(id).getValue(),
                    gameState.claimPointsOf(id).getValue()));

            playerTextFlow.getChildren().addAll(circle, text);
            playerStats.getChildren().add(playerTextFlow);
        }

        Bindings.bindContent(messages.getChildren(), texts);
        root.getChildren().addAll(playerStats, separator, messages);
        return root;
    }
}


















