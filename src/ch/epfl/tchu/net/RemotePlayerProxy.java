package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Représente un mandataire (proxy) de joueur distant.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 * @implNote Toutes les méthodes ré-implémentées de l'interface Player sont utilisées pour communiquer des informations
 * de la partie entre les joueurs
 */
public final class RemotePlayerProxy implements Player {

    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    /**
     * Construit un mandataire (proxy) du joueur distant en fonction d'une la prise (socket), qu'il
     * utilise pour communiquer à travers le réseau avec le client par échange de messages textuels.
     *
     * @param socket la prise (socket) que le mandataire (proxy) utilise
     */
    public RemotePlayerProxy(Socket socket) {
        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), US_ASCII));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), US_ASCII));

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Envoie un message sur la prise de ce mandataire, étant donné son identité
     * et les chaînes de caractères correspondants à la sérialisation de ses arguments.
     *
     * @param identity l'identité du message
     * @param argSers  les séralisations de ses arguments
     * @see MessageId
     */
    private void sendMessage(String identity, String... argSers) {
        try {
            StringJoiner sj = new StringJoiner(" ");
            sj.add(identity);
            Arrays.stream(argSers).forEach(sj::add);

            bufferedWriter.write(sj.toString());
            bufferedWriter.write('\n');
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Reçoit le message en attente sur la prise de ce mandataire.
     *
     * @return le message reçu
     */
    private String readMessage() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Communique les informations sur l'identité du joueur ainsi que le nom des différents joueurs.
     *
     * @param ownId       l'identité du joueur
     * @param playerNames le nom des différents joueurs
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String ownIdSer = Serdes.OF_PLAYER_ID.serialize(ownId);
        String playerNamesSer = Serdes.OF_LIST_OF_STRINGS.serialize(List.of(playerNames.get(PlayerId.PLAYER_1),
                playerNames.get(PlayerId.PLAYER_2)));

        sendMessage(MessageId.INIT_PLAYERS.name(), ownIdSer, playerNamesSer);
    }

    /**
     * Communique une information sur le cours de la partie.
     *
     * @param info l'information qui doit être communiquée
     */
    @Override
    public void receiveInfo(String info) {
        String infoSer = Serdes.OF_STRINGS.serialize(info);
        sendMessage(MessageId.RECEIVE_INFO.name(), infoSer);
    }

    /**
     * Communique le nouvel état de la partie ainsi que le nouvel état spécifique à un joueur.
     *
     * @param newState le nouvel état de la partie
     * @param ownState l'état du joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        String newStateSer = Serdes.OF_PUBLIC_GAME_STATE.serialize(newState);
        String ownStateSer = Serdes.OF_PLAYER_STATE.serialize(ownState);
        sendMessage(MessageId.UPDATE_STATE.name(), newStateSer, ownStateSer);
    }

    /**
     * Communique les billets qu'un joueur à initialement reçus.
     *
     * @param tickets les billets distribués au joueur
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        String ticketsSer = Serdes.OF_SORTEDBAG_OF_TICKETS.serialize(tickets);
        sendMessage(MessageId.SET_INITIAL_TICKETS.name(), ticketsSer);
    }

    /**
     * Communique l'information sur lesquels des billets le joueur à initialement gardés.
     *
     * @return les billets que le joueur à initialement gardés
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        sendMessage(MessageId.CHOOSE_INITIAL_TICKETS.name());

        return Serdes.OF_SORTEDBAG_OF_TICKETS.deserialize(readMessage());
    }

    /**
     * Communique le type de tour de jeu que le joueur veut effectuer.
     *
     * @return e type de tour de jeu que le joueur veut effectuer
     */
    @Override
    public TurnKind nextTurn() {
        sendMessage(MessageId.NEXT_TURN.name());

        return Serdes.OF_TURN_KIND.deserialize(readMessage());
    }

    /**
     * Communique les billets tirés par un joueur ainsi que ceux qu'ils gardent.
     *
     * @param options les billets tirés
     * @return les billets gardés par le joueur
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        String optionsSer = Serdes.OF_SORTEDBAG_OF_TICKETS.serialize(options);
        sendMessage(MessageId.CHOOSE_TICKETS.name(), optionsSer);

        return Serdes.OF_SORTEDBAG_OF_TICKETS.deserialize(readMessage());
    }

    /**
     * Communique l'information sur l'emplacement d'une carte tirée par le joueur. Cela peut provenir des cartes faces
     * visibles ou de la pioche.
     *
     * @return entre 0 et 4 inclus:= si ce joueur tire des cartes faces visibles / Constants.DECK_SLOT (-1) s'il tire
     * une carte de la pioche
     */
    @Override
    public int drawSlot() {
        sendMessage(MessageId.DRAW_SLOT.name());

        return Serdes.OF_INTEGERS.deserialize(readMessage());
    }

    /**
     * Communique la route que le joueur tente de s'emparer.
     *
     * @return la route que le joueur tente de s'emparer
     */
    @Override
    public Route claimedRoute() {
        sendMessage(MessageId.ROUTE.name());

        return Serdes.OF_ROUTES.deserialize(readMessage());
    }

    /**
     * Communique les cartes que le joueur veut initialement utiliser pour s'emparer d'une route.
     *
     * @return les cartes que le joueur veut initialement utiliser pour s'emparer d'une route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        sendMessage(MessageId.CARDS.name());

        return Serdes.OF_SORTEDBAG_OF_CARD.deserialize(readMessage());
    }

    /**
     * Communique l'information que le joueur tente de s'emparer d'un tunnel et qu'il doit utiliser des cartes
     * additionnelles, tout en sachant que les possibilités de cartes à employer sont passées en argument.
     *
     * @param options les possibilités de cartes pour s'emparer du tunnel
     * @return les cartes additionnelles jouées par ce joueur
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        String optionsSer = Serdes.OF_LIST_OF_SORTEDBAG_OF_CARDS.serialize(options);
        sendMessage(MessageId.CHOOSE_ADDITIONAL_CARDS.name(), optionsSer);

        return Serdes.OF_SORTEDBAG_OF_CARD.deserialize(readMessage());
    }
}
