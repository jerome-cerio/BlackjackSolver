import java.io.IOException;

public class HW2Main extends CSVParser {

    private static final String inputCSVFile = "HW2input.csv";

    private static final boolean containsComments = true;

    private static final int numOtherPlayers = 3;

    private static final String outputCSVFile = "HW2output.csv";

    @Override
    protected String getOutputCSVLine(String inputCSVLine) {
        Table table = new Table(inputCSVLine, numOtherPlayers);
        PlayerHand player1Hand = table.getHandList(1).get(0);
        Decision decision = WikiStrategy.singleton().getDecision(table, player1Hand);
        return decision.toString() + inputCSVLine;
    }

    public static void main(String[] args) throws IOException {
        HW2Main parser = new HW2Main();
        parser.computeResults(inputCSVFile, containsComments, outputCSVFile);
    }

}