/**
 * A naive strategy for a player to pick their decision based
 * solely off of the hard and soft values of their hand.
 */
public class NaiveStrategy implements IPlayerStrategy {

    /**
     * The singleton instance of this class, since only one instance
     * is ever needed.
     */
    private static final NaiveStrategy SINGLETON = new NaiveStrategy();

    /**
     * A private constructor to prevent users of this class from
     * creating additional instances of the class.
     */
    private NaiveStrategy() {

    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static NaiveStrategy singleton() {
        return SINGLETON;
    }

    /**
     * Returns a Decision for the player to make based on
     * the hard and soft values of their hand.
     *
     * @param table holds the deck, player hands, and dealer hand
     * @param handInPlay the hand being played
     * @return a decision for the player to make with their hand
     */
    @Override
    public Decision getDecision(Table table, PlayerHand handInPlay) {
        if (handInPlay.getHard() > 11 || handInPlay.getSoft() > 17) {
            return Decision.STAY;
        } else {
            return Decision.HIT;
        }
    }

    @Override
    public String toString() {
        return "Naive";
    }

}