import java.io.*;

/**
 * An abstract class capable of reading each line from an input CSV file,
 * converting them to output lines of CSV according to the abstract
 * method getOutputCSVLine(), and writing the output lines of CSV
 * to an output file.
 */
public abstract class CSVParser {

    /**
     * A complete file path to the folder of csv files. This will work
     * on anyone's machine since System.getProperty("user.dir") locates
     * the path of this repository on their machine.
     */
    private static final String csvDir = System.getProperty("user.dir") + File.separator +
                                                            "src" + File.separator +
                                                            "csv";

    /**
     * Reads each line from the input CSV file, processes the line according to the abstract
     * method getOutputCSVLine(), and writes each output CSV line to the specified output file.
     *
     * @param inputCSVFile a file containing lines of CSV, with cards represented in unicode
     * @param containsComments indicates whether the input CSV file contains comments on every other line
     * @param outputCSVFile the output file to write the converted CSV lines to
     */
    protected void computeResults(String inputCSVFile, boolean containsComments, String outputCSVFile) throws IOException {

        // Reads from the input CSV file.
        BufferedReader reader = new BufferedReader(new FileReader(csvDir + File.separator + inputCSVFile));

        // Writes to the output CSV file.
        BufferedWriter writer = new BufferedWriter(new FileWriter(csvDir + File.separator + outputCSVFile));

        // Converts each input CSV line to an output CSV line, which is then written to the output file.
        String inputCSVLine;
        while ((inputCSVLine = reader.readLine()) != null) {
            writer.write(this.getOutputCSVLine(inputCSVLine) + "\n");
            if (containsComments) {
                reader.readLine();
            }
        }

        // Flush any remaining buffered reads and writes.
        reader.close();
        writer.close();
    }

    /**
     * Processes the input CSV line.
     *
     * @param inputCSVLine a line of CSV containing card unicodes
     * @return the output line of CSV after the input CSV is processed
     */
    protected abstract String getOutputCSVLine(String inputCSVLine);

    /**
     * Rounds the input number to the number of decimal places.
     *
     * @param num a number
     * @param decimalPlaces the number of decimal places to round the number to
     * @return the input number rounded
     */
    protected static double round(double num, int decimalPlaces) {
        return Math.round(num * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
    }

}
