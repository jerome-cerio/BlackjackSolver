/**
 * This class represents the Statistically Best Strategy
 * for a player to play Blackjack.
 */

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.util.concurrent.*;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * A strategy for Player 1 to make a decision with their hand(s)
 * which chooses the decision with the highest expected earnings.
 */
public class IdealStrategy implements IPlayerStrategy {

    private static int numCalls = 0;

    private static final ListeningExecutorService execService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

    private record Position(DealerHand dealerHand, List<List<PlayerHand>> playerHands) {

        public Position(Table table) {
            this(table.getDealerHand(), table.getPlayerHands());
        }

        public Table convertToTable() {
            Deck deck = Deck.shuffledDeck();
            deck.removeAll(dealerHand.getCards());
            for (List<PlayerHand> handList: playerHands) {
                for (PlayerHand hand: handList) {
                    deck.removeAll(hand.getCards());
                }
            }
            return new Table(deck, dealerHand, playerHands);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Position otherPosition)) {
                return false;
            }
            return this.getPlayer1HandSet().equals(otherPosition.getPlayer1HandSet()) &&
                    this.getOtherCardRankSet().equals(otherPosition.getOtherCardRankSet()) &&
                    this.getDealerCardRankSet().equals(otherPosition.getOtherCardRankSet());
        }

        @Override
        public int hashCode() {
            return playerHands.hashCode();
        }

        private Multiset<PlayerHand> getPlayer1HandSet() {
            return HashMultiset.create(playerHands.get(0));
        }

        private Multiset<Rank> getOtherCardRankSet() {
            Multiset<Rank> others = HashMultiset.create();
            for (int i = 1; i < playerHands.size(); i++) {
                List<Card> otherPlayerCards = playerHands.get(i).get(0).getCards();
                others.addAll(otherPlayerCards.stream().map(Card::getRank).toList());
            }
            return others;
        }

        private Multiset<Rank> getDealerCardRankSet() {
            return HashMultiset.create(dealerHand.getCards().stream().map(Card::getRank).toList());
        }

    }

    /**
     * Bundles together the best decision for the player to make
     * on a particular position as well as the expected earnings
     * of making that decision.
     *
     * @param decision the best decision for the player to make on a particular position
     * @param earnings the expected earnings of the player making that decision
     */
    private record DecisionAndEarnings(Decision decision, double earnings) {
        public Decision getDecision() {
            return decision;
        }

        public double getEarnings() {
            return earnings;
        }
    }

    /**
     * The singleton instance of this class, since only one instance
     * is ever needed.
     */
    private static final IdealStrategy SINGLETON = new IdealStrategy();

    /**
     * A private constructor to prevent users of this class from
     * creating additional instances of the class.
     */
    private IdealStrategy() {

    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static IdealStrategy singleton() {
        return IdealStrategy.SINGLETON;
    }

    /**
     * A future cache for computing the best decision to make on a position as well as the
     * expected earnings of making that decision.
     */
    public final LoadingCache<Position, ListenableFuture<DecisionAndEarnings>> earningsCache =
        CacheBuilder.newBuilder().build(new CacheLoader<>() {
            public ListenableFuture<DecisionAndEarnings> load(Position position) {
                if (numCalls++ % 10000 == 0) {
                    System.out.println(numCalls / 10000);
                }
                Table table = position.convertToTable();

                // A list of all of the possible positions just for call to Futures.transform() below.
                final List<ListenableFuture<DecisionAndEarnings>> allFutures = new ArrayList<>();

                // A map to store each possible position after HIT to its probability of occurring.
                final Map<ListenableFuture<DecisionAndEarnings>, Double> ifHit = new HashMap<>();;

                // Stores the resulting position (with probability 1) after STAY.
                ListenableFuture<DecisionAndEarnings> ifStay = null;

                // A map to store each possible position after DOUBLE to its probability of occurring.
                final Map<ListenableFuture<DecisionAndEarnings>, Double> ifDouble = new HashMap<>();

                // A map to store each possible position after SPLIT to its probability of occurring.
                final Map<ListenableFuture<DecisionAndEarnings>, Double> ifSplit = new HashMap<>();

                // Stores the resulting position (with probability 1) after STAY.
                ListenableFuture<DecisionAndEarnings> ifSurrender = null;

                // A map to store each possible position after the dealer hits to its probability of occurring.
                final Map<ListenableFuture<DecisionAndEarnings>, Double> ifDealerHit = new HashMap<>();

                // Search for the first non-final hand controlled by Player 1.
                PlayerHand handInPlay = null;
                for (PlayerHand splitHand: table.getHandList(1)) {
                    if (!splitHand.isFinal()) {
                        handInPlay = splitHand;
                        break;
                    }
                }

                /*
                 * If Player 1 has a non-final hand, consider all of its possible decisions and each of the
                 * possible tables that result from drawing each possible rank of card from the deck.
                 *
                 * Otherwise, if all of Player 1's hands are final, then if the dealer's hand isn't final,
                 * consider all of the possible tables that result from the dealer being hit with each
                 * possible rank of card from the deck.
                 *
                 * Otherwise, meaning Player 1's hands are final and the dealer's hand is final,
                 * simply compute Player 1's total earnings against the dealer's hand.
                 */
                if (handInPlay != null) { /* Play Player 1's first non-final hand */
                    for (Decision decision: handInPlay.getPossibleDecisions()) {
                        switch (decision) {
                            case HIT -> {
                                /*
                                 * Simulate each position that can occur from hitting (one position for each
                                 * rank of card that can be drawn).
                                 */
                                Map<Rank, Double> deckProbs = table.deckProbabilities();
                                for (Map.Entry<Rank, Double> entry : deckProbs.entrySet()) {
                                    Rank possibleRank = entry.getKey();
                                    Double probability = entry.getValue();
                                    Table tableCopy = table.copy();
                                    PlayerHand handInPlayCopy = tableCopy.getHandList(1).get(table.getHandList(1).indexOf(handInPlay));
                                    Card drawnCard = tableCopy.getDeck().removeCard(possibleRank);
                                    tableCopy.hit(handInPlayCopy, drawnCard);
                                    ifHit.put(earningsCache.getUnchecked(new Position(tableCopy)), probability);
                                }
                                allFutures.addAll(ifHit.keySet());
                            }
                            case STAY -> {
                                // Simulate staying (one resulting position).
                                Table tableCopy = table.copy();
                                PlayerHand handInPlayCopy = tableCopy.getHandList(1).get(table.getHandList(1).indexOf(handInPlay));
                                tableCopy.stay(handInPlayCopy);
                                ifStay = earningsCache.getUnchecked(new Position(tableCopy));
                                allFutures.add(ifStay);
                            }
                            case DOUBLE -> {
                                /*
                                 * Simulate each position that can occur from doubling (one position for each
                                 * rank of card that can be drawn).
                                 */
                                for (Map.Entry<Rank, Double> entry : table.deckProbabilities().entrySet()) {
                                    Rank possibleRank = entry.getKey();
                                    Double probability = entry.getValue();
                                    Table tableCopy = table.copy();
                                    PlayerHand handInPlayCopy = tableCopy.getHandList(1).get(table.getHandList(1).indexOf(handInPlay));
                                    Card drawnCard = tableCopy.getDeck().removeCard(possibleRank);
                                    tableCopy.doubleMove(handInPlayCopy, drawnCard);
                                    ifDouble.put(earningsCache.getUnchecked(new Position(tableCopy)), probability);
                                }
                                allFutures.addAll(ifDouble.keySet());
                            }
                            case SPLIT -> {
                                /*
                                 * Simulate each position that can occur from splitting (one position for each
                                 * pair of ranks of cards that can be drawn and given to the split hands).
                                 */
                                for (Map.Entry<Rank, Double> entry1 : table.deckProbabilities().entrySet()) {
                                    Rank possibleRank1 = entry1.getKey();
                                    Double probabilityCard1 = entry1.getValue();
                                    Table tableCopy1 = table.copy();
                                    Card drawnCard1 = tableCopy1.getDeck().removeCard(possibleRank1);
                                    for (Map.Entry<Rank, Double> entry2 : tableCopy1.deckProbabilities().entrySet()) {
                                        Rank possibleRank2 = entry2.getKey();
                                        Double probabilityCard2 = entry2.getValue();
                                        Table tableCopy2 = tableCopy1.copy();
                                        PlayerHand handInPlayCopy2 = tableCopy2.getHandList(1).get(table.getHandList(1).indexOf(handInPlay));
                                        Card drawnCard2 = tableCopy2.getDeck().removeCard(possibleRank2);
                                        tableCopy2.split(1, handInPlayCopy2, drawnCard1, drawnCard2);
                                        ifSplit.put(earningsCache.getUnchecked(new Position(tableCopy2)), probabilityCard1 * probabilityCard2);
                                    }
                                }
                                allFutures.addAll(ifSplit.keySet());
                            }
                            case SURRENDER -> {
                                // Simulate surrendering (one resulting position).
                                Table tableCopy = table.copy();
                                PlayerHand handInPlayCopy = tableCopy.getHandList(1).get(table.getHandList(1).indexOf(handInPlay));
                                tableCopy.surrender(handInPlayCopy);
                                ifSurrender = earningsCache.getUnchecked(new Position(tableCopy));
                                allFutures.add(ifSurrender);
                            }
                        }
                    }
                } else if (!table.getDealerHand().isFinal()) { /* Simulate the dealer getting hit with one card */
                    /*
                     * Simulate each position that can occur when the dealer hits (one position
                     * per rank of card that can be drawn).
                     */
                    Map<Rank, Double> deckProbs = table.deckProbabilities();
                    for (Map.Entry<Rank, Double> entry : deckProbs.entrySet()) {
                        Rank possibleRank = entry.getKey();
                        Double probability = entry.getValue();
                        Table tableCopy = table.copy();
                        Card drawnCard = tableCopy.getDeck().removeCard(possibleRank); // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        tableCopy.hitDealer(drawnCard);
                        ifDealerHit.put(earningsCache.getUnchecked(new Position(tableCopy)), probability);
                    }
                    allFutures.addAll(ifDealerHit.keySet());
                } else { /* Both Player 1 and the dealer are done playing --- compute Player 1's earnings directly */
                    // null is just a dummy value---the player will not be making any more moves.
                    return Futures.immediateFuture(new DecisionAndEarnings(null, table.calculateEarnings(1)));
                }

                // Create final variable copies to satisfy the compiler in the lambdas below.
                final PlayerHand handInPlayFinal = handInPlay;
                final ListenableFuture<DecisionAndEarnings> ifStayFinal = ifStay;
                final ListenableFuture<DecisionAndEarnings> ifSurrenderFinal = ifSurrender;

                return Futures.transform(Futures.allAsList(allFutures),
                        new AsyncFunction<List<DecisionAndEarnings>, DecisionAndEarnings>() {
                            @Override
                            public ListenableFuture<DecisionAndEarnings> apply(List<DecisionAndEarnings> unused) throws Exception {
                                if (handInPlayFinal != null) { // player's hand is not final

                                    // Compute the expected earnings for each possible decision with the hand.
                                    Map<Decision, Double> expectedEarnings = new HashMap<>();
                                    for (Decision decision: handInPlayFinal.getPossibleDecisions()) {
                                        switch (decision) {
                                            case HIT -> {
                                                for (Map.Entry<ListenableFuture<DecisionAndEarnings>, Double> entry: ifHit.entrySet()) {
                                                    double earnings = entry.getKey().get().getEarnings();
                                                    double probability = entry.getValue();
                                                    if (!expectedEarnings.containsKey(Decision.HIT)) {
                                                        expectedEarnings.put(Decision.HIT, earnings * probability);
                                                    } else {
                                                        expectedEarnings.put(Decision.HIT, expectedEarnings.get(Decision.HIT) + earnings * probability);
                                                    }
                                                }
                                            }
                                            case STAY -> {
                                                expectedEarnings.put(Decision.STAY, ifStayFinal.get().getEarnings());
                                            }
                                            case DOUBLE -> {
                                                for (Map.Entry<ListenableFuture<DecisionAndEarnings>, Double> entry: ifDouble.entrySet()) {
                                                    double earnings = entry.getKey().get().getEarnings();
                                                    double probability = entry.getValue();
                                                    if (!expectedEarnings.containsKey(Decision.DOUBLE)) {
                                                        expectedEarnings.put(Decision.DOUBLE, earnings * probability);
                                                    } else {
                                                        expectedEarnings.put(Decision.DOUBLE, expectedEarnings.get(Decision.DOUBLE) + earnings * probability);
                                                    }
                                                }
                                            }
                                            case SPLIT -> {
                                                for (Map.Entry<ListenableFuture<DecisionAndEarnings>, Double> entry: ifSplit.entrySet()) {
                                                    double earnings = entry.getKey().get().getEarnings();
                                                    double probability = entry.getValue();
                                                    if (!expectedEarnings.containsKey(Decision.SPLIT)) {
                                                        expectedEarnings.put(Decision.SPLIT, earnings * probability);
                                                    } else {
                                                        expectedEarnings.put(Decision.SPLIT, expectedEarnings.get(Decision.SPLIT) + earnings * probability);
                                                    }
                                                }
                                            }
                                            case SURRENDER -> {
                                                expectedEarnings.put(Decision.SURRENDER, ifSurrenderFinal.get().getEarnings());
                                            }
                                        }
                                    }

                                    Decision mostProfitable = null;
                                    double maxEarnings = Double.MIN_VALUE;
                                    for (Decision decision: List.of(Decision.HIT, Decision.STAY, Decision.DOUBLE, Decision.SPLIT, Decision.SURRENDER)) {
                                        if (expectedEarnings.containsKey(decision)) {
                                            double earnings = expectedEarnings.get(decision);
                                            if (earnings > maxEarnings) {
                                                mostProfitable = decision;
                                                maxEarnings = earnings;
                                            }
                                        }
                                    }

                                    return Futures.immediateFuture(new DecisionAndEarnings(mostProfitable, maxEarnings));

                                } else { // guaranteed that dealer's hand is not final
                                    double expectedEarnings = 0.0;
                                    for (Map.Entry<ListenableFuture<DecisionAndEarnings>, Double> entry: ifDealerHit.entrySet()) {
                                        double earnings = entry.getKey().get().getEarnings();
                                        double probability = entry.getValue();
                                        expectedEarnings += earnings * probability;
                                    }

                                    /*
                                     * null is just a dummy value---we know the player won't be making
                                     * any more moves since we are playing the dealer.
                                     */
                                    return Futures.immediateFuture(new DecisionAndEarnings(null, expectedEarnings));
                                }
                            }
                        },
                        // Compute the values in parallel using the number of available processors on the machine.
                        execService // Runtime.getRuntime().availableProcessors()
                );
            }
        });

    /**
     * Recursively determines the decision for the player to make
     * based on the Statistically Best Strategy.
     *
     * @param table holds the deck, player hands, and dealer hand
     * @param handInPlay the hand being played
     * @return a decision for the player to make with their hand
     */
    @Override
    public Decision getDecision(Table table, PlayerHand handInPlay) {
        try {
            return earningsCache.getUnchecked(new Position(table)).get().getDecision();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("IdealStrategy.getDecision() FAILED");
        }
    }

//    public final LoadingCache<Integer, ListenableFuture<BigInteger>> fibonacciCache =
//            CacheBuilder.newBuilder().build(new CacheLoader<Integer, ListenableFuture<BigInteger>>() {
//                @Override
//                public final ListenableFuture<BigInteger> load(Integer fibonacciOrdinal) throws Exception {
//                    Preconditions.checkState(fibonacciOrdinal > 0);
//                    numInvocations.incrementAndGet();
//                    switch (fibonacciOrdinal) {
//                        case 1:
//                            return Futures.immediateFuture(BigInteger.ONE);
//                        case 2:
//                            return Futures.immediateFuture(BigInteger.TWO);
//                        default:
//                            ListenableFuture<BigInteger> previousFNumberFuture =
//                                    fibonacciCache.getUnchecked(fibonacciOrdinal - 1);
//                            ListenableFuture<BigInteger> previousToPreviousFNumberFuture =
//                                    fibonacciCache.getUnchecked(fibonacciOrdinal - 2);
//                            return Futures.transform(
//                                    Futures.allAsList(previousFNumberFuture, previousToPreviousFNumberFuture),
//                                    (dummy) -> {
//                                        try {
//                                            return previousFNumberFuture.get().add(previousToPreviousFNumberFuture.get());
//                                        } catch (Exception e) {
//                                            throw new IllegalStateException();
//                                        }
//                                    },
//                                    Executors.executor());
//                    }}});

}
