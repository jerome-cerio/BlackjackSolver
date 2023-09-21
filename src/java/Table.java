import java.util.*;

/**
 * A concrete class representing all of the hands on the table
 * as well as the deck.
 */
public class Table {

    /**
     * Prints step-by-step messages of playing the table for debugging.
     */
    private static final boolean debugFlag = false;

    /**
     * The deck of cards remaining (that haven't been played on the table).
     */
    private Deck deck;

    /**
     * The dealer's hand.
     */
    private DealerHand dealerHand;

    /**
     * A collection of all of the players' hands. Each inner list
     * at index i represents the list of split hands owned by
     * player (i + 1).
     */
    private final List<List<PlayerHand>> playerHands;

    /**
     * Initializes the list of player hands to an empty list.
     */
    private Table() {
        this.playerHands = new ArrayList<>();
    }

    /**
     * Creates a random table by first creating a random deck,
     * then dealing one card to the dealer and two cards to
     * player 1. (The other players aren't dealt any cards
     * in this method.)
     *
     * @return
     */
    public static Table randomTable() {
        Table table = new Table();
        table.deck = Deck.shuffledDeck();

        // Give the dealer one card.
        table.dealerHand = new DealerHand(table.deck.draw());

        // Add a 2-card hand for player 1.
        List<PlayerHand> player1HandList = new ArrayList<>();
        player1HandList.add(new PlayerHand(table.deck.draw(), table.deck.draw()));
        table.playerHands.add(player1HandList);

        // Add space for the other players' hands.
        for (int i = 0; i < 3; i++) {
            table.playerHands.add(new ArrayList<>());
        }

        return table;
    }

    /**
     * Returns a deep copy of the table.
     *
     * @return a deep copy of the table
     */
    public Table copy() {
        Table copyTable = new Table();
        copyTable.deck = this.deck.copy();
        copyTable.dealerHand = this.dealerHand.copy();
        for (List<PlayerHand> handList: this.playerHands) {
            List<PlayerHand> copyHandList = new ArrayList<>(1);
            for (PlayerHand hand: handList) {
                copyHandList.add(hand.copy());
            }
            copyTable.playerHands.add(copyHandList);
        }
        return copyTable;
    }

    /**
     * Constructs each hand from a line of csv.
     *
     * @param csvLine a line of csv
     * @param numOtherPlayers the number of other players (excluding the dealer
     *                          and player 1) with which to interpret the line
     *                          of csv
     */
    public Table(String csvLine, int numOtherPlayers) {

        int handStartIdx = 1;
        int handEndIdx = findCommaAfter(1, csvLine);

        // Add the dealer's hand.
        this.dealerHand = new DealerHand(csvLine.substring(handStartIdx, handEndIdx));

        // Add the other players' hands (excluding player 1).
        this.playerHands = new ArrayList<>(5);
        for (int i = 0; i < numOtherPlayers; i++) {

            int previousHandEndIdx = handEndIdx;

            // This hand starts after the comma at the end of the previous hand.
            handStartIdx = previousHandEndIdx + 1;

            // Every player who is not player 1 has two cards.
            handEndIdx = findCommaAfter(findCommaAfter(previousHandEndIdx, csvLine), csvLine);

            List<PlayerHand> otherPlayerHandList = new ArrayList<>(1);
            otherPlayerHandList.add(new PlayerHand(csvLine.substring(handStartIdx, handEndIdx)));
            this.playerHands.add(otherPlayerHandList);
        }

        // Add player 1's hand. The remaining cards all belong to player 1.
        List<PlayerHand> player1HandList = new ArrayList<>(2);
        player1HandList.add(new PlayerHand(csvLine.substring(handEndIdx + 1)));
        this.playerHands.add(player1HandList);

        // Swap the order so that player 1 is at index 0, player 2 is at index 1,...
        Collections.reverse(this.playerHands);

        // Create a shuffled deck of 52 cards.
        this.deck = Deck.shuffledDeck();
        // Remove the cards on the table from the deck.
        this.deck.removeAll(this.countCardsOnTable());
    }

