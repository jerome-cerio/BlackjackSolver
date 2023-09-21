import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player's hand which has a bet
 * and can be surrendered.
 */
public class PlayerHand extends AHand {

    /**
     * The amount of money bet on this hand. Will
     * either be the initial amount, or double if
     * the player doubled on this hand.
     */
    private double bet;

    /**
     * Indicates whether the player surrendered this hand.
     */
    private boolean surrendered;

    /**
     * Constructs a player hand from any number of input cards,
     * initializing its bet and surrender flag.
     *
     * @param cards any number of cards
     */
    public PlayerHand(Card... cards) {
        super(cards);
        this.bet = 1;
        this.surrendered = false;
    }

    /**
     * Constructs each card from a line of csv, and
     * initializes the hand's bet and surrender flag.
     *
     * @param csvLine a line of csv
     */
    public PlayerHand(String csvLine) {
        super(csvLine);
        this.bet = 1;
        this.surrendered = false;
    }

    /**
     * Returns a deep copy of this hand. The return type
     * is overridden to be PlayerHand instead of AHand.
     *
     * @return a deep copy of this hand
     */
    @Override
    public PlayerHand copy() {
        PlayerHand copyHand = new PlayerHand();
        this.copyCardsInto(copyHand);
        if (this.isFinal()) {
            copyHand.markFinal();
        }
        copyHand.bet = this.bet;
        copyHand.surrendered = this.surrendered;
        return copyHand;
    }

    /**
     * Based on the current hand, this method returns which of the 5 possible
     * moves can be played next: HIT, STAY, DOUBLE, SPLIT, and SURRENDER.
     *
     * @return a List of Decision objects representing which moves can be played.
     */
    public List<Decision> getPossibleDecisions() {
        List<Decision> returnList = new ArrayList<>();
        returnList.add(Decision.HIT);
        returnList.add(Decision.STAY);
        returnList.add(Decision.DOUBLE);
        returnList.add(Decision.SPLIT);
        returnList.add(Decision.SURRENDER);

        if (this.getCards().size() > 2) {
            returnList.remove(Decision.SURRENDER);
            returnList.remove(Decision.DOUBLE);
            returnList.remove(Decision.SPLIT);
        }else{
            if(this.getHard() > 11){
                returnList.remove(Decision.DOUBLE);
            }
            if(this.getCards().get(0).getRank() != (this.getCards().get(1).getRank())){
                returnList.remove(Decision.SPLIT);
            }
        }
        return returnList;
    }

    /**
     * Returns the bet stored in this hand.
     *
     * @return the bet stored in this hand
     */
    public double getBet() {
        return this.bet;
    }

    /**
     * Doubles the bet stored in this hand.
     */
    public void doubleBet() {
        this.bet *= 2;
    }

    /**
     * Returns whether this hand was surrendered.
     *
     * @return true iff this hand was surrendered
     */
    public boolean surrendered() {
        return this.surrendered;
    }

    /**
     * Marks this hand as surrendered.
     */
    public void markSurrendered() {
        this.surrendered = true;
    }

    /**
     * Overridden toString() method for a PlayerHand object.
     *
     * @return a String representation of the player's hand and bet.
     */
    @Override
    public String toString() {
        return super.toString() /*+ " (bet " + this.bet + ")"*/;
    }

    /**
     * Overidden equals() method for a PlayerHand object.
     *
     * @param o Another object whose equality will be compared to the current PlayerHand.
     * @return True if o is an identical PlayerHand object, False otherwise.
     */
    @Override
    public boolean equals(Object o) {
        return (o instanceof PlayerHand otherHand) &&
            this.getCards().equals(otherHand.getCards());
    }

}
