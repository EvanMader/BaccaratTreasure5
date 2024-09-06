public class Card {
    public int suit;
    public int value;

    public Card(int suit, int value) {
        this.suit = suit;
        this.value = Math.min(value, 10);
    }

    public String toString() {
        return "Suit: " + suit + " Value: " + value;
    }
}
