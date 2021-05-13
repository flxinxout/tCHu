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
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ch.epfl.tchu.game.Constants.INITIAL_TICKETS_COUNT;
import static ch.epfl.tchu.game.Constants.IN_GAME_TICKETS_COUNT;
import static ch.epfl.tchu.gui.ActionHandlers.*;
import static ch.epfl.tchu.gui.DecksViewCreator.createCardsView;
import static ch.epfl.tchu.gui.DecksViewCreator.createHandView;
import static ch.epfl.tchu.gui.Info.cardName;
import static ch.epfl.tchu.gui.InfoViewCreator.createInfoView;
import static ch.epfl.tchu.gui.MapViewCreator.createMapView;
import static ch.epfl.tchu.gui.StringsFr.*;
import static javafx.application.Platform.isFxApplicationThread;

/**
 * Interface graphique d'un joueur de tCHu.
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
     * Construit l'interface graphique du joueur donné et ouvre sa fenêtre principale.
     *
     * @param playerId    l'identité du joueur lié à cette interface graphique
     * @param playerNames la table associative entre les joueurs et leur nom
     */
    public GraphicalPlayer(PlayerId playerId, Map<PlayerId, String> playerNames) {
        assert isFxApplicationThread();
        gameState = new ObservableGameState(playerId);
        texts = FXCollections.observableArrayList();

        mainStage = new Stage();
        mainStage.setTitle("tCHu \u2014 " + playerNames.get(playerId));

        claimRouteHP = new SimpleObjectProperty<>();
        drawTicketsHP = new SimpleObjectProperty<>();
        drawCardHP = new SimpleObjectProperty<>();

        Node mapView = createMapView(gameState, claimRouteHP, this::chooseClaimCards);
        Node cardsView = createCardsView(gameState, drawTicketsHP, drawCardHP);
        Node handView = createHandView(gameState);
        Node infoView = createInfoView(playerId, playerNames, gameState, texts);

        BorderPane mainPane = new BorderPane(mapView, null, cardsView, handView, infoView);
        mainStage.setScene(new Scene(mainPane));
        mainStage.show();
    }

    /**
     * Met à jour la totalité des propriétés de l'état de jeu lié à cette interface graphique
     * en fonction des deux états donnés.
     *
     * @param newGameState le nouvel état de jeu
     * @param playerState  le nouvel état du joueur associé à cette interface graphique
     * @see ObservableGameState#setState(PublicGameState, PlayerState)
     */
    public void setState(PublicGameState newGameState, PlayerState playerState) {
        assert isFxApplicationThread();
        gameState.setState(newGameState, playerState);
    }

    /**
     * Ajoute l'information donnée aux messages d'information de cette interface graphique. S'il y a déjà 5 messages
     * affichés, le plus ancien est remplacé par celui donné.
     *
     * @param message la nouvelle information à afficher
     */
    public void receiveInfo(String message) {
        assert isFxApplicationThread();
        if (texts.size() >= 5)
            texts.remove(0);
        texts.add(new Text(message));
    }

    /**
     * Autorise le joueur à effectuer une action durant son tour en fonction de l'état actuel du jeu.
     * Remplit les propriétés des gestionnaires d'action de cette interface graphique à l'aide des 3 autres donnés.
     * Une fonction de vidage des propriétés est automatiquement ajoutée aux gestionnaires donnés.
     *
     * @param drawTicketsH le gestionnaire d'action pour tirer des billets
     * @param drawCardH    le gestionnaire d'action pour tirer des cartes
     * @param claimRouteH  le gestionnaire d'action pour tenter de s'emparer d'une route
     */
    public void startTurn(DrawTicketsHandler drawTicketsH, DrawCardHandler drawCardH, ClaimRouteHandler claimRouteH) {
        assert isFxApplicationThread();

        drawTicketsHP.set(gameState.canDrawTickets() ? () -> {
            clearHandlerProperties();
            drawTicketsH.onDrawTickets();
        } : null);

        drawCardHP.set(gameState.canDrawCards() ? slot -> {
            clearHandlerProperties();
            drawCardH.onDrawCard(slot);
        } : null);

        claimRouteHP.set((Route r, SortedBag<Card> c) -> {
            clearHandlerProperties();
            claimRouteH.onClaimRoute(r, c);
        });
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix entre les billets donnés; une fois celui-ci confirmé,
     * le gestionnaire d'action donné est appelé avec ce choix en argument et la fenêtre est fermée.
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

        ListView<Ticket> optionsLV = new ListView<>(FXCollections.observableArrayList(options.toList()));
        optionsLV.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Button confirmB = new Button(CHOOSE);
        confirmB.disableProperty()
                .bind(Bindings.size(optionsLV.getSelectionModel().getSelectedItems())
                        .lessThan(minCount));

        Stage stage = createSelectionStage(TICKETS_CHOICE,
                String.format(CHOOSE_TICKETS, minCount, plural(minCount)),
                optionsLV,
                confirmB);

        confirmB.setOnAction(e -> {
            chooseTicketsH.onChooseTickets(SortedBag.of(optionsLV.getSelectionModel().getSelectedItems()));
            stage.hide();
        });

        stage.show();
    }

    /**
     * Autorise le joueur a tirer une carte, soit l'une des cinq dont la face est visible, soit celle
     * du sommet de la pioche; une fois que le joueur a choisi l'une de ces cartes, le gestionnaire est appelé avec
     * ce choix; cette méthode est destinée à être appelée lorsque le joueur a déjà tiré une première carte et
     * doit maintenant tirer la seconde.
     *
     * @param drawCardH le gestionnaire d'action pour tirer des cartes
     */
    public void drawCard(DrawCardHandler drawCardH) {
        assert isFxApplicationThread();
        drawCardHP.set(slot -> {
            clearHandlerProperties();
            drawCardH.onDrawCard(slot);
        });
    }

    /**
     * Ouvre une fenêtre permettant au joueur de faire son choix sur la route qu'il veut tenter de s'emparer;
     * une fois que celui-ci a été fait et confirmé, le gestionnaire de choix est appelé avec le choix du joueur en
     * argument et la fenêtre est fermée; cette méthode n'est destinée qu'à être passée en argument à createMapView
     * en tant que valeur de type CardChooser.
     *
     * @param initialCards les ensembles de cartes initiales que le joueur peut utiliser
     * @param chooseCardsH le gestionnaire d'action pour choisir des cartes
     */
    public void chooseClaimCards(List<SortedBag<Card>> initialCards, ChooseCardsHandler chooseCardsH) {
        assert isFxApplicationThread();

        ListView<SortedBag<Card>> optionsLV = new ListView<>(FXCollections.observableArrayList(initialCards));
        optionsLV.setCellFactory(v -> new TextFieldListCell<>(new CardBagStringConverter()));

        Button confirmB = new Button(CHOOSE);
        confirmB.disableProperty().bind(Bindings.size(optionsLV.getSelectionModel().getSelectedItems()).lessThan(1));

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
     * le choix du joueur en argument et la fenêtre est fermée.
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
            SortedBag<Card> selectedCards = optionsLV.getSelectionModel().getSelectedItem();
            chooseCardsH.onChooseCards(selectedCards == null ? SortedBag.of() : selectedCards);
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
        introTextFlow.getChildren().add(new Text(introString));

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
        drawTicketsHP.set(null);
        drawCardHP.set(null);
        claimRouteHP.set(null);
    }

    /**
     * Convertit un multi-ensemble de cartes en une chaîne de caractères.
     *
     * @author Dylan Vairoli (326603)
     * @author Giovanni Ranieri (326870)
     */
    private final static class CardBagStringConverter extends StringConverter<SortedBag<Card>> {

        /**
         * Le nombre maximal de types de cartes différents autorisé pour s'emparer d'une route.
         */
        private static final int CARD_TYPES_COUNT = 2;

        /**
         * Retourne la représentation textuelle de l'ensemble de cartes donné. Il ne doit pas contenir plus de 2 types
         * de carte différents.
         *
         * @param cards l'ensemble de cartes
         * @return la représentation textuelle de l'ensemble de cartes donné
         * @throws IllegalArgumentException si l'ensemble de cartes contient plus de deux types de carte différents
         */
        @Override
        public String toString(SortedBag<Card> cards) {
            Preconditions.checkArgument(cards.toSet().size() <= CARD_TYPES_COUNT);
            List<String> singleCardNames = new ArrayList<>(CARD_TYPES_COUNT);

            for (Card c : cards.toSet()) {
                int n = cards.countOf(c);
                singleCardNames.add(n + " " + cardName(c, n));
            }

            return String.join(AND_SEPARATOR, singleCardNames);
        }

        @Override
        public SortedBag<Card> fromString(String string) {
            throw new UnsupportedOperationException();
        }
    }
}