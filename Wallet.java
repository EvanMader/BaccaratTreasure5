import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;

public class Wallet {
    double[] money = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    double[] transactions = {0l,0l,0l,0l,0l,0l,0l,0l};
    double[] means = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    double [] m2 = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    double[] variances = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
    String[] names;
    public long totalTransactions = 0;
    double unit;

    public Wallet(String[] names, double unit) {
        this.names = names;
        this.unit = unit;
    }

    public void playerPayout(double bet, int win) {
        if (bet == 0) return;
        double update = win*bet;
        money[0] += update;
        updateVariance(update, 0);
    }
    public void bankerPayout(double bet, int win) {
        if (bet == 0) return;
        double update = Math.min(win*bet*0.95, win*bet);
        money [1] += update;
        updateVariance(update, 1);
    }
    public void tiePayout(double bet, int win) {
        if (bet == 0) return;
        double update = Math.max(win*bet*8, win*bet);
        money[2] += update;
        updateVariance(update, 2);
    }
    public void fortunePayout(double bet, int win) {
        if (bet == 0) return;
        double update = Math.max(win*bet*40, win*bet);
        money[3] += update;
        updateVariance(update, 3);
    }
    public void goldenPayout(double bet, int win) {
        if (bet == 0) return;
        double update = Math.max(win*bet*25, win*bet);
        money[4] += update;
        updateVariance(update, 4);
    }
    public void heavenlyPayout(double bet, int win, boolean big) {
        if (bet == 0) return;
        double update = 0;
        if (win == -1) update -= bet;
        else if (big) update += bet * 75;
        else update += bet * 10;
        money[5] += update;
        updateVariance(update, 5);
    }
    public void blazingPayout(double bet, int win, boolean big) {
        if (bet == 0) return;
        double update = 0;
        if (win == -1) update -= bet;
        else if (big) update += bet * 200;
        else update += bet * 50;
        money[6] += update;
        updateVariance(update, 6);
    }
    public void coverAllPayout(double bet, int win) {
        if (bet == 0) return;
        double update = Math.max(win*bet*6, win*bet);
        money[7] += update;
        updateVariance(update, 7);
    }

    private void updateVariance(double x, int index) {
        double oldMean = means[index];
        means[index] += (x - means[index])/transactions[index];
        m2[index] += (x - means[index]) * (x - oldMean);
        variances[index] = m2[index] / transactions[index];
    }

    public void transaction(double[] bets) {
        totalTransactions ++;
        for (int i = 0; i < transactions.length; i++) {
            if (bets[i] != 0.0) transactions[i] ++;
        }
    }

    public void add(Wallet wallet) {
        totalTransactions += wallet.totalTransactions;
        for (int i = 0; i < money.length; i++) {
            if (transactions[i] + wallet.transactions[i] > 0) {
                m2[i] += wallet.m2[i] + ((transactions[i] * wallet.transactions[i] * Math.pow(means[i] - wallet.means[i], 2)) / (transactions[i] + wallet.transactions[i]));
                variances[i] = combinedVariance(variances[i], transactions[i], means[i], wallet.variances[i], wallet.transactions[i], wallet.means[i]);
                means[i] = combinedMean(means[i], transactions[i], wallet.means[i], wallet.transactions[i]);
            }
            money[i] += wallet.money[i];
            transactions[i] += wallet.transactions[i];
        }
    }

    private double combinedMean(double mean1, double n1, double mean2, double n2) {
        return ((n1 * mean1) + (n2 * mean2)) / (n1+n2);
    }
    private double combinedVariance(double variance1, double n1, double mean1, double variance2, double n2, double mean2) {
        double xBar = combinedMean(mean1, n1, mean2, n2);
        double d1 = Math.pow(mean1 - xBar, 2);
        double d2 = Math.pow(mean2 - xBar, 2);
        return ((n1 * (variance1 + d1)) + (n2 * (variance2 + d2))) / (n1+n2);
    }

    public void printEdge() {
        DecimalFormat formatter = new DecimalFormat("0.0000%");
        System.out.println();
        System.out.println("Gambling Edge %:");
        for (int i = 0; i < money.length; i++) {
            System.out.println(names[i] + ": " + formatter.format(money[i] / transactions[i]));
        }
    }

