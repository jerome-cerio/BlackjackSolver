import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class HW3Main {

    private static final List<IPlayerStrategy> playerStrategies = List.of(NaiveStrategy.singleton(), WikiStrategy.singleton());

    private static final Supplier<Table> tableSupplier = () -> Table.randomTable();

    private static final int numTrials = 100_000_000;

    public static void main(String[] args) {
        Map<IPlayerStrategy, MetricRecorder> metricMap = MetricComputer.computeMetrics(playerStrategies, tableSupplier, numTrials);

        // Print out the metrics for each strategy.
        for (IPlayerStrategy playerStrategy: playerStrategies) {
            System.out.println(playerStrategy.toString());
            System.out.println(metricMap.get(playerStrategy));
            System.out.println();
        }
    }
}