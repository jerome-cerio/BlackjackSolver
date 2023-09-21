/**
 * A strategy for a player to pick their decision based on their
 * hand and the other cards on the table.
 */
public interface IPlayerStrategy {

    /**
     * Returns a Decision for the player to make based on their
     * hand and the other cards on the table.
     *
     * @param table holds the deck, player hands, and dealer hand
     * @param handInPlay the hand being played
     * @return a decision for the player to make with their hand
     */
    Decision getDecision(Table table, PlayerHand handInPlay);
}