    public void printFrequency() {
        DecimalFormat formatter = new DecimalFormat("0.0000%");
        System.out.println();
        System.out.println("Bet Frequency %:");
        for (int i = 0; i < transactions.length; i++) {
            System.out.println(names[i] + ": " + formatter.format((double) transactions[i] / totalTransactions));
        }
    }

    public void printHands() {
        DecimalFormat formatter = new DecimalFormat("#,###");
        System.out.println();
        System.out.println("Number of Hands Played: ");
        for (int i = 0; i < transactions.length; i++) {
            System.out.println(names[i] + ": " + formatter.format(transactions[i]));
        }
    }
    
    public void printUnitsPer(long iterations, int handsPerHour) {
        DecimalFormat formatter = new DecimalFormat("#,##0.0000");
        System.out.println();
        System.out.printf("%-12s | %-9s | %-12s | %-12s\n", "Units per", "100", "Shoe", "Hour");
        for (int i = 0; i < money.length; i++) {
            double unitsPer = (money[i] / totalTransactions);
            String units = formatter.format(unitsPer * 100);
            String shoe = formatter.format(unitsPer * iterations);
            String hour = formatter.format(unitsPer * handsPerHour);
            System.out.printf("%-12s | %-9s | %-12s | %-12s\n", names[i], units, shoe, hour);
        } 
    }

    public void printHourlyRate(int handsPerHour) {
        DecimalFormat formatter = new DecimalFormat("$#,##0.0000");
        System.out.println();
        System.out.println("Profit / Person:");
        double[] rates = new double[money.length];
        for (int i = 0; i < money.length; i++) {
            double rate = handsPerHour * money[i] / totalTransactions;
            rates[i] = rate;
        }

        Arrays.sort(rates);
        double rate = 0;
        for (int i = money.length - 1; i > 2; i--) {
            rate += rates[i];
            System.out.print("Team of " + (8-i) + ": " + formatter.format(unit * (rate + rates[2])) + "/hour | ");
            System.out.println(formatter.format((rate + rates[2]) * unit * 2080) + "/Year");
        }
    }

    public void printStats() {
        DecimalFormat formatter = new DecimalFormat("#,##0.0000");
        System.out.println();
        System.out.printf("%-11s | %-8s | %-8s | %-8s\n", "Bet", "Mean", "Variance", "Standard Deviation");
        for (int i = 0; i < variances.length; i++) {
            String mean = formatter.format(means[i]);
            String var = formatter.format(variances[i]);
            String std = formatter.format(Math.sqrt(variances[i]));
            System.out.printf("%-11s | %-8s | %-8s | %-8s", names[i], mean, var, std);
            System.out.println();
        }
    }

    public void printBankroll(double risk) {
        DecimalFormat formatter = new DecimalFormat("#,##0.0000");
        System.out.println();
        System.out.println("Bankroll Info " + risk*100 + "% ROR:");
        double[][] rates = {means, variances, transactions};

        double[][] transposedRates = new double[rates[0].length][rates.length];
        for (int i = 0; i < rates[0].length; i++) {
            for (int j = 0; j < rates.length; j++) {
                transposedRates[i][j] = rates[j][i];
            }
        }
        Arrays.sort(transposedRates, Comparator.comparingDouble(a -> a[0] * a[2]));
        for (int i = 0; i < rates.length; i++) {
            for (int j = 0; j < rates[0].length; j++) {
                rates[i][j] = transposedRates[j][i];
            }
        }

        double edge = rates[0][2];
        double variance = rates[1][2];
        double numTransactions = rates[2][2];
        for (int i = money.length - 1; i > 2; i--) {
            variance = combinedVariance(variance, numTransactions, edge, rates[1][i], rates[0][i], rates[0][i]);
            edge = combinedMean(edge, numTransactions, rates[0][i], rates[2][i]);
            System.out.print("Team of " + (8-i) + ": " + formatter.format(unit * (((variance / (2 * edge)) * Math.log(1/risk))*(8-i))));
            System.out.print(" | " + formatter.format(unit * ((variance / (2 * edge)) * Math.log(1/risk))) + "/person");
            System.out.println();
        }
    }
}