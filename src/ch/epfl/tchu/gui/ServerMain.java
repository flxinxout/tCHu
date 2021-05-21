package ch.epfl.tchu.gui;

import ch.epfl.tchu.Preconditions;
import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;
import ch.epfl.tchu.net.RemotePlayerProxy;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Programme principal du serveur tCHu.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class ServerMain extends Application {

    private static final int DEFAULT_PORT = 5108;
    private static final int DEFAULT_PLAYER_NUMBER = 2;
    private static final List<String> DEFAULT_NAMES = List.of("Ada", "Charles", "Odor", "Renée");


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

        int playerCount = args.isEmpty() ? DEFAULT_PLAYER_NUMBER : Integer.parseInt(args.get(0));
        Preconditions.checkArgument(playerCount >= 2);
        List<PlayerId> playerIds = PlayerId.ALL.subList(0, playerCount);

        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
        for (int i = 0; i < playerCount; i++) {
            PlayerId id = playerIds.get(i);
            playerNames.put(id, args.size() < (i + 2) ? DEFAULT_NAMES.get(i) : args.get(i + 1));
        }

        List<Player> playersProxy = new ArrayList<>();
        for (int i = 0; i < playerCount - 1; i++) {
            Socket socket;
            try (ServerSocket s0 = new ServerSocket(DEFAULT_PORT)) {
                socket = s0.accept();
            }
            playersProxy.add(new RemotePlayerProxy(socket));
        }

        SortedBag<Ticket> tickets = SortedBag.of(ChMap.tickets());

        Map<PlayerId, Player> players = new HashMap<>();
        players.put(PlayerId.PLAYER_1, new GraphicalPlayerAdapter());
        for (int i = 1; i < playerCount; i++)
            players.put(playerIds.get(i), playersProxy.get(i-1));

        new Thread(() -> Game.play(players, playerNames, tickets, new Random())).start();
    }
}
