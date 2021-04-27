package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Deck;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static ch.epfl.tchu.game.Card.*;
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

    private final static double OUTSIDE_WIDTH = 60D;
    private final static double OUTSIDE_HEIGHT = 90D;
    private final static double INSIDE_WIDTH = 40D;
    private final static double INSIDE_HEIGHT = 70D;
    private final static double GAUGE_WIDTH = 50D;
    private final static double GAUGE_HEIGHT = 5D;

    private DecksViewCreator() {
    }

    public static DecksViewCreator createHandView(ObservableGameState gameState) {
        HBox rootHBox = MapViewUtils.hBoxWithoutId("decks.css", "colors.css");
        ListView tickets = MapViewUtils.listView(TICKETS_ID);
        HBox handPaneHBox = MapViewUtils.hBox(HAND_PANE_ID);

        for (Card card : ALL) {
            StackPane cardPane = MapViewUtils.stackPane(card != LOCOMOTIVE ? card.name() : NEUTRAL_SC, CARD_SC);

            ReadOnlyIntegerProperty count = gameState.card(card.ordinal());
            cardPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

            Rectangle outside = MapViewUtils.rectangle(OUTSIDE_WIDTH, OUTSIDE_HEIGHT, OUTSIDE_SC);
            MapViewUtils.addChildrenPane(cardPane, outside);

            Rectangle inside = MapViewUtils.rectangle(INSIDE_WIDTH, INSIDE_HEIGHT, INSIDE_SC, FILLED_SC);
            MapViewUtils.addChildrenPane(cardPane, inside);

            Rectangle trainImage = MapViewUtils.rectangle(INSIDE_WIDTH, INSIDE_HEIGHT, TRAIN_IMAGE_SC);
            MapViewUtils.addChildrenPane(cardPane, trainImage);

            Text text = MapViewUtils.text(COUNT_SC);
            text.textProperty().bind(Bindings.convert(count)); // pas sur du tout
            text.visibleProperty().bind(Bindings.greaterThan(count, 1));
            MapViewUtils.addChildrenPane(cardPane, text);




            handPaneHBox.getChildren().add(cardPane);
        }

        rootHBox.getChildren().add(tickets);
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
}


















