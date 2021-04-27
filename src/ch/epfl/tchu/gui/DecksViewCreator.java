package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.List;

import static ch.epfl.tchu.game.Card.ALL;
import static ch.epfl.tchu.game.Card.LOCOMOTIVE;
import static ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import static ch.epfl.tchu.gui.ActionHandlers.DrawTicketHandler;

class DecksViewCreator {
    private final static String TICKETS_ID = "tickets";
    private final static String HAND_PANE_ID = "hand-pane";
    private final static String CARD_PANE_ID = "card-pane";

    private final static String NEUTRAL_SC = "NEUTRAL";
    private final static String CARD_SC = "card";
    private final static String OUTSIDE_SC = "outside";
    private final static String INSIDE_SC = "inside";
    private final static String FILLED_SC = "filled";
    private final static String TRAIN_IMAGE_SC = "train-image";
    private final static String COUNT_SC = "count";
    private final static String GAUGED_SC = "gauged";
    private final static String BACKGROUND_SC = "background";
    private final static String FOREGROUND_SC = "foreground";

    private final static double OUTSIDE_WIDTH = 60d;
    private final static double OUTSIDE_HEIGHT = 90d;
    private final static double INSIDE_WIDTH = 40d;
    private final static double INSIDE_HEIGHT = 70d;
    private final static double GAUGE_WIDTH = 50d;
    private final static double GAUGE_HEIGHT = 5d;

    private DecksViewCreator() {
    }

    public static void createHandView(ObservableGameState gameState) {
        HBox rootHBox = MapViewUtils.hBoxWithoutId("decks.css", "colors.css");
        ListView<Rectangle> tickets = MapViewUtils.listView(TICKETS_ID);
        HBox handPaneHBox = MapViewUtils.hBox(HAND_PANE_ID);

        for (Card card : ALL) {
            StackPane cardPane = MapViewUtils.stackPane(card != LOCOMOTIVE ? card.name() : NEUTRAL_SC, CARD_SC);

            ReadOnlyIntegerProperty count = gameState.card(card.ordinal());
            cardPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

            Rectangle outside = new Rectangle(OUTSIDE_WIDTH, OUTSIDE_HEIGHT);
            outside.getStyleClass().add(OUTSIDE_SC);

            Rectangle inside = new Rectangle(INSIDE_WIDTH, INSIDE_HEIGHT);
            inside.getStyleClass().addAll(INSIDE_SC, FILLED_SC);

            Rectangle trainImage = new Rectangle(INSIDE_WIDTH, INSIDE_HEIGHT);
            trainImage.getStyleClass().add(TRAIN_IMAGE_SC);

            Text text = MapViewUtils.text(COUNT_SC);
            text.textProperty().bind(Bindings.convert(count)); // pas sur du tout
            text.visibleProperty().bind(Bindings.greaterThan(count, 1));

            cardPane.getChildren().addAll(outside, inside, trainImage, text);

            handPaneHBox.getChildren().add(cardPane);
        }

        rootHBox.getChildren().addAll(tickets, handPaneHBox);
    }

    public static void createCardsView(ObservableGameState gameState,
                                ObjectProperty<DrawTicketHandler> drawTicketHP,
                                ObjectProperty<DrawCardHandler> drawCardHP){

        VBox rootVBox = MapViewUtils.vBox(CARD_PANE_ID, "decks.css", "colors.css");

        for(int i = 1; i <= 2; i++) {
            Button button = MapViewUtils.button(GAUGED_SC);
            MapViewUtils.addChildrenPane(rootVBox, button);

            Group buttonGroup = new Group();
            Rectangle background = MapViewUtils.rectangle(GAUGE_WIDTH, GAUGE_HEIGHT, BACKGROUND_SC);
            Rectangle foreground = MapViewUtils.rectangle(GAUGE_WIDTH, GAUGE_HEIGHT, FOREGROUND_SC);
            MapViewUtils.addChildrenGroup(buttonGroup, background, foreground);

        }

        for (int i = 0; i < Constants.FACE_UP_CARDS_COUNT; i++) {
            Card card = gameState.faceUpCard(i).getValue();
            StackPane pane = MapViewUtils.stackPane(card != LOCOMOTIVE ? card.color().name() : NEUTRAL_SC, CARD_SC);

            Rectangle outside = MapViewUtils.rectangle(OUTSIDE_WIDTH, OUTSIDE_HEIGHT, OUTSIDE_SC);
            MapViewUtils.addChildrenPane(pane, outside);

            Rectangle inside = MapViewUtils.rectangle(INSIDE_WIDTH, INSIDE_HEIGHT, INSIDE_SC, FILLED_SC);
            MapViewUtils.addChildrenPane(pane, inside);

            Rectangle trainImage = MapViewUtils.rectangle(INSIDE_WIDTH, INSIDE_HEIGHT, TRAIN_IMAGE_SC);
            MapViewUtils.addChildrenPane(pane, trainImage);
        }
    }

    private static StackPane cardPaneOf(List<Card> cards){
        
    }
}


















