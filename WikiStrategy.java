import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * A strategy for a player to make a decision with their hand
 * based on the Wikipedia blackjack tables.
 */
public class WikiStrategy implements IPlayerStrategy {

    /**
     * A complete file path to the folder of csv files. This will work
     * on anyone's machine since System.getProperty("user.dir") locates
     * the path of this repository on their machine.
     */
    private static final String csvDir = System.getProperty("user.dir") + File.separator +
                                                            "src" + File.separator +
                                                            "csv";

    /**
     * Holds the Wikipedia table of decisions to make on a pair.
     */
    public static final Map<Rank, Map<Rank, String>> pairMap;

    /**
     * Holds the Wikipedia table of decisions to make on a soft hand.
     */
    public static final Map<Rank, Map<Integer, String>> softMap;

    /**
     * Holds the Wikipedia table of decisions to make on a hard hand.
     */
    public static final Map<Rank, Map<Integer, String>> hardMap;

    /**
     * The singleton instance of this class, since only one instance
     * is ever needed.
     */
    private static final WikiStrategy SINGLETON = new WikiStrategy();

    /**
     * A private constructor to prevent users of this class from
     * creating additional instances of the class.
     */
    private WikiStrategy() {

    }

    /**
     * Returns the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    public static WikiStrategy singleton() {
        return SINGLETON;
    }

    /*
     * Parse the Wikipedia tables from the csv files.
     */
    static {
        try {
            pairMap = makePairMap();
            softMap = makeSoftMap();
            hardMap = makeHardMap();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a Decision for the player to make
     * based on the Wikipedia blackjack tables.
     *
     * @param table holds the deck, player hands, and dealer hand
     * @param handInPlay the hand being played
     * @return a decision for the player to make with their hand
     */
    @Override
    public Decision getDecision(Table table, PlayerHand handInPlay) {
        Card dealerCard = table.getDealerHand().getCards().get(0);
        Rank dealerRank = dealerCard.getRank();

        String decision;

        if (handInPlay.isPair()) {     //if hand is pair use pair sheet
            decision = pairMap.get(dealerRank).get(handInPlay.getCards().get(0).getRank());
        } else if (handInPlay.isSoft()) {      //if hand is soft (contains ace) use soft sheet
            decision = softMap.get(dealerRank).get(handInPlay.getSoft() - 11);
        } else {        //otherwise use hard sheet
            decision = hardMap.get(dealerRank).get(handInPlay.getHard());
        }

        // Testing for alternate outputs dependent on hand size
        if (decision.contains("/")) {
            String option1 = decision.substring(0, decision.indexOf('/'));
            String option2 = decision.substring(decision.indexOf('/') + 1);
            decision = (handInPlay.handSize() == 2) ? option1 : option2;
        }

        // Convert the string to an enum.
        return Enum.valueOf(Decision.class, decision);
    }

    /**
     * This method parses the CSV file of the PAIR TABLE of the wiki strategy to create
     * a mapping that will give the correct decision for the given dealer and player hands.
     *
     * @return a nested mapping of card ranks to strings that represents the PAIR TABLE
     *         of the wiki strategy.
     */
    private static Map<Rank, Map<Rank, String>> makePairMap() throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(csvDir + File.separator + "wiki_strategy_simplified_pairs.csv"));
        String csvLine;
        while ((csvLine = reader.readLine()) != null) {
            lines.add(csvLine);
        }

        reader.close();

        List<String> dealerCardStrings = new ArrayList<>(Arrays.asList(lines.get(0).split(",")));
        dealerCardStrings.remove(0); // get rid of empty string corner of table

        Map<Rank, Map<Rank, String>> pairDecisionMap = new HashMap<>();

        // Empty shell maps to fill
        for (String dealerCard : dealerCardStrings) {
            pairDecisionMap.put(Card.string2RankMap.get(dealerCard), new HashMap<>());
        }

        for (int lineIdx = 1; lineIdx < lines.size(); lineIdx++) {
            String line = lines.get(lineIdx);
            String[] decisions = line.split(",");
            String playerCard = decisions[0].substring(decisions[0].indexOf("+") + 1);

            for (int decIdx = 1; decIdx < decisions.length; decIdx++) {
                Map<Rank, String> playerCard2DecisionMap = pairDecisionMap.get(Card.string2RankMap.get(dealerCardStrings.get(decIdx - 1)));
                playerCard2DecisionMap.put(Card.string2RankMap.get(playerCard), decisions[decIdx]);
            }
        }

        return pairDecisionMap;
    }

