package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.PlayerId;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.gui.MapViewCreator.FILLED_SC;
import static javafx.animation.Animation.INDEFINITE;

/**
 * Créateur de la vue des informations d'une partie de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
final class InfoViewCreator {

    private final static String GAME_INFO_ID = "game-info";
    private final static String PLAYER_STATS_ID = "player-stats";

    private final static String CURRENT_SC = "current";

    private final static double BLINK_DURATION = 1500D;
    private final static double BLINK_DELAY = 750D;

    private final static double CIRCLE_RADIUS = 5D;

    private InfoViewCreator() {
    }

    /**
     * Crée la vue des informations du jeu à l'aide de l'état du jeu observable, les
     * noms des joueurs ainsi que la liste observable des messages à afficher donnés.
     *
     * @param playerId    l'identité du joueur auquel cette vue est attachée
     * @param playerNames la table associative entre les joueurs et leur nom
     * @param gameState   l'état de jeu observable actuel
     * @param messages    les différents messages à observer
     * @return la vue des informations du jeu
     */
    public static Node createInfoView(PlayerId playerId,
                                      Map<PlayerId, String> playerNames,
                                      ObservableGameState gameState,
                                      ObservableList<Text> messages) {
        VBox root = new VBox();
        root.getStylesheets().addAll("info.css", "colors.css");

        VBox playerStats = new VBox();
        playerStats.setId(PLAYER_STATS_ID);

        List<PlayerId> ids = new ArrayList<>(playerNames.keySet());
        Collections.rotate(ids, -playerId.ordinal());
        for (PlayerId id : ids) {
            TextFlow playerTextFlow = new TextFlow();
            playerTextFlow.getStyleClass().add(id.name());

            Circle circle = new Circle(CIRCLE_RADIUS);
            circle.getStyleClass().add(FILLED_SC);

            FadeTransition fade = new FadeTransition();
            fade.setDuration(new Duration(BLINK_DURATION));
            fade.setAutoReverse(true);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setNode(circle);
            fade.setCycleCount(2);
            SequentialTransition blink = new SequentialTransition (fade, new PauseTransition(Duration.millis(BLINK_DELAY)));
            blink.setCycleCount(INDEFINITE);

            gameState.currentPlayer().addListener((o, oV, nV) -> {
                if (oV == id) {
                    blink.stop();
                    circle.setOpacity(1);
                    circle.getStyleClass().remove(CURRENT_SC);
                }
                if (nV == id) {
                    blink.playFromStart();
                    circle.getStyleClass().add(CURRENT_SC);
                }
            });

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