    public Table(Deck deck, DealerHand dealerHand, List<List<PlayerHand>> playerHands) {
        this.deck = deck;
        this.dealerHand = dealerHand;
        this.playerHands = playerHands;
    }

    /**
     * Returns the deck of cards that haven't appeared on the table yet.
     *
     * @return the deck of cards that haven't appeared on the table yet
     */
    public Deck getDeck() {
        return this.deck;
    }

    /**
     * Return the input player's list of split hands.
     *
     * @param playerNumber a player number
     * @return the input player's list of split hands
     */
    public List<PlayerHand> getHandList(int playerNumber) {
        return this.playerHands.get(playerNumber - 1);
    }

    public List<List<PlayerHand>> getPlayerHands() {
        return this.playerHands;
    }

    /**
     * Returns the dealer's hand.
     *
     * @return the dealer's hand
     */
    public DealerHand getDealerHand() {
        return this.dealerHand;
    }

    /**
     * Returns a set of all the cards shown on the table
     * (in the dealer's hand or the players hands).
     *
     * @return a set of all the cards on the table
     */
    public List<Card> countCardsOnTable() {
        List<Card> cardsOnTable = new ArrayList<>();

        // Count the dealer's cards.
        cardsOnTable.addAll(this.dealerHand.getCards());

        // Count the players' cards.
        for (List<PlayerHand> playerHandList: this.playerHands) {
            for (PlayerHand playerHand: playerHandList) {
                cardsOnTable.addAll(playerHand.getCards());
            }
        }

        return cardsOnTable;
    }

    /**
     * Returns a map from each rank appearing in the deck to its
     * probability of being drawn from the deck.
     *
     * @return a map from each rank appearing in the deck to its
     *           probability of being drawn
     */
    public Map<Rank, Double> deckProbabilities() {
        int total = 52;
        Map<Rank, Double> frequencyMap = new HashMap<>();
        frequencyMap.put(Rank.ACE, 4.0);
        frequencyMap.put(Rank.TWO, 4.0);
        frequencyMap.put(Rank.THREE, 4.0);
        frequencyMap.put(Rank.FOUR, 4.0);
        frequencyMap.put(Rank.FIVE, 4.0);
        frequencyMap.put(Rank.SIX, 4.0);
        frequencyMap.put(Rank.SEVEN, 4.0);
        frequencyMap.put(Rank.EIGHT, 4.0);
        frequencyMap.put(Rank.NINE, 4.0);
        frequencyMap.put(Rank.TEN, 4.0);
        frequencyMap.put(Rank.JACK, 4.0);
        frequencyMap.put(Rank.QUEEN, 4.0);
        frequencyMap.put(Rank.KING, 4.0);
        for (Card cardOnTable: this.countCardsOnTable()) {
            frequencyMap.put(cardOnTable.getRank(), frequencyMap.get(cardOnTable.getRank()) - 1);
            total--;
        }
        for (Map.Entry<Rank, Double> entry : frequencyMap.entrySet()) {
            entry.setValue(entry.getValue() / total);
        }
        for (Map.Entry<Rank, Double> entry: (new HashMap<>(frequencyMap)).entrySet()) {
            if (entry.getValue() == 0.0) {
                frequencyMap.remove(entry.getKey());
            }
        }
        return frequencyMap;
    }

    /**
     * Simulates the actual game by playing the input player's starting hand
     * using the input strategy. After the player's split hands are all played,
     * the dealer's hand is played, and the player's total earnings are computed.
     *
     * @param playerNumber the number of the player to play
     * @param playerStrategy the strategy to be used by the player
     * @return the total earnings of the player after playing the dealer
     */
    public double play(int playerNumber, IPlayerStrategy playerStrategy) {

        // Play the player's starting hand as well as any split hands that are created.
        this.playPlayerHand(playerNumber, playerStrategy, this.getHandList(playerNumber).get(0));

        // Play the dealer's hand.
        this.playDealerHand();

        debugln("Computing player 1's earnings on each hand.");

        double earnings = this.calculateEarnings(playerNumber);

        debugln("Total earnings: " + earnings);

        return earnings;
    }

