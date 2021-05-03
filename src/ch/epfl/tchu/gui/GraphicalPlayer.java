package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Constants.INITIAL_TICKETS_COUNT;
import static ch.epfl.tchu.game.Constants.IN_GAME_TICKETS_COUNT;
import static ch.epfl.tchu.gui.ActionHandlers.*;

/**
 * Représente l'interface graphique d'un joueur de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class GraphicalPlayer {

    private final PlayerId playerId;
    private final Map<PlayerId, String> playerNames;
    private final ObservableGameState gameState;
    private final ObservableList<Text> texts;
    private final Stage primaryStage;

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.playerId = playerId;
        this.playerNames = playerNames;

        this.primaryStage = new Stage();
        primaryStage.setTitle("tCHu \u2014 " + playerNames.get(playerId));

        this.gameState = new ObservableGameState(playerId);
        this.texts = FXCollections.emptyObservableList();

        ObjectProperty<ClaimRouteHandler> claimRoute = new SimpleObjectProperty<>();
        ObjectProperty<DrawTicketsHandler> drawTickets = new SimpleObjectProperty<>();
        ObjectProperty<DrawCardHandler> drawCard = new SimpleObjectProperty<>();

        Node mapView = MapViewCreator.createMapView(gameState, claimRoute, GraphicalPlayer::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTickets, drawCard);
        Node handView = DecksViewCreator.createHandView(gameState);
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, gameState, texts);

        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        primaryStage.setScene(new Scene(mainPane));
        primaryStage.show();
    }

    /**
     * Met à jour la totalité des propriétés de l'état lié à ce en fonction des deux états donnés.
     *
     * @param newGameState le nouvel état de jeu
     * @param playerState  le nouvel état du joueur associé à cet état de jeu
     * @see ObservableGameState#setState(PublicGameState, PlayerState)
     */
    public void setState(PublicGameState newGameState, PlayerState playerState) {
        gameState.setState(newGameState, playerState);
    }

    public void receiveInfo(String message) {
        if (texts.size() == 5)
            texts.remove(0);

        texts.add(new Text(message));
    }

    public void startTurn(DrawTicketsHandler drawTicketsH, DrawCardHandler drawCardH, ClaimRouteHandler claimRouteH) {

    }

    public void chooseTickets(SortedBag<Ticket> options, ChooseTicketsHandler chooseTicketsH) {
        Preconditions.checkArgument(options.size() == INITIAL_TICKETS_COUNT ||
                options.size() == IN_GAME_TICKETS_COUNT);

        Stage selectionStage = createTicketsSelectionStage(options, chooseTicketsH);
        selectionStage.show();
    }

    public void drawCard(DrawCardHandler drawCardH) {

    }

    public static void chooseClaimCards(List<SortedBag<Card>> initialCards, ChooseCardsHandler chooseCardsH) {
        Stage selectionStage = createInitialCardsSelectionStage(primary,initialCards, chooseCardsH);
        selectionStage.show();
    }

    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ChooseCardsHandler chooseCardsH) {

    }

    private Stage createTicketsSelectionStage(SortedBag<Ticket> options, ChooseTicketsHandler chooseTicketsH) {
        int minCount = options.size() - 2;

        VBox root = new VBox();

        TextFlow introTextFlow = new TextFlow();
        Text introText = new Text(String.format(StringsFr.CHOOSE_TICKETS, minCount, StringsFr.plural(minCount)));
        introTextFlow.getChildren().add(introText);

        ListView<Ticket> optionsLV = new ListView<>(FXCollections.observableArrayList(options.toList()));
        optionsLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button confirmB = new Button();
        confirmB.disableProperty().bind(Bindings.size(optionsLV.getSelectionModel().getSelectedItems())
                .lessThan(minCount));

        root.getChildren().addAll(introTextFlow, optionsLV, confirmB);
        Stage stage = createChooserStageOf(primaryStage, StringsFr.TICKETS_CHOICE, root);

        confirmB.setOnAction(e -> {
            stage.hide();
            chooseTicketsH.onChooseTickets(SortedBag.of(optionsLV.getSelectionModel().getSelectedItems()));
        });

        return stage;
    }

    //TODO: solve problem with static
    private static Stage createInitialCardsSelectionStage(Window owner, List<SortedBag<Card>> options, ChooseCardsHandler chooseCardsH) {
        VBox root = new VBox();

        TextFlow introTextFlow = new TextFlow();
        Text introText = new Text(StringsFr.CHOOSE_CARDS);
        introTextFlow.getChildren().add(introText);

        ListView<SortedBag<Card>> optionsLV = new ListView<>(FXCollections.observableArrayList(options));
        optionsLV.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button confirmB = new Button();

        root.getChildren().addAll(introTextFlow, optionsLV, confirmB);
        Stage stage = createChooserStageOf(owner, StringsFr.CARDS_CHOICE, root);

        confirmB.setOnAction(e -> {
            stage.hide();
            chooseCardsH.onChooseCards(optionsLV.getSelectionModel().getSelectedItem());
        });

        return stage;
    }

    //TODO: mettre static
    private Stage createAdditionalCardsSelectionStage(List<SortedBag<Card>> options, ChooseCardsHandler chooseCardsH) {
        VBox root = new VBox();

        TextFlow introTextFlow = new TextFlow();
        Text introText = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        introTextFlow.getChildren().add(introText);

        ListView<SortedBag<Card>> optionsLV = new ListView<>(FXCollections.observableArrayList(options));
        optionsLV.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button confirmB = new Button();
        confirmB.disableProperty().bind(new SimpleBooleanProperty(optionsLV.getSelectionModel().getSelectedItem() != null));

        root.getChildren().addAll(introTextFlow, optionsLV, confirmB);
        Stage stage = createChooserStageOf(primaryStage, StringsFr.CARDS_CHOICE, root);

        confirmB.setOnAction(e -> {
            stage.hide();
            chooseCardsH.onChooseCards(optionsLV.getSelectionModel().getSelectedItem());
        });

        return stage;
    }

    private static Stage createChooserStageOf(Window owner, String title, Parent root) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(owner);
        stage.setTitle(title);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(Event::consume);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);
        return stage;
    }
}