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

import java.util.List;
import java.util.Map;

/**
 * Permet de créer la vue des informations d'une partie de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
class InfoViewCreator {

    private final static String GAME_INFO_ID = "game-info";
    private final static String PLAYER_STATS_ID = "player-stats";

    private final static String FILLED_SC = "filled";

    private final static double CIRCLE_RADIUS = 5D;

    private InfoViewCreator() {
    }

    /**
     * Permet de créer la vue des informations correspondant au joueur donné, à l'aide de l'état du jeu observable, les
     * noms des joueurs ainsi que la liste observable des messages à afficher donnés.
     *
     * @param playerId    identité du joueur auquel cette vue est attachée
     * @param playerNames carte associative entre les joueurs et leur nom
     * @param gameState   l'état de jeu observable
     * @param messages    la liste observable des différents messages à observer
     * @return la vue des informations de l'état de jeu
     */
    public static Node createInfoView(PlayerId playerId, Map<PlayerId, String> playerNames,
                                      ObservableGameState gameState, ObservableList<Text> messages) {
        VBox root = new VBox();
        root.getStylesheets().addAll("info.css", "colors.css");

        VBox playerStats = new VBox();
        playerStats.setId(PLAYER_STATS_ID);
        for (PlayerId id : List.of(playerId, playerId.next())) {
            TextFlow playerTextFlow = new TextFlow();
            playerTextFlow.getStyleClass().add(id.name());

            Circle circle = new Circle(CIRCLE_RADIUS);
            circle.getStyleClass().add(FILLED_SC);

            Text text = new Text();
            text.textProperty().bind(Bindings.format(StringsFr.PLAYER_STATS,
                    playerNames.get(id),
                    gameState.ticketsCountOf(id),
                    gameState.cardsCountOf(id),
                    gameState.carsCountOf(id),
                    gameState.claimPointsOf(id)));

            playerTextFlow.getChildren().addAll(circle, text);
            playerStats.getChildren().add(playerTextFlow);
        }

        Separator separator = new Separator(Orientation.HORIZONTAL);

        TextFlow messagesFlow = new TextFlow();
        messagesFlow.setId(GAME_INFO_ID);
        Bindings.bindContent(messagesFlow.getChildren(), messages);

        root.getChildren().addAll(playerStats, separator, messagesFlow);
        return root;
    }
}


