    public double calculateEarnings(int playerNumber) {

        // Sum the player's earnings from each of their split hands against the dealer.
        double earnings = 0.0;
        for (PlayerHand playerHand: this.getHandList(playerNumber)) {
            debug("Hand " + playerHand.toString());
            if (playerHand.surrendered()) {
                debugln(" loses " + playerHand.getBet() / 2.0 + " for surrendering.");
                earnings -= playerHand.getBet() / 2.0;
            } else if (playerHand.isBlackJack() && this.dealerHand.isBlackJack()) {
                debugln(" ties with dealer's hand; both Blackjacks.");
                continue;
            } else if (playerHand.isBlackJack() && !this.dealerHand.isBlackJack()) {
                debugln(" wins " + 1.5 * playerHand.getBet() + " since it is a Blackjack and the dealer's hand isn't.");
                earnings += 1.5 * playerHand.getBet();
            } else if (!playerHand.isBlackJack() && this.dealerHand.isBlackJack()) {
                debugln(" loses " + playerHand.getBet() + " since it isn't a Blackjack but the dealer's hand is.");
                earnings -= playerHand.getBet();
            } else if (playerHand.isBust()) {
                debugln(" loses " + playerHand.getBet() + " since it busted.");
                earnings -= playerHand.getBet();
            } else if (this.dealerHand.isBust()) {
                debugln(" wins " + playerHand.getBet() + " since the dealer busted.");
                earnings += playerHand.getBet();
            } else {
                int dealerValue = this.dealerHand.bestValue();
                int playerValue = playerHand.bestValue();
                if (playerValue > dealerValue) {
                    debugln(" wins " + playerHand.getBet() + " since it is higher value.");
                    earnings += playerHand.getBet();
                } else if (playerValue < dealerValue) {
                    debugln(" loses " + playerHand.getBet() + " since it is lower value.");
                    earnings -= playerHand.getBet();
                } else {
                    debugln(" ties with the dealer's hand; equal value.");
                }
            }
        }
        return earnings;
    }

    /**
     * Plays the input player hand and any split hands that arise from it until
     * all of these hands are final.
     *
     * @param playerNumber the player that owns the input hand
     * @param playerStrategy the strategy to be used when playing the hands
     * @param playerHand the hand to be played completely
     */
    private void playPlayerHand(int playerNumber, IPlayerStrategy playerStrategy, PlayerHand playerHand) {
        debugln("Playing hand: " + playerHand);
        do {
            Decision decision = playerStrategy.getDecision(this, playerHand);
            if (decision == Decision.SPLIT) {
                List<PlayerHand> splitHands = this.split(playerNumber, playerHand, this.deck.draw(), this.deck.draw());

                debugln("Decided to SPLIT. Updated table:\n" + this);

                // Recursively play both of the split hands using the same strategy.
                this.playPlayerHand(playerNumber, playerStrategy, splitHands.get(0));
                this.playPlayerHand(playerNumber, playerStrategy, splitHands.get(1));
                return; // done playing both split hands
            } else if (decision == Decision.HIT) {
                this.hit(playerHand, this.deck.draw());
                debugln("Decided to HIT. Updated table:\n" + this);
            } else if (decision == Decision.DOUBLE) {
                this.doubleMove(playerHand, this.deck.draw());
                debugln("Decided to DOUBLE. Updated table:\n" + this);
            } else if (decision == Decision.SURRENDER) {
                this.surrender(playerHand);
                debugln("Decided to SURRENDER.");
            } else {
                this.stay(playerHand);
                debugln("Decided to STAY.");
            }
        } while (!playerHand.isFinal());
    }

    /**
     * Plays the dealer's hand.
     */
    private void playDealerHand() {
        // Correct implementation, from Blackjack Game Rules document.
        debugln("\nPlaying dealer.");
        while (!this.dealerHand.isFinal()) {
            this.hitDealer(this.deck.draw());
            debugln("Dealer hit. Updated table:\n" + this);
        }

        // Incorrect implementation from HW3 instructions document.
//        debugln("\nPlaying dealer.");
//        while (!this.dealerHand.isBust() &&
//                !(this.dealerHand.getHard() > 17) &&
//                !(this.dealerHand.isSoft() && this.dealerHand.getSoft() == 17)) {
//            this.dealerHand.addCard(this.deck.draw());
//            debugln("Dealer hit. Updated table:\n" + this);
//        }
    }

