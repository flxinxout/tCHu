package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Random;

/**
 * Programme principal du serveur tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class ServerMain extends Application {
    public static void main(String[] args) { launch(args); }

    /**
     * Analyse les arguments passés au programme afin de déterminer les noms des deux joueurs, attend une connexion
     * de la part du client sur le port 5108, crée les deux joueurs, le premier étant un joueur graphique, le second
     * un mandataire du joueur distant qui se trouve sur le client et démarre le fil d'exécution gérant la partie.
     *
     * @param primaryStage le stage principal
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(5108);
             Socket socket = serverSocket.accept()) {

            Player playerProxy = new RemotePlayerProxy(socket);
            SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

            Map<PlayerId, Player> players = Map.of(
                    PlayerId.PLAYER_1,
                    new GraphicalPlayerAdapter(),
                    PlayerId.PLAYER_2,
                    playerProxy);

            Map<PlayerId, String> playersName = Map.of(
                    PlayerId.PLAYER_1,
                    getParameters().getRaw().get(0).equals("Ada") ? getParameters().getRaw().get(0) : "Ada",
                    PlayerId.PLAYER_2,
                    getParameters().getRaw().get(0).equals("Charles") ? getParameters().getRaw().get(0) : "Charles");

            new Thread(() -> Game.play(players, playersName, tickets, new Random())).start();
        }
    }
}
