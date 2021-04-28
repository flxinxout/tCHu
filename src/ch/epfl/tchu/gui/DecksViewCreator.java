package ch.epfl.tchu.gui;

import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Constants;
import ch.epfl.tchu.game.Ticket;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
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
import static ch.epfl.tchu.gui.ActionHandlers.DrawTicketsHandler;

class DecksViewCreator {
    private final static String TICKETS_ID = "tickets";
    private final static String HAND_PANE_ID = "hand-pane";
    private final static String CARD_PANE_ID = "card-pane";

    private final static String TICKETS_BUTTON_TEXT = "Billets";
    private final static String CARDS_BUTTON_TEXT = "Cartes";

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

    private DecksViewCreator() {
    }

    public static Node createHandView(ObservableGameState gameState) {
        HBox root = new HBox();
        root.getStylesheets().addAll("decks.css", "colors.css");

        ListView<Ticket> tickets = new ListView<>(gameState.tickets());
        tickets.setId(TICKETS_ID);

        HBox handPaneHBox = new HBox();
        handPaneHBox.setId(HAND_PANE_ID);

        for (Card card : ALL) {
            StackPane cardPane = stackPaneOf(card);

            Text text = new Text();
            text.getStyleClass().add(COUNT_SC);
            cardPane.getChildren().add(text);

            ReadOnlyIntegerProperty count = gameState.occurrencesOf(card);
            cardPane.visibleProperty().bind(Bindings.greaterThan(count, 0));

            text.textProperty().bind(Bindings.convert(count));
            text.visibleProperty().bind(Bindings.greaterThan(count, 1));

            handPaneHBox.getChildren().add(cardPane);
        }

        root.getChildren().addAll(tickets, handPaneHBox);
        return root;
    }

    public static Node createCardsView(ObservableGameState gameState,
                                       ObjectProperty<DrawTicketsHandler> drawTicketHP,
                                       ObjectProperty<DrawCardHandler> drawCardHP) {
        VBox root = new VBox();
        root.setId(CARD_PANE_ID);
        root.getStylesheets().addAll("decks.css", "colors.css");

        Button ticketsB = buttonOf(gameState.ticketsPercentage());
        ticketsB.setText(TICKETS_BUTTON_TEXT);
        ticketsB.disableProperty().bind(drawTicketHP.isNull());
        ticketsB.setOnMouseClicked(e -> drawTicketHP.get().onDrawTickets());

        root.getChildren().add(ticketsB);

        for (int slot : Constants.FACE_UP_CARD_SLOTS) {
            Card card = gameState.faceUpCardAt(slot).get();
            StackPane cardPane = stackPaneOf(card);
            gameState.faceUpCardAt(slot).addListener((o, oV, nV) -> {
                int index;
                if (oV != null) {
                    index = cardPane.getStyleClass().indexOf(oV != LOCOMOTIVE ? oV.name() : NEUTRAL_SC);
                    cardPane.getStyleClass().set(index, nV != LOCOMOTIVE ? nV.name() : NEUTRAL_SC);
                } else
                    cardPane.getStyleClass().add(nV != LOCOMOTIVE ? nV.name() : NEUTRAL_SC);
            });
            root.getChildren().add(cardPane);
        }

        Button cardsB = buttonOf(gameState.cardsPercentage());
        cardsB.setText(CARDS_BUTTON_TEXT);
        cardsB.disableProperty().bind(drawCardHP.isNull());
        cardsB.setOnMouseClicked(e -> drawCardHP.get().onDrawCard(-1));

        root.getChildren().add(cardsB);
        return root;
    }

    private static StackPane stackPaneOf(Card card) {
        StackPane cardPane = new StackPane();
        if (card != null)
            cardPane.getStyleClass().add(card != LOCOMOTIVE ? card.name() : NEUTRAL_SC);
        cardPane.getStyleClass().add(CARD_SC);

        Rectangle outside = new Rectangle(OUTSIDE_WIDTH, OUTSIDE_HEIGHT);
        outside.getStyleClass().add(OUTSIDE_SC);

        Rectangle inside = new Rectangle(INSIDE_WIDTH, INSIDE_HEIGHT);
        inside.getStyleClass().addAll(INSIDE_SC, FILLED_SC);

        Rectangle trainImage = new Rectangle(INSIDE_WIDTH, INSIDE_HEIGHT);
        trainImage.getStyleClass().add(TRAIN_IMAGE_SC);

        cardPane.getChildren().addAll(outside, inside, trainImage);

        return cardPane;
    }

    private static Button buttonOf(ReadOnlyIntegerProperty gaugePercentage) {
        Button button = new Button();
        button.getStyleClass().add(GAUGED_SC);

        Rectangle gaugeBackground = new Rectangle(GAUGE_INITIAL_WIDTH, GAUGE_HEIGHT);
        gaugeBackground.getStyleClass().add(BACKGROUND_SC);
        Rectangle gaugeForeground = new Rectangle(GAUGE_INITIAL_WIDTH, GAUGE_HEIGHT);
        gaugeForeground.getStyleClass().add(FOREGROUND_SC);
        gaugeForeground.widthProperty().bind(gaugePercentage.multiply(50).divide(100));

        Group gaugeGroup = new Group(gaugeBackground, gaugeForeground);
        button.setGraphic(gaugeGroup);
        return button;
    }
}