package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static ch.epfl.tchu.game.Constants.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Un client de joueur distant.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class RemotePlayerClient {

    private final Player player;
    private final String hostName;
    private final int port;
    private int playerCount = MINIMUM_NUMBER_PLAYERS;

    /**
     * Construit le client du joueur donné, auquel il doit fournir un accès distant à l'aide
     * du nom d'hôte et du port d'écoute à utiliser pour se connecter au mandataire.
     *
     * @param player   le joueur auquel le client fourni un accès distant
     * @param hostName le nom d'hôte
     * @param port     le port d'écoute
     * @throws UncheckedIOException en cas d'erreur d'entrée/sortie
     */
    public RemotePlayerClient(Player player, String hostName, int port) {
        this.player = player;
        this.hostName = hostName;
        this.port = port;
    }

    /**
     * Permet au client d'écouter, durant la partie entière, sur le port et le nom d'hôte donnés.
     * Quand un message peut être lu sur ce port, il est récupéré et désérialisé en fonction de son type.
     * Après leur désérialisation, les arguments sont utilisés pour appeler la méthode associée au type du message.
     * Si cette méthode retourne un résultat, il est sérialisé et renvoyer au mandataire en réponse.
     */
    public void run() {
        try (Socket socket = new Socket(hostName, port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII))) {

            String str;
            while ((str = reader.readLine()) != null) {
                String[] message = str.split(Pattern.quote(" "), -1);

                switch (MessageId.valueOf(message[0])) {
                    case INIT_PLAYERS:
                        playerCount = Serdes.OF_INTEGER.deserialize(message[1]);
                        PlayerId id = Serdes.OF_PLAYER_ID.deserialize(message[2]);
                        List<String> names = Serdes.OF_LIST_OF_STRINGS.deserialize(message[3]);
                        Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
                        for (int i = 0; i < playerCount; i++) {
                            playerNames.put(PlayerId.ALL.get(i), names.get(i));
                        }

                        player.initPlayers(id, playerNames);
                        break;

                    case RECEIVE_INFO:
                        player.receiveInfo(Serdes.OF_STRING.deserialize(message[1]));
                        break;

                    case UPDATE_STATE:
                        int initialCarCount = INITIAL_CAR_COUNT - 10 * playerCount;
                        player.updateState(Serdes.OF_PUBLIC_GAME_STATE.deserialize(message[1]),
                                Serdes.ofPlayerState(initialCarCount).deserialize(message[2]));
                        break;

                    case SET_INITIAL_TICKETS:
                        player.setInitialTicketChoice(Serdes.OF_SORTED_BAG_OF_TICKETS.deserialize(message[1]));
                        break;

                    case CHOOSE_INITIAL_TICKETS:
                        writer.write(Serdes.OF_SORTED_BAG_OF_TICKETS.serialize(player.chooseInitialTickets()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case NEXT_TURN:
                        writer.write(Serdes.OF_TURN_KIND.serialize(player.nextTurn()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case CHOOSE_TICKETS:
                        SortedBag<Ticket> chosenTickets = player
                                .chooseTickets(Serdes.OF_SORTED_BAG_OF_TICKETS.deserialize(message[1]));
                        writer.write(Serdes.OF_SORTED_BAG_OF_TICKETS.serialize(chosenTickets));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case DRAW_SLOT:
                        writer.write(Serdes.OF_INTEGER.serialize(player.drawSlot()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case ROUTE:
                        writer.write(Serdes.OF_ROUTE.serialize(player.claimedRoute()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case CARDS:
                        writer.write(Serdes.OF_SORTED_BAG_OF_CARD.serialize(player.initialClaimCards()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case CHOOSE_ADDITIONAL_CARDS:
                        SortedBag<Card> chosenCards = player
                                .chooseAdditionalCards(Serdes.OF_LIST_OF_SORTED_BAGS_OF_CARDS.deserialize(message[1]));
                        writer.write(Serdes.OF_SORTED_BAG_OF_CARD.serialize(chosenCards));
                        writer.write('\n');
                        writer.flush();
                        break;

                    default:
                        throw new Error("Type de message (MessageId) non reconnu: " + MessageId.valueOf(message[0]));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
