package linkedNetwork;

import java.util.HashSet;
import java.util.Set;

public class HelperFunctions {

    public static Set<Pair> pairsFromSum(int sum, int minN, int maxN, int difference) {
        Set<Pair> pairs = new HashSet<>();
        for (int i = minN; i <= maxN; i++) {
            for (int j = i + difference; j <= maxN; j++) {
                if (i + j == sum) {
                    pairs.add(new Pair(i, j));
                }
            }
        }
        return pairs;
    }

    public static Set<Pair> pairsFromProduct(int product, int minN, int maxN, int difference) {
        Set<Pair> pairs = new HashSet<>();
        for (int i = minN; i <= maxN; i++) {
            for (int j = i + difference; j <= maxN; j++) {
                if (i * j == product) {
                    pairs.add(new Pair(i, j));
                }
            }
        }
        return pairs;
    }

    public static boolean isPrime(int num) {
        if (num <= 1) return false;
        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) return false;
        }
        return true;
    }

    public static boolean isPrimeSquare(int num) {
        int sqrt = (int) Math.sqrt(num);
        return sqrt * sqrt == num && isPrime(sqrt);
    }
}