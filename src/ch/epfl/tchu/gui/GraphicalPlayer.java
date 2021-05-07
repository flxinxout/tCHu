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
import static ch.epfl.tchu.gui.StringsFr.*;
import static javafx.application.Platform.isFxApplicationThread;

/**
 * Représente l'interface graphique d'un joueur de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class GraphicalPlayer {

    private final ObservableGameState gameState;
    private final ObservableList<Text> texts;
    private final Stage mainStage;
    private final ObjectProperty<ClaimRouteHandler> claimRouteHP;
    private final ObjectProperty<DrawTicketsHandler> drawTicketsHP;
    private final ObjectProperty<DrawCardHandler> drawCardHP;

    /**
     * Construit l'interface graphique du joueur donné.
     *
     * @param playerId    l'identité du joueur lié à cette interface graphique
     * @param playerNames la table associative entre les joueurs et leur nom
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        this.gameState = new ObservableGameState(playerId);
        this.texts = FXCollections.observableArrayList();

        this.mainStage = new Stage();
        mainStage.setTitle("tCHu \u2014 " + playerNames.get(playerId));

        this.claimRouteHP = new SimpleObjectProperty<>();
        this.drawTicketsHP = new SimpleObjectProperty<>();
        this.drawCardHP = new SimpleObjectProperty<>();

        Node mapView = MapViewCreator.createMapView(gameState, claimRouteHP, this::chooseClaimCards);
        Node cardsView = DecksViewCreator.createCardsView(gameState, drawTicketsHP, drawCardHP);
        Node handView = DecksViewCreator.createHandView(gameState);
        Node infoView = InfoViewCreator.createInfoView(playerId, playerNames, gameState, texts);

        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        mainStage.setScene(new Scene(mainPane));
        mainStage.show();
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

    /**
     * Ajoute l'information donnée aux messages informatifs de cette interface graphique. S'il y a déjà 5 messages
     * affichés, le plus ancien est remplacé par celui donné.
     *
     * @param message la nouvelle information à afficher
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (texts.size() == 5)
            texts.remove(0);
        texts.add(new Text(message));
    }

    /**
     * Permet au joueur d'effectuer une action parmi les 3 gestionnaires d'actions passés en paramètre de la méthode.
     *
     * @param drawTicketsH le gestionnaire d'action pour tirer des billets
     * @param drawCardH    le gestionnaire d'action pour tirer des cartes
     * @param claimRouteH  le gestionnaire d'action pour tenter de s'emparer d'une route
     */
    public void startTurn(DrawTicketsHandler drawTicketsH, DrawCardHandler drawCardH, ClaimRouteHandler claimRouteH) {
        assert isFxApplicationThread();

        drawTicketsHP.setValue(!gameState.canDrawTickets() ? null :
                () -> {
                    clearHandlerProperties();
                    drawTicketsH.onDrawTickets();
                });

        drawCardHP.setValue(!gameState.canDrawCards() ? null :
                slot -> {
                    clearHandlerProperties();
                    drawCardH.onDrawCard(slot);
                });

        claimRouteHP.setValue((Route r, SortedBag<Card> c) -> {
            clearHandlerProperties();
            claimRouteH.onClaimRoute(r, c);
        });
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix entre les billets à choisir;
     * une fois celui-ci confirmé, le gestionnaire de choix donné est appelé avec ce choix en argument.
     *
     * @param options        les options de billets parmi lesquelles le joueur peut choisir
     * @param chooseTicketsH le gestionnaire d'action pour tirer des billets
     */
    public void chooseTickets(SortedBag<Ticket> options, ChooseTicketsHandler chooseTicketsH) {
        assert isFxApplicationThread();
        int optionsSize = options.size();
        Preconditions.checkArgument(optionsSize == INITIAL_TICKETS_COUNT ||
                optionsSize == IN_GAME_TICKETS_COUNT);

        int minCount = optionsSize - 2;
        String introText = String.format(CHOOSE_TICKETS, minCount, plural(minCount));

        ListView<Ticket> optionsLV = new ListView<>(FXCollections.observableArrayList(options.toList()));

        Button confirmB = new Button(CHOOSE);
        confirmB.disableProperty().bind(Bindings.size(optionsLV.getSelectionModel().getSelectedItems())
                .lessThan(minCount));

        optionsLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        Stage stage = createSelectionStage(TICKETS_CHOICE,
                introText,
                optionsLV,
                confirmB);

        confirmB.setOnAction(e -> {
            chooseTicketsH.onChooseTickets(SortedBag.of(optionsLV.getSelectionModel().getSelectedItems()));
            stage.hide();
        });

        stage.show();
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
        drawCardHP.setValue(slot -> {
            clearHandlerProperties();
            drawCardH.onDrawCard(slot);
        });
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

        ListView<SortedBag<Card>> optionsLV = new ListView<>(FXCollections.observableArrayList(initialCards));
        optionsLV.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button confirmB = new Button(CHOOSE);
        confirmB.disableProperty().bind(Bindings.size(optionsLV.getSelectionModel().getSelectedItems()).isEqualTo(0));

        Stage stage = createSelectionStage(CARDS_CHOICE,
                CHOOSE_CARDS,
                optionsLV,
                confirmB);

        confirmB.setOnAction(e -> {
            chooseCardsH.onChooseCards(optionsLV.getSelectionModel().getSelectedItem());
            stage.hide();
        });

        stage.show();
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix sur les cartes additionnelles qu'il peut utiliser pour
     * s'emparer d'un tunnel; une fois que celui-ci a été fait et confirmé, le gestionnaire de choix est appelé avec
     * le choix du joueur en argument.
     *
     * @param additionalCards les options des cartes qu'il peut choisir
     * @param chooseCardsH    le gestionnaire d'action pour choisir des cartes
     */
    public void chooseAdditionalCards(List<SortedBag<Card>> additionalCards, ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> optionsLV = new ListView<>(FXCollections.observableArrayList(additionalCards));
        optionsLV.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button confirmB = new Button(CHOOSE);

        Stage stage = createSelectionStage(CARDS_CHOICE,
                CHOOSE_ADDITIONAL_CARDS,
                optionsLV,
                confirmB);

        confirmB.setOnAction(e -> {
            chooseCardsH.onChooseCards(optionsLV.getSelectionModel().getSelectedItem());
            stage.hide();
        });

        stage.show();
    }

    /**
     * Crée une fenêtre de sélection avec le titre, le texte d'introduction, la liste des choix possibles et le bouton
     * de confirmation donnés. Elle est une sous-fenêtre de la fenêtre principale de cette interface graphique.
     *
     * @param title       le titre de la fenêtre
     * @param introString le texte d'introduction
     * @param optionsLV   la liste des choix possibles
     * @param confirmB    le bouton de confirmation
     * @param <T>         le type du contenu de la liste
     * @return la fenêtre de sélection
     */
    private <T> Stage createSelectionStage(String title,
                                           String introString,
                                           ListView<T> optionsLV,
                                           Button confirmB) {
        VBox root = new VBox();

        TextFlow introTextFlow = new TextFlow();
        Text introText = new Text(introString);
        introTextFlow.getChildren().add(introText);

        root.getChildren().addAll(introTextFlow, optionsLV, confirmB);

        Stage stage = new Stage(StageStyle.UTILITY);
        stage.initOwner(mainStage);
        stage.setTitle(title);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setOnCloseRequest(Event::consume);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("chooser.css");
        stage.setScene(scene);
        return stage;
    }

    /**
     * Vide de leur contenu les propriétés contenant les gestionnaires d'action de cette interface graphique.
     */
    private void clearHandlerProperties() {
        drawTicketsHP.setValue(null);
        drawCardHP.setValue(null);
        claimRouteHP.setValue(null);
    }
}