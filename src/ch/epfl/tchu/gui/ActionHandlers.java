package ch.epfl.tchu.gui;

import ch.epfl.tchu.SortedBag;
import ch.epfl.tchu.game.Card;
import ch.epfl.tchu.game.Route;
import ch.epfl.tchu.game.Ticket;

/**
 * Contient cinq interfaces fonctionnelles imbriquées représentant différents gestionnaires d'actions.
 *
 * @author Dylan Vairoli (326603)
 * @author Giovanni Ranieri (326870)
 *
 * @implNote Chaque inteface est un morceau de code à exécuter lorsque le joueur effectue une action.
 * Par exemple, quand c'est à lui de jouer, un joueur peut effectuer trois actions différentes: tirer des billets,
 * tirer des cartes, ou (tenter de) s'emparer d'une route. À chacune de ces trois actions correspond donc
 * un gestionnaire d'action décrit ci-dessous.
 */
public interface ActionHandlers {

    /**
     * Gestionnaire de l'action de tirer des billets.
     */
    @FunctionalInterface
    interface DrawTicketsHandler {
        /**
         * Est appelée lorsque le joueur désire tirer des billets.
         */
        void onDrawTickets();
    }

    /**
     * Gestionnaire de l'action de tirer des cartes.
     */
    @FunctionalInterface
    interface DrawCardHandler {
        /**
         * Est appelée lorsque le joueur désire tirer une carte de l'emplacement donné.
         *
         * @param slot l'emplacement duquel tirer une carte
         */
        void onDrawCard(int slot);
    }

    /**
     * Gestionnaire de l'action de s'emparer d'une route.
     */
    @FunctionalInterface
    interface ClaimRouteHandler {
        /**
         * Est appelée lorsque le joueur désire s'emparer de la route donnée au moyen des cartes (initiales) données.
         *
         * @param route la route dont le joueur désire s'emparer
         * @param cards les cartes (initiales) que le joueur utilise pour s'emparer de la route
         */
        void onClaimRoute(Route route, SortedBag<Card> cards);
    }

    /**
     * Gestionnaire de l'action de choisir des billets parmi un tirage.
     */
    @FunctionalInterface
    interface ChooseTicketsHandler {
        /**
         * Est appelée lorsque le joueur a choisi de garder les billets donnés suite à un tirage de billets.
         *
         * @param tickets le tirage de billets duquel le joueur doit en choisir
         */
        void onChooseTickets(SortedBag<Ticket> tickets);
    }

    /**
     * Gestionnaire de l'action d'utiliser des cartes pour s'emparer d'une route.
     */
    @FunctionalInterface
    interface ChooseCardsHandler {
        /**
         * Est appelée lorsque le joueur a choisi d'utiliser les cartes données comme cartes initiales ou additionnelles
         * lors de la prise de possession d'une route; s'il s'agit de cartes additionnelles,
         * alors le multiensemble peut être vide, ce qui signifie que le joueur renonce à s'emparer du tunnel.
         *
         * @param cards les cartes initiales ou additionnelles utilisées pour s'emparer d'une route
         */
        void onChooseCards(SortedBag<Card> cards);
    }
}
