package linkedNetwork;

public class StepsToSolve {

    public static final int maxS = 5;

    public static String stepsToSolve(Pair pair, int minN, int maxN, int difference, int depth) {
        if (depth > maxS) {
            return "X"; // Return "X" to indicate it exceeded the limit
        }

        int x = pair.first;
        int y = pair.second;

        if (x == 1 && (HelperFunctions.isPrime(y) || HelperFunctions.isPrimeSquare(y))) {
            return "1";
        } else {
            int S = x + y;
            int P = x * y;

            // sum path
            int sumSteps = Integer.MAX_VALUE;
            for (Pair sumPair : HelperFunctions.pairsFromSum(S, minN, maxN, difference)) {
                if (!sumPair.equals(pair)) {
                    String steps = stepsToSolve(sumPair, minN, maxN, difference, depth + 1);
                    if (!steps.equals("X")) { // Only consider valid steps
                        sumSteps = Math.min(sumSteps, Integer.parseInt(steps) + 1);
                    } else {
                        sumSteps = Integer.MAX_VALUE;
                        break;
                    }
                }
            }

            // product path
            int productSteps = Integer.MAX_VALUE;
            for (Pair productPair : HelperFunctions.pairsFromProduct(P, minN, maxN, difference)) {
                if (!productPair.equals(pair)) {
                    String steps = stepsToSolve(productPair, minN, maxN, difference, depth + 1);
                    if (!steps.equals("X")) { // Only consider valid steps
                        productSteps = Math.min(productSteps, Integer.parseInt(steps) + 1);
                    } else {
                        productSteps = Integer.MAX_VALUE;
                        break;
                    }
                }
            }

            // Handle case where both sumSteps and productSteps are Integer.MAX_VALUE
            if (sumSteps == Integer.MAX_VALUE && productSteps == Integer.MAX_VALUE) {
                return "X";
            } else {
                return String.valueOf(Math.min(sumSteps, productSteps));
            }
        }
    }
}