import java.util.*;

/**
 * Represents the standard 52-card deck.
 */
public class Deck {

    /**
     * Holds the cards in the deck.
     */
    private final List<Card> cards;

    /**
     * A private constructor only for use within this class.
     */
    private Deck() {
        this.cards = new LinkedList<>();
    }

    /**
     * Returns a shuffled deck of the 52 cards.
     *
     * @return a shuffled deck
     */
    public static Deck shuffledDeck() {
        Deck deck = sortedDeck();
        deck.shuffle();
        return deck;
    }

    /**
     * Returns a sorted deck of the 52 cards.
     *
     * @return a sorted deck
     */
    private static Deck sortedDeck() {
        Deck deck = new Deck();
        for (Suit suit: Suit.values()) {
            for (Rank rank: Rank.values()) {
                deck.cards.add(new Card(suit, rank));
            }
        }
        return deck;
    }

    /**
     * Returns a deep copy of this deck.
     *
     * @return a deep copy of this deck
     */
    public Deck copy() {
        Deck deckCopy = new Deck();
        for (Card card: this.cards) {
            deckCopy.cards.add(card.copy());
        }
        return deckCopy;
    }

//    public Map<Card, Integer> getCardSet() {
//        Map<Card, Integer> returnMap = new HashMap<>();
//        for(Card card : this.cards) {
//            if(!returnMap.containsKey(card)){
//                returnMap.put(card, 1);
//            }else{
//                returnMap.put(card, returnMap.get(card) + 1);
//            }
//        }
//        return returnMap;
//    }

    /**
     * Draws a card from the top of the deck
     * (removing it from the deck).
     *
     * @return the card that was drawn
     */
    public Card draw() {
        return cards.remove(0);
    }

    /**
     * Removes a card from the deck of the input rank.
     *
     * @param rank the rank of the card to remove
     *
     * @return the card that was removed
     */
    public Card removeCard(Rank rank) {
        Card toRemove = null;
        for (Card card: this.cards) {
            if (card.getRank() == rank) {
                toRemove = card;
                break;
            }
        }
        this.cards.remove(toRemove);
        return toRemove;
    }

    /**
     * Removes all of the cards in the input collection
     * from the deck. If any of the cards in the collection
     * were never in the deck in the first place, these
     * cards are simply ignored.
     *
     * @param collection a collection of cards
     */
    public void removeAll(Collection<Card> collection) {
        for (Card card: collection) {
            this.removeCard(card.getRank());
        }
    }

    /**
     * Shuffles the deck's cards into a random order.
     */
    private void shuffle() {
        Collections.shuffle(this.cards);
    }

    @Override
    public String toString() {
        return this.cards.toString();
    }

}