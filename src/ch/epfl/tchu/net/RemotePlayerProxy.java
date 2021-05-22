package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.*;

import java.io.*;
import java.net.Socket;
import java.util.*;

import static ch.epfl.tchu.net.MessageId.*;
import static ch.epfl.tchu.net.Serdes.*;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Un mandataire (proxy) de joueur distant.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public final class RemotePlayerProxy implements Player {

    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private int playerNb = 2;

    /**
     * Construit un mandataire du joueur distant en fonction de la prise ({@code Socket}),
     * qu'il utilise pour communiquer à travers le réseau avec le client par échange de messages textuels.
     *
     * @param socket la prise utilisée pour communiquer à travers le réseau
     * @throws UncheckedIOException en cas d'erreur d'entrée/sortie
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
     * Écrit un message sur la prise de ce mandataire, en fonction de son identité
     * et les chaînes de caractères correspondants à la sérialisation de ses arguments données.
     *
     * @param identity l'identité du message
     * @param argSer   la sérialisation de ses arguments
     * @throws UncheckedIOException en cas d'erreur d'entrée/sortie
     * @see MessageId
     */
    private void writeMessage(MessageId identity, String... argSer) {
        try {
            StringJoiner sj = new StringJoiner(" ");
            sj.add(identity.name());
            Arrays.stream(argSer).forEach(sj::add);

            bufferedWriter.write(sj.toString());
            bufferedWriter.write('\n');
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Lit le message en attente sur la prise de ce mandataire.
     *
     * @return le message lu
     * @throws UncheckedIOException en cas d'erreur d'entrée/sortie
     */
    private String readMessage() {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Écrit, sur la prise de ce mandataire, un message communiquant l'identité de ce joueur
     * et les noms des différents joueurs.
     *
     * @param ownId       l'identité de ce joueur
     * @param playerNames le nom des différents joueurs associé à leur identité
     */
    @Override
    public void initPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String playerNamesSer = OF_LIST_OF_STRINGS.serialize(List.copyOf(playerNames.values()));

        playerNb = playerNames.size();

        writeMessage(INIT_PLAYERS,
                OF_INTEGER.serialize(playerNames.size()),
                OF_PLAYER_ID.serialize(ownId),
                playerNamesSer);
    }

    /**
     * Écrit, sur la prise de ce mandataire, un message communiquant une information sur la partie en cours.
     *
     * @param info l'information communiquée
     */
    @Override
    public void receiveInfo(String info) {
        writeMessage(RECEIVE_INFO, OF_STRING.serialize(info));
    }

    /**
     * Écrit, sur la prise de ce mandataire,
     * un message communiquant le nouvel état public de la partie ainsi que le nouvel état de ce joueur.
     *
     * @param newState le nouvel état public de la partie
     * @param ownState le nouvel état de ce joueur
     */
    @Override
    public void updateState(PublicGameState newState, PlayerState ownState) {
        writeMessage(UPDATE_STATE, OF_PUBLIC_GAME_STATE.serialize(newState), ofPlayerState(playerNb).serialize(ownState));
    }

    /**
     * Écrit, sur la prise de ce mandataire,
     * un message communiquant les billets initialement tirés par ce joueur en début de partie.
     *
     * @param tickets les billets initialement tirés par le joueur
     */
    @Override
    public void setInitialTicketChoice(SortedBag<Ticket> tickets) {
        writeMessage(SET_INITIAL_TICKETS, OF_SORTED_BAG_OF_TICKETS.serialize(tickets));
    }

    /**
     * Demande, sur la prise de ce mandataire, au client de communiquer les billets gardés par ce joueur parmi ceux
     * qu'il a tirés en début de partie. Son choix de billets est déterminé en lisant le dernier message sur la prise.
     *
     * @return les billets gardés par ce joueur parmi ceux qu'il a tirés en début de partie
     */
    @Override
    public SortedBag<Ticket> chooseInitialTickets() {
        writeMessage(CHOOSE_INITIAL_TICKETS);
        return OF_SORTED_BAG_OF_TICKETS.deserialize(readMessage());
    }

    /**
     * Demande, sur la prise de ce mandataire, au client de communiquer l'action que le joueur veut effectuer durant son
     * tour. Cette action est déterminée en lisant le dernier message sur la prise.
     *
     * @return l'action que le joueur veut effectuer durant son tour
     */
    @Override
    public TurnKind nextTurn() {
        writeMessage(NEXT_TURN);
        return OF_TURN_KIND.deserialize(readMessage());
    }

    /**
     * Écrit, sur la prise de ce mandataire, un message communiquant les billets tirés par le joueur en cours de partie
     * et demande au client de communiquer les billets gardés parmi ceux-ci. Ces billets sont déterminés en lisant
     * le dernier message sur la prise.
     *
     * @param options les billets tirés
     * @return les billets gardés par le joueur
     */
    @Override
    public SortedBag<Ticket> chooseTickets(SortedBag<Ticket> options) {
        writeMessage(CHOOSE_TICKETS, OF_SORTED_BAG_OF_TICKETS.serialize(options));
        return OF_SORTED_BAG_OF_TICKETS.deserialize(readMessage());
    }

    /**
     * Demande, sur la prise de ce mandataire, au client de communiquer l'emplacement de la carte tirée par ce joueur.
     * Cet emplacement est déterminé en lisant le dernier message sur la prise.
     *
     * @return entre 0 et 4 inclus si ce joueur tire des cartes faces visibles ou -1 s'il tire une carte de la pioche
     */
    @Override
    public int drawSlot() {
        writeMessage(DRAW_SLOT);
        return OF_INTEGER.deserialize(readMessage());
    }

    /**
     * Demande, sur la prise de ce mandataire, au client de communiquer la route dont ce joueur tente de s'emparer.
     * Cette route est déterminée en lisant le dernier message sur la prise.
     *
     * @return la route dont le joueur tente de s'emparer
     */
    @Override
    public Route claimedRoute() {
        writeMessage(ROUTE);
        return OF_ROUTE.deserialize(readMessage());
    }

    /**
     * Demande, sur la prise de ce mandataire, au client de communiquer les cartes initiales utilisées par ce joueur
     * pour tenter de s'emparer d'une route. Ces cartes sont déterminées en lisant le dernier message sur la prise.
     *
     * @return le multi-ensemble de cartes que le joueur utilise initialement pour s'emparer d'une route
     */
    @Override
    public SortedBag<Card> initialClaimCards() {
        writeMessage(CARDS);
        return OF_SORTED_BAG_OF_CARD.deserialize(readMessage());
    }

    /**
     * Écrit, sur la prise de ce mandataire, un message communiquant les possibilités de cartes additionnelles que le
     * joueur peut utiliser pour s'emparer d'un tunnel et demande au client de communiquer le multi-ensemble de cartes
     * additionnelles que le joueur a effectivement choisi parmi ces options. Ce multi-ensemble est déterminé en lisant
     * le dernier message sur la prise.
     *
     * @param options les possibilités de multi-ensembles de cartes pour s'emparer du tunnel
     * @return les cartes additionnelles utilisées par ce joueur
     */
    @Override
    public SortedBag<Card> chooseAdditionalCards(List<SortedBag<Card>> options) {
        writeMessage(CHOOSE_ADDITIONAL_CARDS, OF_LIST_OF_SORTED_BAGS_OF_CARDS.serialize(options));
        return OF_SORTED_BAG_OF_CARD.deserialize(readMessage());
    }
}
