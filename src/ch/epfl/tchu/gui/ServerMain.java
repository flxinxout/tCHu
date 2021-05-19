package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Programme principal du serveur tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class ServerMain extends Application {

    private static final int DEFAULT_PORT = 5108;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Analyse les arguments passés au programme afin de déterminer les noms des deux joueurs. Attend une connexion
     * de la part du client sur le port passé en argument et crée les deux joueurs. Le premier étant un joueur graphique,
     * le second un mandataire du joueur distant qui se trouve sur le client. Démarre le fil d'exécution gérant la partie.
     *
     * @param primaryStage la scène principale
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        List<String> args = getParameters().getRaw();

        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        for (int i = 0; i < PlayerId.COUNT; i++) {
            PlayerId id = PlayerId.ALL.get(i);
            playerNames.put(id, args.size() < (i + 1) ? id.defaultName() : args.get(i));
        }

        //TODO adapt for three
        Socket socket;
        try (ServerSocket s0 = new ServerSocket(DEFAULT_PORT)) {
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
