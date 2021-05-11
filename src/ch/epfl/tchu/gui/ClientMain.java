package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;

/**
 * Programme principal du client tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class ClientMain extends Application {
    private static final int DEFAULT_PORT_NUMBER = 5108;
    private static final String DEFAULT_HOST_NAME = "localhost";

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Analyse les arguments passés au programme afin de déterminer le nom de l'hôte et le numéro de port du serveur,
     * crée un client distant associé à un joueur graphique et démarre le fil gérant l'accès au réseau.
     *
     * @param primaryStage la scène principale
     */
    @Override
    public void start(Stage primaryStage) {
        List<String> args = getParameters().getRaw();
        String hostName = args.isEmpty() ? DEFAULT_HOST_NAME : args.get(0);
        int port = args.size() < 2 ? DEFAULT_PORT_NUMBER : Integer.parseInt(args.get(1));

        GraphicalPlayerAdapter playerAdapter = new GraphicalPlayerAdapter();
        RemotePlayerClient playerClient = new RemotePlayerClient(playerAdapter, hostName, port);

        new Thread(playerClient::run).start();
    }
}
