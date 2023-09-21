import java.io.IOException;

public class HW1Main extends CSVParser {

    private static final String inputCSVFile = "HW1input_no_comments.csv";

    private static final boolean containsComments = false;

    private static final int numOtherPlayers = 3;

    private static final String outputCSVFile = "HW1output.csv";

    @Override
    protected String getOutputCSVLine(String inputCSVLine) {
        Table table = new Table(inputCSVLine, numOtherPlayers);
        PlayerHand player1Hand = table.getHandList(1).get(0);
        Decision decision = NaiveStrategy.singleton().getDecision(table, player1Hand);
        return decision.toString() + inputCSVLine;
    }

    public static void main(String[] args) throws IOException {
        HW1Main parser = new HW1Main();
        parser.computeResults(inputCSVFile, containsComments, outputCSVFile);
    }

}
