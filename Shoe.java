import java.util.*;

public class Shoe {
    public int numDecks;
    public double decksRemanining;
    public int cutCardDepth;
    public ArrayList<Card> cards;
    private Random _random;
    public Count count = new Count();

    public Shoe(int numDecks, int cutCardDepth,  long threadID) {
        this.numDecks = numDecks;
        this.decksRemanining = numDecks;
        this.cutCardDepth = cutCardDepth;
        this.cards = new ArrayList<>(numDecks*52);
        this._random = new Random(System.currentTimeMillis() + threadID);
        
        reset();
    }

    public void reset() {
        this.cards.clear();
        for (int i = 0; i < this.numDecks * 52; i++) {
            this.cards.add(new Card(i%4, ((i/4) % 13) + 1));
        }
        Collections.shuffle(this.cards, _random);
        count.reset();
    }

    public Card draw() {
        Card card = cards.remove(this.cards.size() - 1);
        count.counter(card);
        decksRemanining = cards.size()/52.0;
        return card;
    }
}
