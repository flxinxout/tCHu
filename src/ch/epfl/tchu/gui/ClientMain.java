package ch.epfl.tchu.gui;

import ch.epfl.tchu.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Programme principal du client tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class ClientMain extends Application {
    public static void main(String[] args) { launch(args); }

    /**
     * Analyse les arguments passés au programme afin de déterminer le nom de l'hôte et le numéro de port du serveur,
     * crée un client distant associé à un joueur graphique et démarre le fil gérant l'accès au réseau.
     *
     * @param primaryStage le stage principal
     */
    @Override
    public void start(Stage primaryStage) {
        System.out.println(getParameters().getRaw().get(0));
        System.out.println(getParameters().getRaw().get(1));
        GraphicalPlayerAdapter playerAdapter = new GraphicalPlayerAdapter();
        RemotePlayerClient playerClient = new RemotePlayerClient(
                playerAdapter,
                getParameters().getRaw().get(0).equals("localhost") ? getParameters().getRaw().get(0) : "localhost",
                Integer.parseInt(getParameters().getRaw().get(1)) == 5108 ? Integer.parseInt(getParameters().getRaw().get(1)) : 5108);

        new Thread(playerClient::run).start();
    }
}
