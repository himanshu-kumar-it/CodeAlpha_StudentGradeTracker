import java.util.Random;

/** Simulated payment gateway (no real money, no real card data stored). */
public class Payment {
    private static final Random RND = new Random();

    public enum Method { CARD, UPI, CASH_AT_HOTEL }

    /**
     * Simulates processing a payment.
     * @return a payment reference number, or null if the (simulated) payment failed.
     */
    public static String process(Method method, double amount) {
        if (amount <= 0) return null;
        if (method == Method.CARD && RND.nextInt(20) == 0) return null; // 5% simulated decline
        String prefix = method == Method.CARD ? "CRD" : method == Method.UPI ? "UPI" : "CSH";
        return prefix + (100000 + RND.nextInt(900000));
    }
}
