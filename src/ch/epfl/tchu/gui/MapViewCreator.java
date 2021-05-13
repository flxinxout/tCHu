package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.ChMap;
import ch.epfl.tchu.game.Color;
import ch.epfl.tchu.game.Route;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.List;

import static ch.epfl.tchu.gui.ActionHandlers.ChooseCardsHandler;
import static ch.epfl.tchu.gui.ActionHandlers.ClaimRouteHandler;

/**
 * Créateur de la vue de la carte d'une partie de tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
final class MapViewCreator {

    //Package-private car ils sont réutilisés par d'autres classes du package
    final static String FILLED_SC = "filled";

    private final static String CAR_CS = "car";
    private final static String NEUTRAL_SC = "NEUTRAL";
    private final static String ROUTE_SC = "route";
    private final static String TRACK_SC = "track";

    private final static double CIRCLE_MARGIN = 6D;
    private final static double CIRCLE_RADIUS = 3D;
    private final static double RECT_HEIGHT = 12D;
    private final static double RECT_WIDTH = 36D;

    private MapViewCreator() {
    }

    /**
     * Crée la vue de la carte à l'aide de l'état du jeu observable, de la propriété contenant le gestionnaire
     * d'action à utiliser lorsque le joueur désire s'emparer d'une route et du sélectionneur de cartes donnés.
     *
     * @param gameState    l'état de jeu observable
     * @param claimRouteHP la propriété contenant le gestionnaire d'action à utiliser
     *                     lorsque le joueur désire s'emparer d'une route
     * @param cardChooser  le sélectionneur de cartes à utiliser
     * @return la vue de la carte de l'état de jeu observable
     */
    public static Node createMapView(ObservableGameState gameState,
                                     ObjectProperty<ClaimRouteHandler> claimRouteHP,
                                     CardChooser cardChooser) {
        Pane root = new Pane();
        root.getStylesheets().addAll("map.css", "colors.css");
        root.getChildren().add(new ImageView());

        for (Route route : ChMap.routes()) {
            Group routeGroup = new Group();
            routeGroup.setId(route.id());
            routeGroup.getStyleClass().addAll(ROUTE_SC,
                    route.level().name(),
                    colorSC(route.color()));

            gameState.ownerOf(route).addListener((o, oV, nV) -> routeGroup.getStyleClass().add(nV.name()));

            routeGroup.disableProperty().bind(claimRouteHP.isNull().or(gameState.claimable(route).not()));

            routeGroup.setOnMouseClicked(e -> {
                List<SortedBag<Card>> possibleClaimCards = gameState.possibleClaimCards(route);

                if (possibleClaimCards.size() == 1) {
                    claimRouteHP.get().onClaimRoute(route, possibleClaimCards.get(0));
                } else {
                    ChooseCardsHandler chooseCardsH = chosenCards -> claimRouteHP.get().onClaimRoute(route, chosenCards);
                    cardChooser.chooseCards(possibleClaimCards, chooseCardsH);
                }
            });

            for (int i = 1; i <= route.length(); i++) {
                Group squareGroup = new Group();
                squareGroup.setId(String.format("%s_%d", route.id(), i));

                Rectangle railwayRect = new Rectangle(RECT_WIDTH, RECT_HEIGHT);
                railwayRect.getStyleClass().addAll(TRACK_SC, FILLED_SC);

                Group carGroup = new Group();
                carGroup.getStyleClass().add(CAR_CS);

                Rectangle carRect = new Rectangle(RECT_WIDTH, RECT_HEIGHT);
                carRect.getStyleClass().add(FILLED_SC);
                carGroup.getChildren().add(carRect);

                for (int j = 1; j <= 2; j++) {
                    Circle carCircle = new Circle((carRect.getWidth() / 2 - CIRCLE_MARGIN) * j,
                            carRect.getHeight() / 2,
                            CIRCLE_RADIUS);
                    carGroup.getChildren().add(carCircle);
                }

                squareGroup.getChildren().addAll(railwayRect, carGroup);
                routeGroup.getChildren().add(squareGroup);
            }
            root.getChildren().add(routeGroup);
        }
        return root;
    }

    /**
     * Retourne la classe de style correspondante à la couleur donnée. Cette méthode est package-private car elle est
     * utilisée dans plusieurs classes du paquetage.
     *
     * @param color la couleur
     * @return la classe de style attachée à la couleur donnée
     */
    static String colorSC(Color color) {
        return color == null ? NEUTRAL_SC : color.name();
    }

    /**
     * Sélectionneur de cartes.
     *
     * @author Dylan Vairoli (326603)
     * @author Giovanni Ranieri (326870)
     */
    @FunctionalInterface
    interface CardChooser {
        /**
         * Est appelée lorsque le joueur doit choisir les cartes qu'il désire utiliser pour s'emparer d'une route. Il doit
         * les choisir parmi les options données et le gestionnaire d'action donné est utilisé pour effectuer le choix.
         *
         * @param options les différents choix de cartes possibles
         * @param handler le gestionnaire d'action à utiliser lorsque le joueur à fait son choix
         */
        void chooseCards(List<SortedBag<Card>> options, ChooseCardsHandler handler);
    }
}