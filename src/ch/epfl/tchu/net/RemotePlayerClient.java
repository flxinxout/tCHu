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

/**
 * Représente un client de joueur distant.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public class RemotePlayerClient {

    private final Player player;
    private final String name;
    private final int port;

    /**
     * Construit un client de joueur distant basé sur le joueur auquel il doit fournir un accès distant,
     * ainsi que le nom d'hôte et le port d'écoute à utiliser pour se connecter au mandataire.
     *
     * @param player le joueur auquel le client fourni un accès distant
     * @param name   le nom d'hôte
     * @param port   le numéro du port d'écoute
     * @throws UncheckedIOException en cas d'erreur d'entrée/sortie
     */
    public RemotePlayerClient(Player player, String name, int port) {
        this.player = player;
        this.name = name;
        this.port = port;
    }

    /**
     * Permet au client d'écouter sur toute la durée de la partie sur le port donné du nom d'hôte donné.
     * Quand un message peut être lu sur ce port, il est récupéré et désérialisé en fonction de son type.
     * Après leur désérialisation, les arguments sont utilisés pour appeler la méthode associée au type du message.
     * Quand plus rien ne peut être lu sur le port en question, la connexion est fermée.
     */
    public void run() {
        try (Socket socket = new Socket(name, port);
             final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
             final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII))) {

            String str;
            while ((str = reader.readLine()) != null) {
                String[] message = str.split(Pattern.quote(" "), -1);

                switch (MessageId.valueOf(message[0])) {
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
                        writer.write(Serdes.OF_SORTEDBAG_OF_TICKETS.serialize(player.chooseInitialTickets()));
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
                                .chooseTickets(Serdes.OF_SORTEDBAG_OF_TICKETS.deserialize(message[1]));
                        writer.write(Serdes.OF_SORTEDBAG_OF_TICKETS.serialize(chosenTickets));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case DRAW_SLOT:
                        writer.write(Serdes.OF_INTEGERS.serialize(player.drawSlot()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case ROUTE:
                        writer.write(Serdes.OF_ROUTES.serialize(player.claimedRoute()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case CARDS:
                        writer.write(Serdes.OF_SORTEDBAG_OF_CARD.serialize(player.initialClaimCards()));
                        writer.write('\n');
                        writer.flush();
                        break;

                    case CHOOSE_ADDITIONAL_CARDS:
                        SortedBag<Card> chosenCards = player
                                .chooseAdditionalCards(Serdes.OF_LIST_OF_SORTEDBAG_OF_CARDS.deserialize(message[1]));
                        writer.write(Serdes.OF_SORTEDBAG_OF_CARD.serialize(chosenCards));
                        writer.write('\n');
                        writer.flush();
                        break;

                    default:
                        break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
