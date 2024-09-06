import java.util.concurrent.*;

public class GameLoop implements Callable<Wallet> {
    double unit;
    int numDecks;
    int cutCardDepth;
    long iterations;
    String[] names;

    public GameLoop(double unit, int numDecks, int cutCardDepth, long iterations, String[] names) {
        this.unit = unit;
        this.numDecks = numDecks;
        this.cutCardDepth = cutCardDepth;
        this.iterations = iterations;
        this.names = names;
    }

    public Wallet call() {
        Wallet wallet = new Wallet(names, unit);
        Shoe shoe = new Shoe(numDecks, cutCardDepth, Thread.currentThread().threadId());
        Player player = new Player();
        Player banker = new Player();

        // SHOE START
        for (int iteration = 0; iteration < iterations; iteration++) {
            while (shoe.cards.size() > cutCardDepth) {
                double[] bets = setBets(shoe);

                player.draw(shoe);
                banker.draw(shoe);
                player.draw(shoe);
                banker.draw(shoe);

                if (player.total() >= 8 || banker.total() >= 8) {
                    payout(wallet, bets, player, banker);
                    continue;
                }
                Card playerCard3 = null;
                if (player.total() < 6) {
                    playerCard3 = player.draw(shoe);
                } else if (banker.total() < 6) {
                    banker.draw(shoe);
                    payout(wallet, bets, player, banker);
                    continue;
                } else {
                    payout(wallet, bets, player, banker);
                    continue;
                }

                switch (banker.total()) {
                    case 0:
                    case 1:
                    case 2:
                        banker.draw(shoe);
                        break;
                    case 3:
                        if (playerCard3.value != 8)
                            banker.draw(shoe);
                        break;
                    case 4:
                        if (playerCard3.value > 1 && playerCard3.value < 8)
                            banker.draw(shoe);
                        break;
                    case 5:
                        if (playerCard3.value > 3 && playerCard3.value < 8)
                            banker.draw(shoe);
                        break;
                    case 6:
                        if (playerCard3.value == 6 || playerCard3.value == 7)
                            banker.draw(shoe);
                        break;
                    default:
                        break;
                }

                payout(wallet, bets, player, banker);
            }
            shoe.reset();
        }
        return wallet;
    }

    private void payout(Wallet wallet, double[] bets, Player player, Player banker) {
        wallet.transaction(bets);

        // PLAYER / BANKER / TIE
        if (player.total() > banker.total()) {
            wallet.playerPayout(bets[0], 1);
            wallet.bankerPayout(bets[1], -1);
            wallet.tiePayout(bets[2], -1);
        } else if (banker.total() > player.total()) {
            wallet.playerPayout(bets[0], -1);
            wallet.bankerPayout(bets[1], 1);
            wallet.tiePayout(bets[2], -1);
        } else {
            wallet.playerPayout(bets[0], 0);
            wallet.bankerPayout(bets[1], 0);
            wallet.tiePayout(bets[2], 1);
        }

        // SIDE BETS
        boolean coverAll = false;

        // FORTUNE
        if (banker.total() > player.total() && banker.total() == 7 && banker.hand.size() == 3) {
            wallet.fortunePayout(bets[3], 1);
            coverAll = true;
        } else
            wallet.fortunePayout(bets[3], -1);

        // GOLDEN
        if (banker.total() < player.total() && player.total() == 8 && player.hand.size() == 3) {
            wallet.goldenPayout(bets[4], 1);
            coverAll = true;
        } else
            wallet.goldenPayout(bets[4], -1);

        // HEAVENLY
        if (banker.total() == 9 && player.total() == 9 && banker.hand.size() == 3 && player.hand.size() == 3) {
            wallet.heavenlyPayout(bets[5], 1, true);
            coverAll = true;
        } else if ((banker.total() == 9 && banker.hand.size() == 3)
                || (player.total() == 9 && player.hand.size() == 3)) {
            wallet.heavenlyPayout(bets[5], 1, false);
            coverAll = true;
        } else
            wallet.heavenlyPayout(bets[5], -1, false);

        // BLAZING
        if (banker.total() == 7 && player.total() == 7 && banker.hand.size() == 3 && player.hand.size() == 3) {
            wallet.blazingPayout(bets[6], 1, true);
            coverAll = true;
        } else if (banker.total() == 7 && player.total() == 7 && banker.hand.size() == 2 && player.hand.size() == 2) {
            wallet.blazingPayout(bets[6], 1, false);
            coverAll = true;
        } else
            wallet.blazingPayout(bets[6], - 1, false);

        // COVERALL
        if (coverAll)
            wallet.coverAllPayout(bets[7], 1);
        else
            wallet.coverAllPayout(bets[7], -1);
        
        player.hand.clear();
        banker.hand.clear();
    }

    private double[] setBets(Shoe shoe) {
        double[] bets = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
        for (int i = 0; i < bets.length; i++) {
            if (shoe.count.counts[i] / shoe.decksRemanining >= shoe.count.triggers[i]) {
                bets[i] = 1.0;
            } else {
                bets[i] = 0.0;
            }
        }
        return bets;
    }
}