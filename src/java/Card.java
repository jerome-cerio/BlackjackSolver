import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a single card from a deck of cards.
 */
public class Card {

    /**
     * Mapping of each rank to its respective hard value.
     */
    public static final Map<Rank, Integer> hardValues = Map.ofEntries(
            Map.entry(Rank.ACE, 1),
            Map.entry(Rank.TWO, 2),
            Map.entry(Rank.THREE, 3),
            Map.entry(Rank.FOUR, 4),
            Map.entry(Rank.FIVE, 5),
            Map.entry(Rank.SIX, 6),
            Map.entry(Rank.SEVEN, 7),
            Map.entry(Rank.EIGHT, 8),
            Map.entry(Rank.NINE, 9),
            Map.entry(Rank.TEN, 10),
            Map.entry(Rank.JACK, 10),
            Map.entry(Rank.QUEEN, 10),
            Map.entry(Rank.KING, 10));

    /**
     * Mapping of single-letter string representations 2-10, J, Q, K, A to their ranks.
     */
    public static final Map<String, Rank> string2RankMap = createString2RankMap();

    /**
     * The suit of the card
     */
    private final Suit suit;

    /**
     * The rank of the card
     */
    private final Rank rank;

    /**
     * Initializes the card's suit and rank.
     *
     * @param suit the card's suit
     * @param rank the card's rank
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Constructor to make a new card from its unicode value.
     *
     * @param unicode string holding the unicode value of the card
     *                to be created.
     */
    public Card(String unicode) {

        // Ensure uppercase letters are used for the map.
        unicode = unicode.toUpperCase();

        char suitIndicator = unicode.charAt(3);
        char rankIndicator = unicode.charAt(4);

        /*
         * These initial values of null are required since the
         * compiler can't guarantee the suit and rank will be
         * initialized in the switch statements below. If a card's
         * suit or rank ever are null, that indicates an error.
         */
        Suit suit = null;
        Rank rank = null;

        // Set the card's suit.
        switch (suitIndicator) {
            case 'A' -> suit = Suit.SPADES;
            case 'B' -> suit = Suit.HEARTS;
            case 'C' -> suit = Suit.DIAMONDS;
            case 'D' -> suit = Suit.CLUBS;
        }

        // Set the card's rank.
        switch (rankIndicator) {
            case '1' -> rank = Rank.ACE;
            case '2' -> rank = Rank.TWO;
            case '3' -> rank = Rank.THREE;
            case '4' -> rank = Rank.FOUR;
            case '5' -> rank = Rank.FIVE;
            case '6' -> rank = Rank.SIX;
            case '7' -> rank = Rank.SEVEN;
            case '8' -> rank = Rank.EIGHT;
            case '9' -> rank = Rank.NINE;
            case 'A' -> rank = Rank.TEN;
            case 'B' -> rank = Rank.JACK;
            case 'D' -> rank = Rank.QUEEN;
            case 'E' -> rank = Rank.KING;
        }

        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Makes a deep copy of this card.
     *
     * @return a deep copy of this card
     */
    public Card copy() {
        return new Card(this.suit, this.rank);
    }

    /**
     * Returns the suit of the card.
     *
     * @return a Suit type.
     */
    public Suit getSuit(){
        return this.suit;
    }

    /**
     * Returns the rank of the card.
     *
     * @return a Rank type.
     */
    public Rank getRank(){
        return this.rank;
    }

    /**
     * Returns the hard value of the card, according to its rank.
     *
     * @return an integer representing the hard value of the card.
     */
    public int hardValue() {
        return hardValues.get(this.rank);
    }

    /**
     * Returns true iff the card is an ace.
     *
     * @return true iff the card is an ace
     */
    public boolean isAce() {
        return (this.rank == Rank.ACE);
    }

    /**
     * Initializes the map of strings 2-10, J, Q, K, A to Ranks.
     *
     * @return the map of strings to Ranks
     */
    private static Map<String, Rank> createString2RankMap() {
        Map<String, Rank> string2RankMap = new HashMap<>();
        string2RankMap.put("A", Rank.ACE);
        string2RankMap.put("K", Rank.KING);
        string2RankMap.put("Q", Rank.QUEEN);
        string2RankMap.put("J", Rank.JACK);
        string2RankMap.put("10", Rank.TEN);
        string2RankMap.put("9", Rank.NINE);
        string2RankMap.put("8", Rank.EIGHT);
        string2RankMap.put("7", Rank.SEVEN);
        string2RankMap.put("6", Rank.SIX);
        string2RankMap.put("5", Rank.FIVE);
        string2RankMap.put("4", Rank.FOUR);
        string2RankMap.put("3", Rank.THREE);
        string2RankMap.put("2", Rank.TWO);

        return string2RankMap;
    }

    @Override
    public String toString() {
        String rank;
        switch (this.rank) {
            case ACE -> rank = "A";
            case TWO -> rank = "2";
            case THREE -> rank = "3";
            case FOUR -> rank = "4";
            case FIVE -> rank = "5";
            case SIX -> rank = "6";
            case SEVEN -> rank = "7";
            case EIGHT -> rank = "8";
            case NINE -> rank = "9";
            case TEN -> rank = "10";
            case JACK -> rank = "J";
            case QUEEN -> rank = "Q";
            case KING -> rank = "K";
            default -> rank = "[ERROR: unknown rank]";
        }

        String suit;
        switch (this.suit) {
            case CLUBS -> suit = "c";
            case HEARTS -> suit = "h";
            case DIAMONDS -> suit = "d";
            case SPADES -> suit = "s";
            default -> suit = "[ERROR: unknown suit]";
        }

        return rank;
    }

    /**
     * Compares the RANK equality of the current card with another object.
     *
     * @param o Another object.
     * @return true if the object is equivalent to the current card. False otherwise
     */
    public boolean equals(Object o) {
        return (o instanceof Card otherCard) &&
                this.suit == otherCard.suit &&
                this.rank == otherCard.rank;
    }

    public int hashCode() {
        return (new Point(this.rank.ordinal(), this.suit.ordinal()).hashCode());
    }

}
