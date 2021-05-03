package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import javax.script.Bindings;

import static ch.epfl.tchu.gui.ActionHandlers.*;

import java.util.List;
import java.util.Map;

public class GraphicalPlayer {

    private static Node createTicketsSelectionView(Window window) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(window);
        stage.initModality(Modality.WINDOW_MODAL);

        VBox vBox = new VBox();
        TextFlow textFlow = new TextFlow();
        ListView<Text> textListView = new ListView<>();

        Text text = new Text(String.format(StringsFr.CHOOSE_TICKETS, )); //TODO: !!
        textFlow.getChildren().addAll(text);

        Button button = new Button();

        vBox.getChildren().addAll(textFlow, textListView, button);
        Scene scene = new Scene(vBox);
        scene.getStylesheets().add("chooser.css");

        stage.setScene(scene);
    }

    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState gameState;

    private final ObservableList<Text> texts;

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.playerId = playerId;
        this.playerNames = playerNames;

        Stage primaryStage = new Stage();
        primaryStage.setTitle("tCHu \\u2014 " + playerNames.get(playerId));

        this.gameState = new ObservableGameState(playerId);
        this.texts = FXCollections.emptyObservableList();

        ObjectProperty<ActionHandlers.ClaimRouteHandler> claimRoute = new SimpleObjectProperty<>();
        ObjectProperty<ActionHandlers.DrawTicketsHandler> drawTickets = new SimpleObjectProperty<>();
        ObjectProperty<ActionHandlers.DrawCardHandler> drawCard = new SimpleObjectProperty<>();

        Node mapView = MapViewCreator.createMapView(gameState, claimRoute, GraphicalPlayer::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTickets, drawCard);
        Node handView = DecksViewCreator.createHandView(gameState);
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, gameState, texts);

        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

    public void setState(PublicGameState newGameState, PlayerState playerState) {
        gameState.setState(newGameState, playerState);
    }

    public void receiveInfo(String message) {
        if(texts.size() == 5)
            texts.remove(0);

        texts.add(new Text(message));
    }

    public void startTurn(DrawTicketsHandler drawTicketsH, DrawCardHandler drawCardH, ClaimRouteHandler claimRouteH) {
        
    }

    public void chooseTickets(SortedBag<Ticket> tickets, ChooseTicketsHandler chooseTicketsH) {
        Preconditions.checkArgument(tickets.size() == Constants.INITIAL_TICKETS_COUNT || tickets.size() == Constants.IN_GAME_TICKETS_COUNT);
    }

    public void drawCard(DrawCardHandler drawCardH) {

    }

    public static void chooseClaimCards(List<SortedBag<Card>> initialCards, ChooseCardsHandler chooseCardsH) {

    }

    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ChooseCardsHandler chooseCardsH) {

    }




}





















