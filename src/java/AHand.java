import java.util.ArrayList;
import java.util.List;

/**
 * This abstract class holds the shared capabilities
 * of player hands and dealer hands.
 */
public abstract class AHand {

    /**
     * The list of cards contained in the hand.
     */
    private final List<Card> cards;

    /**
     * Indicates whether the hand is final, meaning it is done being played.
     */
    private boolean isFinal;

    /**
     * Constructs a hand from any number of input cards.
     *
     * @param cards any number of cards
     */
    protected AHand(Card... cards) {
        this.cards = new ArrayList<>();
        this.cards.addAll(List.of(cards));
        this.isFinal = false;
    }

    /**
     * Constructs each card from a line of csv.
     *
     * @param csvLine a line of csv
     */
    protected AHand(String csvLine) {
        this.cards = new ArrayList<>(2);
        for (String cardUnicode : csvLine.split(",")) {
            // If the card unicode is the empty string, there is no card to add.
            if (!cardUnicode.isEmpty()) {
                this.cards.add(new Card(cardUnicode));
            }
        }
        this.isFinal = false;
    }

    /**
     * Returns a deep copy of this hand. Each concrete
     * implementation of this method should return the
     * specific type of the class in which the implementation
     * resides.
     *
     * @return a deep copy of this hand
     */
    public abstract AHand copy();

    /**
     * Copies each card in this hand into the other hand.
     * This hand is not mutated in any way since deep copies
     * are created.
     *
     * @param other another hand
     */
    protected void copyCardsInto(AHand other) {
        for (Card card: this.cards) {
            other.cards.add(card.copy());
        }
    }

    /**
     * Returns the list of cards in this hand.
     *
     * @return the list of cards in this hand
     */
    public List<Card> getCards() {
        return this.cards;
    }

    /**
     * Returns the number of cards in this hand.
     *
     * @return the number of cards in this hand
     */
    public int handSize() {
        return this.cards.size();
    }

    /**
     * Adds the input card to this hand.
     *
     * @param card a card to add to the hand
     */
    public void addCard(Card card) {
        this.cards.add(card);
    }

    /**
     * Returns the hard value of the hand, where all aces are
     * counted as having a value of 1.
     *
     * @return an integer representing the hard value of the hand
     */
    public int getHard() {
        int total = 0;
        for (Card card: this.cards) {      //iterate through cards in hand
            total += card.hardValue();
        }
        return total;
    }

    /**
     * Returns the soft value of the hand, where if the hand has at
     * least one ace, then one of the aces is counted as 11, and the
     * other aces are counted as 1.
     *
     * @return an integer representing the soft value of a hand
     */
    public int getSoft() {
        // Adds ten to the total if the hand contains at least one ace.
        return this.getHard() + (this.containsAce() ? 10 : 0);
    }

    /**
     * Returns the best way to interpret the hand's value,
     * which is the maximum of the hard value and the soft
     * value of the hand not exceeding 21.
     *
     * @return the best way to interpret the hand's value
     */
    public int bestValue() {
        int hardValue = this.getHard();
        int softValue = this.getSoft();
        int maxValue = Math.max(hardValue, softValue);
        if (maxValue <= 21) {
            return maxValue;
        } else {
            return Math.min(hardValue, softValue);
        }
    }

    /**
     * Returns whether this hand is soft, which is true
     * iff it contains an ace and has a valid soft value (<=21).
     *
     * @return true iff this hand is soft
     */
    public boolean isSoft() {
        return (this.containsAce() && getSoft() <= 21);
    }

    /**
     * Returns whether this hand is a pair, which is true
     * iff it consists of exactly two cards with identical ranks.
     *
     * @return true iff this hand is a pair
     */
    public boolean isPair() {
        return (this.handSize() == 2 &&
                this.cards.get(0).getRank() == this.cards.get(1).getRank());
    }

    /**
     * Returns whether this hand is a Blackjack, which is true
     * iff it consists of exactly two cards with a soft total of 21.
     *
     * @return true iff this hand is a Blackjack
     */
    public boolean isBlackJack() {
        return (this.handSize() == 2 && this.getSoft() == 21);
    }

    /**
     * Returns whether this hand's hard value is above 21.
     *
     * @return true iff this hand's hard value is above 21
     */
    public boolean isBust() {
        return this.getHard() > 21;
    }

    /**
     * Returns true iff the hand contains at least one ace.
     *
     * @return true iff the hand contains at least one ace
     */
    private boolean containsAce() {
        for (Card card : this.cards) {
            if (card.isAce()) {
                return true;
            }
        }
        return false;
    }

    public boolean isFinal() {
        return this.isFinal;
    }

    public void markFinal() {
        this.isFinal = true;
    }

    @Override
    public String toString() {
        return this.cards.toString();
    }

}
