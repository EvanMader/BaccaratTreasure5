import java.util.*;

public class Player {
    public ArrayList<Card> hand = new ArrayList<>(3); // set the initial capacity to 3
    
    public Card draw(Shoe shoe) {
        Card card = shoe.draw();
        hand.add(card);
        return card;
    }

    public int total() {
        int total = 0;
        for (int i = 0; i < hand.size(); i++) {
            total += hand.get(i).value;
        }
        return total % 10;
    }
    
    public String handToString() {
        String string = "";
        for (int i = 0; i < hand.size(); i++) {
            string += hand.get(i).value;
        }
        return string;
    }
}