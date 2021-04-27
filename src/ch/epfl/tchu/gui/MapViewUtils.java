package ch.epfl.tchu.gui;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class MapViewUtils {

    private MapViewUtils() {
    }

    public static Rectangle rectangle(double width, double height, String... elements) {
        Rectangle rect = new Rectangle(width, height);
        rect.getStyleClass().addAll(elements);
        return rect;
    }

    public static Circle circle(double x, double y, double radius, String... elements) {
        Circle circle = new Circle(x, y, radius);
        circle.getStyleClass().addAll(elements);
        return circle;
    }

    public static Text text(String... elements) {
        Text text = new Text();
        text.getStyleClass().addAll(elements);
        return text;
    }

    public static Button button(String... elements) {
        Button button = new Button();
        button.getStylesheets().addAll(elements);
        return button;
    }

    public static Group group(String id) {
        Group group = new Group();
        group.setId(id);
        return group;
    }

    public static Group groupWithoutId(String element) {
        Group group = new Group();
        group.getStyleClass().add(element);
        return group;
    }

    public static Group group(String id, String... elements) {
        Group group = new Group();
        group.getStyleClass().addAll(elements);
        group.setId(id);
        return group;
    }

    public static Pane pane(String... elements) {
        Pane pane = new Pane();
        pane.getStyleClass().addAll(elements);
        return pane;
    }

    public static HBox hBoxWithoutId(String... elements) {
        HBox hBox = new HBox();
        hBox.getStyleClass().addAll(elements);
        return hBox;
    }

    public static HBox hBox(String id) {
        HBox hBox = new HBox();
        hBox.setId(id);
        return hBox;
    }

    public static VBox vBox(String id, String... elements) {
        VBox vBox = new VBox();
        vBox.getStyleClass().addAll(elements);
        vBox.setId(id);
        return vBox;
    }

    public static void addChildrenPane(Pane parent, Node... children){
        parent.getChildren().addAll(children);
    }
    public static void addChildrenGroup(Group parent, Node... children){
        parent.getChildren().addAll(children);
    }

    public static <T> ListView<T> listView(String id) {
        ListView<T> list = new ListView();
        list.setId(id);
        return list;
    }

    public static StackPane stackPane(String... elements) {
        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().addAll(elements);
        return stackPane;
    }

    public static void showStageOf(Parent root){
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }
}
