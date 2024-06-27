package linkedNetwork;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GraphVisualization {
    public static void main(String[] args) {
        for (int k = 1; k <= 20; k++) {
            // Define the range for P and S nodes
            int difference = 1;
            int minN = 1;
            int maxN = k;

            Set<Integer> primes = HelperFunctions.generatePrimes(maxN);
            Set<Pair> rootPairs = new HashSet<>();

            for (int p : primes) {
                rootPairs.add(new Pair(1, p));
            }
            for (int p : primes) {
                int pSquare = p * p;
                if (pSquare <= maxN) {
                    rootPairs.add(new Pair(1, pSquare));
                }
            }

            Set<Pair> s4Pairs = new HashSet<>();
            for (Pair rootPair : rootPairs) {
                findS4Pairs(rootPair, 0, s4Pairs, minN, maxN, difference);
            }

            // Export the table as a CSV file
            try (FileWriter csvWriter = new FileWriter(String.format("pairs-N=%s.csv", maxN))) {
                csvWriter.append("x,y,s\n");
                for (Pair pair : s4Pairs) {
                    String steps = StepsToSolve.stepsToSolve(pair, minN, maxN, difference, 0);
                    if ("4".equals(steps)) {
                        csvWriter.append(String.format("%d,%d,%s\n", pair.first, pair.second, steps));
                    } else {
                        csvWriter.append(String.format("%d,%d,X\n", pair.first, pair.second));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void findS4Pairs(Pair pair, int depth, Set<Pair> s4Pairs, int minN, int maxN, int difference) {
        if (depth == 3) {
            s4Pairs.add(pair);
            return;
        }

        int S = pair.first + pair.second;
        int P = pair.first * pair.second;

        for (Pair sumPair : HelperFunctions.pairsFromSum(S, minN, maxN, difference)) {
            findS4Pairs(sumPair, depth + 1, s4Pairs, minN, maxN, difference);
        }

        for (Pair productPair : HelperFunctions.pairsFromProduct(P, minN, maxN, difference)) {
            findS4Pairs(productPair, depth + 1, s4Pairs, minN, maxN, difference);
        }
    }
}