    /**
     * This method parses the CSV file of the SOFT TABLE of the wiki strategy to create
     * a mapping that will give the correct decision for the given dealer and player hands.
     *
     * @return a nested mapping that represents the SOFT TABLE of the wiki strategy.
     *         NOTE: the type of the inner keys of the map (representing the player hand) is INTEGER
     *         rather than type Card.Rank.
     */
    private static Map<Rank, Map<Integer, String>> makeSoftMap() throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(csvDir + File.separator + "wiki_strategy_simplified_soft.csv"));
        String csvLine;
        while ((csvLine = reader.readLine()) != null) {
            lines.add(csvLine);
        }
        reader.close();

        List<String> dealerCardStrings = new ArrayList<>(Arrays.asList(lines.get(0).split(",")));
        dealerCardStrings.remove(0); // get rid of empty string corner of table

        Map<Rank, Map<Integer, String>> softDecisionMap = new HashMap<>();

        // Empty shell maps to fill
        for (String dealerCard : dealerCardStrings) {
            softDecisionMap.put(Card.string2RankMap.get(dealerCard), new HashMap<>());
        }

        for (int lineIdx = 1; lineIdx < lines.size(); lineIdx++) {
            String line = lines.get(lineIdx);
            String[] decisions = line.split(",");
            String playerCard = decisions[0].substring(decisions[0].indexOf("+") + 1);

            for (int decIdx = 1; decIdx < decisions.length; decIdx++) {
                Map<Integer, String> playerCard2DecisionMap = softDecisionMap.get(Card.string2RankMap.get(dealerCardStrings.get(decIdx - 1)));
                playerCard2DecisionMap.put(Integer.parseInt(playerCard), decisions[decIdx]);
            }
        }

        return softDecisionMap;
    }

    /**
     * This method parses the CSV file of the HARD TABLE of the wiki strategy to create
     * a mapping that will give the correct decision for the given dealer and player hands.
     *
     * @return a nested mapping that represents the HARD TABLE of the wiki strategy.
     *         NOTE: the type of the inner keys of the map (representing the player hand) is INTEGER
     *         rather than type Card.Rank.
     */
    private static Map<Rank, Map<Integer, String>> makeHardMap() throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(csvDir + File.separator + "wiki_strategy_simplified_hard.csv"));
        String csvLine;
        while ((csvLine = reader.readLine()) != null) {
            lines.add(csvLine);
        }
        reader.close();

        List<String> dealerCardStrings = new ArrayList<>(Arrays.asList(lines.get(0).split(",")));
        dealerCardStrings.remove(0); // get rid of empty string corner of table

        Map<Rank, Map<Integer, String>> hardDecisionMap = new HashMap<>();

        // Empty shell maps to fill
        for (String dealerCard : dealerCardStrings) {
            hardDecisionMap.put(Card.string2RankMap.get(dealerCard), new HashMap<>());
        }

        for (int lineIdx = 1; lineIdx < lines.size(); lineIdx++) {
            String line = lines.get(lineIdx);
            String[] decisions = line.split(",");
            String playerCard = decisions[0];

            for (int decIdx = 1; decIdx < decisions.length; decIdx++) {
                Map<Integer, String> playerCard2DecisionMap = hardDecisionMap.get(Card.string2RankMap.get(dealerCardStrings.get(decIdx - 1)));
                playerCard2DecisionMap.put(Integer.parseInt(playerCard), decisions[decIdx]);
            }
        }

        return hardDecisionMap;
    }

    @Override
    public String toString() {
        return "Wiki";
    }

}