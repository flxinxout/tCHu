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

import static ch.epfl.tchu.game.Card.ALL;
import static ch.epfl.tchu.game.Card.LOCOMOTIVE;
import static ch.epfl.tchu.gui.ActionHandlers.DrawCardHandler;
import static ch.epfl.tchu.gui.ActionHandlers.DrawTicketHandler;
import static ch.epfl.tchu.gui.MapViewUtils.*;

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
    private final static double GAUGE_INITIAL_WIDTH = 50D;
    private final static double GAUGE_HEIGHT = 5D;

    private DecksViewCreator() { }

    public static void createHandView(ObservableGameState gameState) {
        HBox root = hBoxWithoutId("decks.css", "colors.css");
        ListView<Rectangle> tickets = listView(TICKETS_ID);
        HBox handPaneHBox = hBox(HAND_PANE_ID);

        for (Card cardType : ALL) {
            StackPane card = stackPaneOf(cardType);

            Text text = text(COUNT_SC);
            card.getChildren().add(text);

            ReadOnlyIntegerProperty count = gameState.cardOccurrencesProperty(cardType);
            card.visibleProperty().bind(Bindings.greaterThan(count, 0));

            text.textProperty().bind(Bindings.convert(count));
            text.visibleProperty().bind(Bindings.greaterThan(count, 1));

            handPaneHBox.getChildren().add(card);
        }
        root.getChildren().addAll(tickets, handPaneHBox);

        showStageOf(root);
    }

    public static void createCardsView(ObservableGameState gameState,
                                ObjectProperty<DrawTicketHandler> drawTicketHP,
                                ObjectProperty<DrawCardHandler> drawCardHP){
        VBox root = vBox(CARD_PANE_ID, "decks.css", "colors.css");

        //TODO: VERIFIER QUE CA JOUE MEME SI LE BIND EST PASSE EN ARGUMENT
        Button ticketsB = createButton(gameState.ticketsPercentageProperty());
        ticketsB.disableProperty().bind(drawTicketHP.isNull());
        ticketsB.setOnMouseClicked(e -> drawTicketHP.get().onDrawTickets());

        //TODO: VERIFIER QUE CA JOUE MEME SI LE BIND EST PASSE EN ARGUMENT
        Button cardsB = createButton(gameState.cardsPercentageProperty());
        cardsB.disableProperty().bind(drawCardHP.isNull());
        cardsB.setOnMouseClicked(e -> drawCardHP.get().onDrawCard(-1));

        root.getChildren().addAll(ticketsB, cardsB);

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card card = gameState.faceUpCardPropertyAt(slot).get();
            StackPane cardPane = stackPaneOf(card);
            gameState.faceUpCardPropertyAt(slot).addListener((o, oV, nV) -> cardPane.getStyleClass().set(
                    cardPane.getStyleClass().indexOf(oV),
                    nV != LOCOMOTIVE ? nV.name() : NEUTRAL_SC));
            root.getChildren().add(cardPane);
        }

        showStageOf(root);
    }

    private static StackPane stackPaneOf(Card card){
        StackPane cardPane = stackPane(card != LOCOMOTIVE ? card.name() : NEUTRAL_SC, CARD_SC);

        Rectangle outside = new Rectangle(OUTSIDE_WIDTH, OUTSIDE_HEIGHT);
        outside.getStyleClass().add(OUTSIDE_SC);

        Rectangle inside = new Rectangle(INSIDE_WIDTH, INSIDE_HEIGHT);
        inside.getStyleClass().addAll(INSIDE_SC, FILLED_SC);

        Rectangle trainImage = new Rectangle(INSIDE_WIDTH, INSIDE_HEIGHT);
        trainImage.getStyleClass().add(TRAIN_IMAGE_SC);

        cardPane.getChildren().addAll(outside, inside, trainImage);

        return cardPane;
    }

    private static Button createButton(ReadOnlyIntegerProperty gaugePercentage){
        Button button = button(GAUGED_SC);

        Rectangle gaugeBackground = rectangle(GAUGE_INITIAL_WIDTH, GAUGE_HEIGHT, BACKGROUND_SC);
        Rectangle gaugeForeground = rectangle(GAUGE_INITIAL_WIDTH, GAUGE_HEIGHT, FOREGROUND_SC);
        gaugeForeground.widthProperty().bind(gaugePercentage.multiply(50).divide(100));

        Group buttonGroup = new Group(gaugeBackground, gaugeForeground);
        button.setGraphic(buttonGroup);

        return button;
    }
}


















