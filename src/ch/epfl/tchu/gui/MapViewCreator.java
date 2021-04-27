package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.w3c.dom.css.Rect;

import java.util.List;

import static ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import static ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;

class MapViewCreator {

    //TODO: demander si c'est bien de créer constantes pours les styles class
    private final static String ROUTE_SC = "route";
    private final static String NEUTRAL_SC = "NEUTRAL";
    private final static String TRACK_SC = "track";
    private final static String FILLED_SC = "filled";
    private final static String CAR_CS = "car";

    private final static double RECT_HEIGHT = 12D;
    private final static double RECT_WIDTH = 36D;
    private final static double CIRCLE_RADIUS = 3D;
    private final static double CIRCLE_MARGIN = 6D;

    private MapViewCreator() {
    }

    /**
     * Permet de créer la vue de la carte à l'aide de l'état du jeu observable, d'une propriété contenant le
     * gestionnaire d'action à utiliser lorsque le joueur désire s'emparer d'une route et d'un sélectionneur de cartes.
     *
     * @param gameState    l'état de jeu observable actuel
     * @param claimRouteHP une propriété contenant le gestionnaire d'action à utiliser
     *                     lorsque le joueur désire s'emparer d'une route
     * @param cardChooser  le sélectionneur de cartes à utiliser
     */
    public static void createMapView(ObservableGameState gameState,
                                     ObjectProperty<ClaimRouteHandler> claimRouteHP,
                                     CardChooser cardChooser) {
        //3.4.1
        Pane rootPane = MapViewUtils.pane("map.css", "colors.css");
        MapViewUtils.addChildrenPane(rootPane, new ImageView());

        ChMap.routes().forEach(route -> {

            //3.4.1
            Group routeGroup = MapViewUtils.group(route.id(), ROUTE_SC, route.level().name(),
                    route.color() == null ? NEUTRAL_SC : route.color().name());

            //3.4.2
            routeGroup.disableProperty().bind(claimRouteHP.isNull().or(gameState.claimable(route).not()));
            gameState.route(ChMap.routes().indexOf(route)).addListener((o, oV, nV) -> {
                if (nV != null)
                    routeGroup.getStyleClass().add(nV.name());
            });

            //3.4.3
            routeGroup.setOnMouseClicked(e -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);
                ClaimRouteHandler claimRouteH = claimRouteHP.get();

                if (possibleClaimCards.size() == 1)
                    claimRouteH.onClaimRoute(route, possibleClaimCards.get(0));
                else {
                    ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteH.onClaimRoute(route, chosenCards);
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
            });

            //3.4.1
            for (int i = 1; i <= route.length(); i++) {
                Group squareGroup = MapViewUtils.group(String.format("%s_%d", route.id(), i));
                Rectangle wayRect = MapViewUtils.rectangle(RECT_WIDTH, RECT_HEIGHT, TRACK_SC, FILLED_SC);
                Group carGroup = MapViewUtils.groupWithoutId(CAR_CS);

                Rectangle carRect = MapViewUtils.rectangle(RECT_WIDTH, RECT_HEIGHT, FILLED_SC);
                MapViewUtils.addChildrenGroup(carGroup, carRect);

                for (int j = 1; j <= 2; j++) {
                    Circle carCircle = MapViewUtils.circle((carRect.getWidth() / 2 - CIRCLE_MARGIN) * j,
                            carRect.getHeight() / 2, CIRCLE_RADIUS, FILLED_SC);
                    MapViewUtils.addChildrenGroup(carGroup, carCircle);
                }

                MapViewUtils.addChildrenGroup(squareGroup, wayRect, carGroup);
                MapViewUtils.addChildrenGroup(routeGroup, squareGroup);
            }

            MapViewUtils.addChildrenPane(rootPane, routeGroup);
        });

        Scene scene = new Scene(rootPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Sélectionneur de cartes.
     */
    @FunctionalInterface
    interface CardChooser {
        /**
         * Est appelée lorsque le joueur doit choisir les cartes qu'il désire utiliser pour s'emparer d'une route.
         * Les possibilités qui s'offrent à lui sont données par l'argument {@code options},
         * tandis que le gestionnaire d'action {@code handler} est destiné à être utilisé lorsqu'il a fait son choix.
         *
         * @param options les différents choix de cartes possibles
         * @param handler le gestionnaire d'action à utiliser lorsque le joueur à fait son choix
         */
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
}