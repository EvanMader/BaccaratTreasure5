import java.text.DecimalFormat;
import java.util.concurrent.*;

public class Main {
    public static void main (String args[]) throws Exception {
        String[] names = {"Player", "Banker", "Tie", "Fortune 7", "Golden 8", "Heavenly 9", "Blazing 7's", "Cover All"};
        double unit = 25.00;
        int numDecks = 8;
        int cutCardDepth = 14;
        long iterations = 100_000_000;
        int threads = 12;
        int handsPerHour = 72;
        double risk = .01;

        DecimalFormat formatter = new DecimalFormat("#,###");
        DecimalFormat doubleFormatter = new DecimalFormat("#.0000");
        Long start = System.currentTimeMillis();
        System.out.println("Current Time: " + java.time.LocalTime.now());
        System.out.println("Expected Excecution Time:");
        System.out.println((int) (0.00006465 * iterations) / (60 * threads) + " Minutes " + ((0.00006465 * (iterations/threads))) % 60 + " Seconds");
        System.out.println();
    
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CompletionService<Wallet> completionService = new ExecutorCompletionService<Wallet>(pool);

        for (int i = 0; i < threads; i++) {
            Callable<Wallet> gameLoop = new GameLoop(unit, numDecks, cutCardDepth, iterations/threads, names);
            completionService.submit(gameLoop);
        }

        Wallet bigWallet = new Wallet(names, unit);
        int recieved = 0;
        while (recieved < threads) {
            Future<Wallet> resultFuture = completionService.take();
            bigWallet.add(resultFuture.get());
            recieved ++;
        }
        pool.shutdown();

        // PRINT STATS
        System.out.println("Total hands: " + formatter.format(bigWallet.totalTransactions));
        System.out.println("Shoes: " + formatter.format(iterations - (iterations%threads)));
        System.out.println("Cut Card Depth: " + cutCardDepth);
        System.out.println("Number of Decks: " + numDecks);
        System.out.println("Avg hands per shoe: " + doubleFormatter.format(bigWallet.totalTransactions * 1.0 / (iterations -(iterations%threads))));
        System.out.println("Unit Size: " + unit);
        bigWallet.printEdge();
        bigWallet.printFrequency();
        bigWallet.printHands();
        bigWallet.printUnitsPer(iterations/threads, handsPerHour);
        bigWallet.printHourlyRate(handsPerHour);
        bigWallet.printStats();
        bigWallet.printBankroll(risk);
        System.out.println();
        System.out.println("Program Excecution Time: " + ((System.currentTimeMillis() - start) / 60000) + " Minutes " + ((System.currentTimeMillis() - start) / 1000.0) % 60 + " Seconds");
    }
}