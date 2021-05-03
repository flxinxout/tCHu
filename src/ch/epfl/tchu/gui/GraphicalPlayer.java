package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
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

import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Constants.INITIAL_TICKETS_COUNT;
import static ch.epfl.tchu.game.Constants.IN_GAME_TICKETS_COUNT;
import static ch.epfl.tchu.gui.ActionHandlers.*;
import static javafx.application.Platform.isFxApplicationThread;

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

    private final ObjectProperty<ClaimRouteHandler> claimRoute;
    private final ObjectProperty<DrawTicketsHandler> drawTickets;
    private final ObjectProperty<DrawCardHandler> drawCard;

    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.playerId = playerId;
        this.playerNames = playerNames;
        this.gameState = new ObservableGameState(playerId);
        this.texts = FXCollections.emptyObservableList();

        this.primaryStage = new Stage();
        primaryStage.setTitle("tCHu \u2014 " + playerNames.get(playerId));

        this.claimRoute = new SimpleObjectProperty<>();
        this.drawTickets = new SimpleObjectProperty<>();
        this.drawCard = new SimpleObjectProperty<>();

        Node mapView = MapViewCreator.createMapView(gameState, claimRoute, this::chooseClaimCards);
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
        assert isFxApplicationThread();
        gameState.setState(newGameState, playerState);
    }

    public void receiveInfo(String message) {
        assert isFxApplicationThread();

        if (texts.size() == 5)
            texts.remove(0);

        texts.add(new Text(message));
    }

    /**
     * Permet au joueur d'effectuer une action parmis les 3 gestionnaires d'actions passés en paramètre de la méthode.
     *
     * @param drawTicketsH le gestionnaire d'action pour tirer des billets
     * @param drawCardH le gestionnaire d'action pour tirer des cartes
     * @param claimRouteH le gestionnaire d'action pour tenter de s'emparer d'une route
     */
    public void startTurn(DrawTicketsHandler drawTicketsH, DrawCardHandler drawCardH, ClaimRouteHandler claimRouteH) {
        assert isFxApplicationThread();

        //TODO: J'ai pas réussi à faire une fonction si tu y arrives c'est nice car c'est des functional interface
        //TODO: donc essaye d'en faire une méthode simple efficace car là bordel ma tete elle a pas réussi mdrrrr
        drawTickets.setValue(() -> {
            drawTickets.setValue(null);
            drawCard.setValue(null);
            claimRoute.setValue(null);
            drawTicketsH.onDrawTickets();
        });

        drawCard.setValue(slot -> {
            drawTickets.setValue(null);
            drawCard.setValue(null);
            claimRoute.setValue(null);
            drawCardH.onDrawCard(slot);
        });

        claimRoute.setValue((Route r, SortedBag<Card> c) -> {
            drawTickets.setValue(null);
            drawCard.setValue(null);
            claimRoute.setValue(null);
            claimRouteH.onClaimRoute(r, c);
        });

        // 3.2.1 partie avec les 3 points à la suite.
        if(gameState.canDrawTickets())
            drawTickets.setValue(drawTicketsH);

        if(gameState.canDrawCards())
            drawCard.setValue(drawCardH);

        claimRoute.setValue(claimRouteH);

    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix sur les billets à choisir; une fois celui-ci confirmé,
     * le gestionnaire de choix est appelé avec ce choix en argument.
     *
     * @param options les choix des billets que le joueur peut choisir
     * @param chooseTicketsH le gestionnaire d'action pour tirer des billets
     */
    public void chooseTickets(SortedBag<Ticket> options, ChooseTicketsHandler chooseTicketsH) {
        assert isFxApplicationThread();
        Preconditions.checkArgument(options.size() == INITIAL_TICKETS_COUNT ||
                options.size() == IN_GAME_TICKETS_COUNT);

        Stage selectionStage = createTicketsSelectionStage(options, chooseTicketsH); // jla sens static aussi donc le stage il va sauté...
        selectionStage.show();
    }

    /**
     * Autorise le joueur a choisir une carte wagon/locomotive, soit l'une des cinq dont la face est visible, soit celle
     * du sommet de la pioche; une fois que le joueur a cliqué sur l'une de ces cartes, le gestionnaire est appelé avec
     * le choix du joueur; cette méthode est destinée à être appelée lorsque le joueur a déjà tiré une première carte et
     * doit maintenant tirer la seconde.
     *
     * @param drawCardH le gestionnaire d'action pour tirer des cartes
     */
    public void drawCard(DrawCardHandler drawCardH) {
        assert isFxApplicationThread();
        //TODO: va falloir la comprendre celle là mdr
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix sur la route qu'il veut tenter de s'emparer;
     * une fois que celui-ci a été fait et confirmé, le gestionnaire de choix est appelé avec le choix du joueur en
     * argument; cette méthode n'est destinée qu'à être passée en argument à createMapView en tant que valeur de type
     * CardChooser.
     *
     * @param initialCards les cartes initiales que le joueur peut utiliser
     * @param chooseCardsH le gestionnaire d'action pour choisir des cartes
     */
    public void chooseClaimCards(List<SortedBag<Card>> initialCards, ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();
        Stage selectionStage = createInitialCardsSelectionStage(initialCards, chooseCardsH);
        selectionStage.show();
    }

    /**
     * Ouvre une fenêtre  permettant au joueur de faire son choix sur les cartes additionelles qu'il peut utiliser pour
     * s'emparer d'un tunnel; une fois que celui-ci a été fait et confirmé, le gestionnaire de choix est appelé avec
     * le choix du joueur en argument.
     *
     * @param additionalCards les options des cartes qu'il peut choisir
     * @param chooseCardsH le gestionnaire d'action pour choisir des cartes
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();
        Stage chooseAdditionalCardsStage = createAdditionalCardsSelectionStage(primaryStage, additionalCards, chooseCardsH);
        chooseAdditionalCardsStage.show();
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
        Stage stage = createChooserStageOf(StringsFr.TICKETS_CHOICE, root);

        confirmB.setOnAction(e -> {
            stage.hide();
            chooseTicketsH.onChooseTickets(SortedBag.of(optionsLV.getSelectionModel().getSelectedItems()));
        });

        return stage;
    }

    private Stage createInitialCardsSelectionStage(List<SortedBag<Card>> options, ChooseCardsHandler chooseCardsH) {
        VBox root = new VBox();

        TextFlow introTextFlow = new TextFlow();
        Text introText = new Text(StringsFr.CHOOSE_CARDS);
        introTextFlow.getChildren().add(introText);

        ListView<SortedBag<Card>> optionsLV = new ListView<>(FXCollections.observableArrayList(options));
        optionsLV.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button confirmB = new Button();
        confirmB.disableProperty().bind(Bindings.size(optionsLV.getSelectionModel().getSelectedItems()).lessThan(1)); // maybe constant ? // juste ?

        root.getChildren().addAll(introTextFlow, optionsLV, confirmB);
        Stage stage = createChooserStageOf(StringsFr.CARDS_CHOICE, root);

        confirmB.setOnAction(e -> {
            stage.hide();
            chooseCardsH.onChooseCards(optionsLV.getSelectionModel().getSelectedItem());
        });

        return stage;
    }

    private Stage createAdditionalCardsSelectionStage(Stage owner, List<SortedBag<Card>> options, ChooseCardsHandler chooseCardsH) {
        VBox root = new VBox();

        TextFlow introTextFlow = new TextFlow();
        Text introText = new Text(StringsFr.CHOOSE_ADDITIONAL_CARDS);
        introTextFlow.getChildren().add(introText);

        ListView<SortedBag<Card>> optionsLV = new ListView<>(FXCollections.observableArrayList(options));
        optionsLV.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button confirmB = new Button();
        //TODO: je crois tu t'es trompé de méthode, si je lis bien pour les additional card y'a rien mais au dessus oui !
        //confirmB.disableProperty().bind(new SimpleBooleanProperty(optionsLV.getSelectionModel().getSelectedItem() != null));

        root.getChildren().addAll(introTextFlow, optionsLV, confirmB);
        Stage stage = createChooserStageOf(StringsFr.CARDS_CHOICE, root);

        confirmB.setOnAction(e -> {
            stage.hide();
            chooseCardsH.onChooseCards(optionsLV.getSelectionModel().getSelectedItem());
        });

        return stage;
    }

    private Stage createChooserStageOf(String title, Parent root) {
        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(primaryStage);
        stage.setTitle(title);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(Event::consume);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);
        return stage;
    }
}