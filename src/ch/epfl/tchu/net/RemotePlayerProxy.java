package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static java.nio.charset.StandardCharsets.US_ASCII;

public class RemotePlayerProxy implements Player {

    private final Socket socket;

    public RemotePlayerProxy(Socket socket){
        this.socket = socket;
    }

    private void sendMessage(String identity, String... argSers){
        try (BufferedWriter w = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII))){
            StringJoiner sj = new StringJoiner(" ");
            sj.add(identity);
            Arrays.stream(argSers).forEach(sj::add);

            w.write(sj.toString());
            w.write('\n');
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String readMessage(){
        try (BufferedReader r = new BufferedReader(new InputStreamReader(socket.getInputStream(),US_ASCII))){
            return r.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String ownIdSer = Serdes.OF_PLAYER_ID.serialize(ownId);
        String playerNamesSer = Serdes.OF_LIST_OF_STRINGS.serialize(List.of(playerNames.get(PlayerId.PLAYER_1),
                playerNames.get(PlayerId.PLAYER_2)));

        sendMessage(MessageId.INIT_PLAYERS.toString(), ownIdSer, playerNamesSer);
    }

    @Override
    public void receiveInfo(String info) {
        String infoSer = Serdes.OF_STRINGS.serialize(info);
        sendMessage(MessageId.RECEIVE_INFO.toString(), infoSer);
    }

    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String newStateSer = Serdes.OF_PUBLIC_GAME_STATE.serialize(newState);
        String ownStateSer = Serdes.OF_PLAYER_STATE.serialize(ownState);
        sendMessage(MessageId.UPDATE_STATE.toString(), newStateSer, ownStateSer);
    }

    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String ticketsSer = Serdes.OF_SORTEDBAG_OF_TICKETS.serialize(tickets);
        sendMessage(MessageId.SET_INITIAL_TICKETS.toString(), ticketsSer);
    }

    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS.toString());

        return Serdes.OF_SORTEDBAG_OF_TICKETS.deserialize(readMessage());
    }

    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN.toString());

        return Serdes.OF_TURN_KIND.deserialize(readMessage());
    }

    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String optionsSer = Serdes.OF_SORTEDBAG_OF_TICKETS.serialize(options);
        sendMessage(MessageId.CHOOSE_TICKETS.toString(), optionsSer);

        return Serdes.OF_SORTEDBAG_OF_TICKETS.deserialize(readMessage());
    }

    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT.toString());

        return Serdes.OF_INTEGERS.deserialize(readMessage());
    }

    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE.toString());

        return Serdes.OF_ROUTES.deserialize(readMessage());
    }

    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS.toString());

        return Serdes.OF_SORTEDBAG_OF_CARD.deserialize(readMessage());
    }

    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String optionsSer = Serdes.OF_LIST_OF_SORTEDBAG_OF_CARDS.serialize(options);
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS.toString(), optionsSer);

        return Serdes.OF_SORTEDBAG_OF_CARD.deserialize(readMessage());
    }
}
