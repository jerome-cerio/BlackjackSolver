/**
 * Represents a dealer's hand which currently has no
 * more capabilities than AHand.
 */
public class DealerHand extends AHand {

    /**
     * Constructs a dealer hand from any number of input cards.
     *
     * @param cards any number of cards
     */
    public DealerHand(Card... cards) {
        super(cards);
    }

    /**
     * Constructs each card from a line of csv.
     *
     * @param csvLine a line of csv
     */
    public DealerHand(String csvLine) {
        super(csvLine);
    }

    /**
     * Returns a deep copy of this hand. The return type
     * is overridden to be DealerHand instead of AHand.
     *
     * @return a deep copy of this hand
     */
    @Override
    public DealerHand copy() {
        DealerHand copyHand = new DealerHand();
        this.copyCardsInto(copyHand);
        if (this.isFinal()) {
            copyHand.markFinal();
        }
        return copyHand;
    }

}
