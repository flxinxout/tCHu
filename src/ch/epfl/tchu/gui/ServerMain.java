package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.epfl.tchu.gui.StringsFr.DEFAULT_NAME_P1;
import static ch.epfl.tchu.gui.StringsFr.DEFAULT_NAME_P2;

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
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        List<String> args = getParameters().getRaw();

        Map<PlayerId, String> playerNames = Map.of(
                PlayerId.PLAYER_1,
                args.isEmpty() ? DEFAULT_NAME_P1 : args.get(0),
                PlayerId.PLAYER_2,
                args.size() < 2 ? DEFAULT_NAME_P2 : args.get(1));

        Socket socket;
        try (ServerSocket s0 = new ServerSocket(StringsFr.DEFAULT_PORT)) {
            socket = s0.accept();
        }
        Player playerProxy = new RemotePlayerProxy(socket);
        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        Map<PlayerId, Player> players = Map.of(
                PlayerId.PLAYER_1,
                new GraphicalPlayerAdapter(),
                PlayerId.PLAYER_2,
                playerProxy);

        new Thread(() -> Game.play(players, playerNames, tickets, new Random())).start();
    }
}
