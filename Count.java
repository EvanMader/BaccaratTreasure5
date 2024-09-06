public class Count {
    int[] counts = {0,0,0,0,0,0,0,0,0,0};
    int[] triggers = {0,0,0,4,11,7,4,2};

    int[][] tags = {
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0,0,0},
        {0,0,0,-1,-1,-1,-1,2,2,0},
        {1,1,-2,-2,-2,-1,-1,-2,4,1},
        {1,1,0,-1,-1,-1,-1,0,-2,1},
        {1,0,-1,-1,-1,0,-6,2,2,1},
        {1,0,0,-1,-1,-1,-2,0,0,1}
    };

    public void counter(Card card) {
        for (int i = 0; i < tags.length; i++) {
            counts[i] += tags[i][card.value - 1];
        }
    }

    public void reset() {
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }
    }
}
