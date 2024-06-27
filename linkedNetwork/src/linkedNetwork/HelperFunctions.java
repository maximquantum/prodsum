package linkedNetwork;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HelperFunctions {

    public static Set<Integer> generatePrimes(int maxN) {
        Set<Integer> primes = new HashSet<>();
        boolean[] isPrime = new boolean[maxN + 1];
        Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;
        for (int i = 2; i * i <= maxN; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j <= maxN; j += i) {
                    isPrime[j] = false;
                }
            }
        }
        for (int i = 2; i <= maxN; i++) {
            if (isPrime[i]) {
                primes.add(i);
            }
        }
        return primes;
    }

    public static boolean isPrimeSquare(int num, Set<Integer> primes) {
        int sqrt = (int) Math.sqrt(num);
        return sqrt * sqrt == num && primes.contains(sqrt);
    }

    public static Set<Pair> pairsFromSum(int sum, int minN, int maxN, int difference) {
        Set<Pair> pairs = new HashSet<>();
        for (int i = minN; i <= sum / 2; i++) {
            int j = sum - i;
            if (i < j && j <= maxN) {
                pairs.add(new Pair(i, j));
            }
        }
        return pairs;
    }

    public static Set<Pair> pairsFromProduct(int product, int minN, int maxN, int difference) {
        Set<Pair> pairs = new HashSet<>();
        for (int i = minN; i * i <= product; i++) {
            if (product % i == 0) {
                int j = product / i;
                if (i < j && j <= maxN) {
                    pairs.add(new Pair(i, j));
                }
            }
        }
        return pairs;
    }

	public static boolean isPrime(int num, Set<Integer> primes) {
        return primes.contains(num);
	}
}