    /**
     * Simulates the move of HITTING for the player.
     */
    public void hit(PlayerHand hand, Card drawnCard) {
        hand.addCard(drawnCard);
        if (hand.isBust()) {
            hand.markFinal();
        }
    }

    /**
     * Simulates the move of STAYING for the player.
     *
     * @param hand The current player's hand.
     */
    public void stay(PlayerHand hand) {
        hand.markFinal();
    }

    /**
     * Simulates the move of DOUBLING for the player.
     *
     * @param hand The current player's hand.
     */
    public void doubleMove(PlayerHand hand, Card drawnCard) {
        hand.addCard(drawnCard);
        hand.doubleBet();
        hand.markFinal();
    }

    /**
     * Simulates the move of SURRENDERING for the player.
     *
     * @param hand The current player's hand.
     */
    public void surrender(PlayerHand hand) {
        hand.markSurrendered();
        hand.markFinal();
    }

    /**
     * Simulates the move of SPLITTING for the player.
     *
     * @param playerNumber the player's number
     * @param hand the current player's hand
     * @param drawnCard1 the card to add to the first split hand
     * @param drawnCard2 the card to add to the second split hand
     * @return a two-element list of the resulting split hands
     */
    public List<PlayerHand> split(int playerNumber, PlayerHand hand, Card drawnCard1, Card drawnCard2) {
        List<Card> initialTwoCards = hand.getCards();

        /*
         * Create two split hands from the pair of cards in the input hand.
         * Deal each of these split hands an additional card.
         */
        PlayerHand splitHand1 = new PlayerHand(initialTwoCards.get(0), drawnCard1);
        PlayerHand splitHand2 = new PlayerHand(initialTwoCards.get(1), drawnCard2);

        // Remove the old, un-split hand from the player's hand list.
        this.getHandList(playerNumber).remove(hand);

        // Add the new split hands to the beginning of the player's hand list.
        this.getHandList(playerNumber).add(0, splitHand1);
        this.getHandList(playerNumber).add(1, splitHand2);

        // Return the split hands as a two-element list.
        return List.of(splitHand1, splitHand2);
    }

    /**
     * Simulates when the dealer is hit with a card.
     *
     * @param drawnCard the card to add to the dealer's hand
     */
    public void hitDealer(Card drawnCard) {
        this.dealerHand.addCard(drawnCard);
        if (this.dealerHand.isBust()) {           // Stop after busting
            this.dealerHand.markFinal();
        } else if (this.dealerHand.isSoft()) {
            if (this.dealerHand.getSoft() > 17) { // Stop on soft hand > 17
                this.dealerHand.markFinal();
            }
        } else {
            if (this.dealerHand.getHard() >= 17) { // Stop on hard hand >= 17
                this.dealerHand.markFinal();
            }
        }
    }

    /**
     * Returns the first index of a comma contained in the input
     * string, where the comma's index is strictly greater than the
     * input index.
     *
     * @param idx an index in the string
     * @param str a string
     * @return the first index of a comma in the input string after the input index
     */
    private static int findCommaAfter(int idx, String str) {
        return idx + 1 + str.substring(idx + 1).indexOf(',');
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("D: ").append(this.dealerHand.toString()).append("\n");
        for (int playerNumber = 1; playerNumber <= this.playerHands.size(); playerNumber++) {
            sb.append("P").append(playerNumber).append(": ").append(this.getHandList(playerNumber).toString()).append("\n");
        }
        sb.append("Deck: ").append(this.deck.toString()).append("\n");
        return sb.toString();
    }

    /**
     * Helper method that prints statements indicating where the program is along
     * the computation process.
     *
     * @param msg a String of the message to be printed to the command line.
     */
    private static void debug(String msg) {
        if (debugFlag) {
            System.out.print(msg);
        }
    }

    /**
     * Helper method that prints statements indicating where the program is along
     * the computation process, with a trailing newline character.
     *
     * @param msg a String of the message to be printed to the command line.
     */
    private static void debugln(String msg) {
        if (debugFlag) {
            System.out.println(msg);
        }
    }
}
