import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HW4Main extends CSVParser {

    private static final String inputCSVFile = "HW4input.csv";

    private static final boolean containsComments = true;

    private static final int numOtherPlayers = 4;

    private static final String outputCSVFile = "dummy.csv";

    private static final List<IPlayerStrategy> playerStrategies = List.of(NaiveStrategy.singleton(), WikiStrategy.singleton());

    private static final int numTrials = 1_000_000;

    private static final int roundToDecimalPlaces = 5;

    @Override
    protected String getOutputCSVLine(String inputCSVLine) {
        Map<IPlayerStrategy, MetricRecorder> metricMap = MetricComputer.computeMetrics(playerStrategies, inputCSVLine, numOtherPlayers, numTrials);
        MetricRecorder naiveMetric = metricMap.get(NaiveStrategy.singleton());
        MetricRecorder wikiMetric = metricMap.get(WikiStrategy.singleton());
        double naiveAvgEarnings = round(naiveMetric.getTotalEarnings() / numTrials, roundToDecimalPlaces);
        double wikiAvgEarnings = round(wikiMetric.getTotalEarnings() / numTrials, roundToDecimalPlaces);
        return naiveAvgEarnings + "," + wikiAvgEarnings + "," + inputCSVLine.substring(2);
    }

    public static void main(String[] args) throws IOException {
        HW4Main parser = new HW4Main();
        parser.computeResults(inputCSVFile, containsComments, outputCSVFile);
    }
}
