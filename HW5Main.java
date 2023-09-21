import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HW5Main extends CSVParser {

    private static final String inputCSVFile = "HW5input.csv";

    private static final boolean containsComments = true;

    private static final int numOtherPlayers = 4;

    private static final String outputCSVFile = "HW5output.csv";

    private static final List<IPlayerStrategy> playerStrategies = List.of(IdealStrategy.singleton(), WikiStrategy.singleton());

    private static final int numTrials = 100;

    private static final int roundToDecimalPlaces = 5;

    @Override
    protected String getOutputCSVLine(String inputCSVLine) {
        Map<IPlayerStrategy, MetricRecorder> metricMap = MetricComputer.computeMetrics(playerStrategies, inputCSVLine, numOtherPlayers, numTrials);
        MetricRecorder idealMetric = metricMap.get(IdealStrategy.singleton());
        MetricRecorder wikiMetric = metricMap.get(WikiStrategy.singleton());
        double idealAvgEarnings = round(idealMetric.getTotalEarnings() / numTrials, roundToDecimalPlaces);
        double wikiAvgEarnings = round(wikiMetric.getTotalEarnings() / numTrials, roundToDecimalPlaces);
        return idealAvgEarnings + "," + wikiAvgEarnings + "," + inputCSVLine.substring(2);
    }

    public static void main(String[] args) throws IOException {
        HW5Main parser = new HW5Main();
        parser.computeResults(inputCSVFile, containsComments, outputCSVFile);
    }

}
