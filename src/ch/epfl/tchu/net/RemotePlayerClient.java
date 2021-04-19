package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Player;
import ch.epfl.tchu.game.PlayerId;
import ch.epfl.tchu.game.Ticket;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerClient {
    private final Player player;
    private final String name;
    private final int port;

    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.name = name;
        this.port = port;
    }

    public void run(){
        try (Socket s = new Socket(name, port);
             BufferedReader r = new BufferedReader(new InputStreamReader(s.getInputStream(), US_ASCII));
             BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), US_ASCII))) {

            String[] message = r.readLine().split(Pattern.quote(" "), -1);

            switch(MessageId.valueOf(message[0])){
                case INIT_PLAYERS:
                    PlayerId id = Serdes.OF_PLAYER_ID.deserialize(message[1]);
                    List<String> names = Serdes.OF_LIST_OF_STRINGS.deserialize(message[2]);
                    Map<PlayerId, String> playerNames = Map.of(PlayerId.PLAYER_1, names.get(0),
                            PlayerId.PLAYER_2, names.get(1));

                    player.initPlayers(id, playerNames);
                    break;

                case RECEIVE_INFO:
                    player.receiveInfo(Serdes.OF_STRINGS.deserialize(message[1]));
                    break;

                case UPDATE_STATE:
                    player.updateState(Serdes.OF_PUBLIC_GAME_STATE.deserialize(message[1]),
                            Serdes.OF_PLAYER_STATE.deserialize(message[2]));
                    break;

                case SET_INITIAL_TICKETS:
                    player.setInitialTicketChoice(Serdes.OF_SORTEDBAG_OF_TICKETS.deserialize(message[1]));
                    break;

                case CHOOSE_INITIAL_TICKETS:
                    w.write(Serdes.OF_SORTEDBAG_OF_TICKETS.serialize(player.chooseInitialTickets()));
                    break;

                case NEXT_TURN:
                    w.write(Serdes.OF_TURN_KIND.serialize(player.nextTurn()));
                    break;

                case CHOOSE_TICKETS:
                    SortedBag<Ticket> chosenTickets = player
                            .chooseTickets(Serdes.OF_SORTEDBAG_OF_TICKETS.deserialize(message[1]));
                    w.write(Serdes.OF_SORTEDBAG_OF_TICKETS.serialize(chosenTickets));
                    break;

                case DRAW_SLOT:
                    w.write(Serdes.OF_INTEGERS.serialize(player.drawSlot()));
                    break;

                case ROUTE:
                    w.write(Serdes.OF_ROUTES.serialize(player.claimedRoute()));
                    break;

                case CARDS:
                    w.write(Serdes.OF_SORTEDBAG_OF_CARD.serialize(player.initialClaimCards()));
                    break;

                case CHOOSE_ADDITIONAL_CARDS:
                    SortedBag<Card> chosenCards = player
                            .chooseAdditionalCards(Serdes.OF_LIST_OF_SORTEDBAG_OF_CARDS.deserialize(message[1]));
                    w.write(Serdes.OF_SORTEDBAG_OF_CARD.serialize(chosenCards));
                    break;

                default:
                    break;
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
