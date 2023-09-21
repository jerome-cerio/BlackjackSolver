/**
 * An object which can keep track of multiple metrics for a strategy.
 */
public class MetricRecorder {

    /**
     * The total earnings by this strategy so far.
     */
    private double totalEarnings;

    /**
     * The maximum gain by this strategy over its initial amount of money (0).
     * This is equal to the maximum value of totalEarnings over all time.
     */
    private double maxEarnings;

    /**
     * The minimum gain by this strategy over its initial amount of money (0).
     * This is equal to the minimum value of totalEarnings over all time.
     */
    private double minEarnings;

    /**
     * Initializes the total earnings, maximum gain, and minimum gain to 0.
     */
    public MetricRecorder() {
        this.totalEarnings = this.maxEarnings = this.minEarnings = 0.0;
    }

    /**
     * Updates each of the metrics based on the input amount of money
     * that was earned on the most recent hand.
     *
     * @param earnings the amount of money earned on the most recent hand
     */
    public void updateEarnings(double earnings) {
        this.totalEarnings += earnings;
        this.maxEarnings = Math.max(this.maxEarnings, this.totalEarnings);
        this.minEarnings = Math.min(this.minEarnings, this.totalEarnings);
    }

    /**
     * Returns the total earnings of the strategy being recorded.
     *
     * @return the total earnings of the strategy being recorded
     */
    public double getTotalEarnings() {
        return this.totalEarnings;
    }

    @Override
    public String toString() {
        return "Total Earnings: " + totalEarnings
                + "\nMax Earnings: " + maxEarnings
                + "\nMin Earnings: " + minEarnings;
    }

}