package linkedNetwork;

import java.util.HashSet;
import java.util.Set;

class PairVectorSpace {
    public static Set<Pair> generatePairs(int minN, int maxN, int difference, int proportion) {
        Set<Pair> pairs = new HashSet<>();
        for (int i = minN; i <= maxN; i++) {
            for (int j = Math.max(i + difference, proportion * i); j <= maxN; j++) {
                pairs.add(new Pair(i, j));
            }
        }
        return pairs;
    }
}