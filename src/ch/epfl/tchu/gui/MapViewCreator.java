package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.List;

class MapViewCreator {

    private MapViewCreator() {
    }

    public static void createMapView(ObservableGameState observableGameState,
                              ObjectProperty<ActionHandlers.ClaimRouteHandler> handler,
                              CardChooser cardChooser) {

        Pane view = new Pane();
        ImageView scene = new ImageView("../../../../../resources/map.png");
        view.getChildren().add(scene);

    }

    @FunctionalInterface
    interface CardChooser {
        void chooseCards(List<SortedBag<Card>> options,
                         ActionHandlers.ChooseCardsHandler handler);
    }

}
