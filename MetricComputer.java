import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Capable of computing metrics for strategies.
 */
public class MetricComputer {

    /**
     * Prints step-by-step messages of playing the table for debugging.
     */
    private static final boolean debugFlag = false;

    /**
     * Computes the total-earning, max-earning, and min-earning metrics for
     * each of the strategies in the input list, and prints out the results.
     *
     * @param playerStrategies a list of strategies
     * @param tableSupplier creates the tables for use in each trial
     * @param numTrials the number of random tables to play with each strategy
     */
    public static Map<IPlayerStrategy, MetricRecorder> computeMetrics(List<IPlayerStrategy> playerStrategies, Supplier<Table> tableSupplier, int numTrials) {

        /*
         * Initialize a map from each strategy to an object which can keep track
         * of the metrics.
         */
        Map<IPlayerStrategy, MetricRecorder> metricMap = new HashMap<>();
        for (IPlayerStrategy playerStrategy: playerStrategies) {
            metricMap.put(playerStrategy, new MetricRecorder());
        }

        // Play numTrials random tables using each strategy.
        for (int i = 0; i < numTrials; i++) {
            if (i % 1000000 == 0) {
                System.out.println(i / 1000000);
            }
            Table randomTable = tableSupplier.get();
            for (IPlayerStrategy playerStrategy: playerStrategies) {

                debugln("\n\n\n\n\n\n\n\n\n\nUsing " + playerStrategy + " Strategy");

                // Each strategy uses the same table, for fairness.
                Table tableCopy = randomTable.copy();
                debugln(tableCopy.toString());

                // Play player 1's hand with the strategy.
                double earnings = tableCopy.play(1, playerStrategy);

                // Update the object keeping track of the metrics.
                metricMap.get(playerStrategy).updateEarnings(earnings);

                debugln("\nUpdated metric:\n" + metricMap.get(playerStrategy));
            }
            System.out.println(metricMap);
        }

        return metricMap;
    }

    public static Map<IPlayerStrategy, MetricRecorder> computeMetrics(List<IPlayerStrategy> playerStrategies, String inputCSVLine, int numOtherPlayers, int numTrials) {

        /*
         * Initialize a map from each strategy to an object which can keep track
         * of the metrics.
         */
        Map<IPlayerStrategy, MetricRecorder> metricMap = new HashMap<>();
        for (IPlayerStrategy playerStrategy: playerStrategies) {
            metricMap.put(playerStrategy, new MetricRecorder());
        }

        // Play numTrials random tables using each strategy.
        for (int i = 0; i < numTrials; i++) {
            System.out.println(i);
            Table randomTable = new Table(inputCSVLine, numOtherPlayers);
            for (IPlayerStrategy playerStrategy: playerStrategies) {

                debugln("\n\n\n\n\n\n\n\n\n\nUsing " + playerStrategy + " Strategy");

                // Each strategy uses the same table, for fairness.
                Table tableCopy = randomTable.copy();
                debugln(tableCopy.toString());

                // Play player 1's hand with the strategy.
                double earnings = tableCopy.play(1, playerStrategy);

                // Update the object keeping track of the metrics.
                metricMap.get(playerStrategy).updateEarnings(earnings);

                debugln("\nUpdated metric:\n" + metricMap.get(playerStrategy));
            }
            //System.out.println(metricMap);
        }

        return metricMap;
    }

    private static void debugln(String msg) {
        if (debugFlag) {
            System.out.println(msg);
        }
    }
}