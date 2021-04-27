package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import static ch.epfl.tchu.game.Card.*;
import static ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import static ch.epfl.tchu.gui.ActionHandlers.DrawTicketHandler;

class DecksViewCreator {
    private final static String TICKETS_ID = "tickets";
    private final static String HAND_PANE_ID = "hand-pane";

    private final static String NEUTRAL_SC = "NEUTRAL";
    private final static String CARD_SC = "card";
    private final static String OUTSIDE_SC = "outside";
    private final static String INSIDE_SC = "inside";
    private final static String FILLED_SC = "filled";
    private final static String TRAIN_IMAGE_SC = "train-image";
    private final static String COUNT_SC = "count";

    private final static double OUTSIDE_WIDTH = 60d;
    private final static double OUTSIDE_HEIGHT = 90d;
    private final static double INSIDE_WIDTH = 40d;
    private final static double INSIDE_HEIGHT = 70d;


    public DecksViewCreator createHandView(ObservableGameState gameState) {
        HBox rootHBox = new HBox();
        rootHBox.getStylesheets().add("tCHu/resources/decks.css");
        rootHBox.getStylesheets().add("tCHu/resources/colors.css");

        ListView tickets = new ListView();
        tickets.setId(TICKETS_ID);

        HBox handPaneHBox = new HBox();
        handPaneHBox.setId(HAND_PANE_ID);

        for (Card card : ALL) {
            StackPane cardPane = new StackPane();
            cardPane.getStyleClass().add(card != LOCOMOTIVE ? card.name() : NEUTRAL_SC);
            cardPane.getStyleClass().add(CARD_SC);

            Rectangle outside = new Rectangle();
            outside.getStyleClass().add(OUTSIDE_SC);
            outside.setHeight(OUTSIDE_HEIGHT);
            outside.setWidth(OUTSIDE_WIDTH);
            cardPane.getChildren().add(outside);

            Rectangle inside = new Rectangle();
            inside.getStyleClass().add(INSIDE_SC);
            inside.getStyleClass().add(FILLED_SC);
            inside.setHeight(INSIDE_HEIGHT);
            inside.setWidth(INSIDE_WIDTH);
            cardPane.getChildren().add(inside);

            Rectangle trainImage = new Rectangle();
            trainImage.getStyleClass().add(TRAIN_IMAGE_SC);
            trainImage.setHeight(INSIDE_HEIGHT);
            trainImage.setWidth(INSIDE_WIDTH);
            cardPane.getChildren().add(trainImage);

            Text count = new Text();
            count.getStyleClass().add(COUNT_SC);

            handPaneHBox.getChildren().add(cardPane);
        }
    }

    public void createCardsView(ObservableGameState gameState,
                                ObjectProperty<DrawTicketHandler> drawTicketHP,
                                ObjectProperty<DrawCardHandler> drawCardHP){

    }
}
