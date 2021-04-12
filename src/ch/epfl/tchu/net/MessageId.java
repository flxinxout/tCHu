package ch.epfl.tchu.net;

/**
 * Types de messages que le serveur peut envoyer aux clients.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 */
public enum MessageId {

    INIT_PLAYERS,
    RECEIVE_INFO,
    UPDATE_STATE,
    SET_INITIAL_TICKETS,
    CHOOSE_INITIAL_TICKETS,
    NEXT_TURN,
    CHOOSE_TICKETS,
    DRAW_SLOT,
    ROUTE,
    CARDS,
    CHOOSE_ADDITIONAL_CARDS



}
