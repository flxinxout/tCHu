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
        String hostName = args.isEmpty() ? StringsFr.DEFAULT_HOSTNAME : args.get(0);
        int port = args.size() < 2 ? StringsFr.DEFAULT_PORT : Integer.parseInt(args.get(1));

        GraphicalPlayerAdapter playerAdapter = new GraphicalPlayerAdapter();
        RemotePlayerClient playerClient = new RemotePlayerClient(playerAdapter, hostName, port);

        new Thread(playerClient::run).start();
    }
}
