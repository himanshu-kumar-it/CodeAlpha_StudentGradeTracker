import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** A user's cash, holdings, transaction log and value history. */
public class Portfolio {

    /** Quantity and average buy cost of one stock. */
    public static class Holding {
        public int quantity;
        public double avgCost;
        Holding(int q, double c) { quantity = q; avgCost = c; }
    }

    private final String owner;
    private final double startingCash;
    private double cash;
    private final Map<String, Holding> holdings = new LinkedHashMap<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<double[]> history = new ArrayList<>(); // {day, totalValue}

    public Portfolio(String owner, double startingCash) {
        this.owner = owner;
        this.startingCash = startingCash;
        this.cash = startingCash;
    }

    public String getOwner() { return owner; }
    public double getCash() { return cash; }
    public double getStartingCash() { return startingCash; }
    public Map<String, Holding> getHoldings() { return holdings; }
    public List<Transaction> getTransactions() { return transactions; }
    public List<double[]> getHistory() { return history; }

    /** Used by DataStore when loading a saved portfolio. */
    void restoreCash(double c) { this.cash = c; }

    /** @return null on success, otherwise an error message. */
    public String buy(Stock s, int qty, int day) {
        if (qty <= 0) return "Quantity must be positive.";
        double cost = qty * s.getPrice();
        if (cost > cash) return String.format("Insufficient funds. Need %.2f, have %.2f.", cost, cash);
        cash -= cost;
        Holding h = holdings.get(s.getSymbol());
        if (h == null) {
            holdings.put(s.getSymbol(), new Holding(qty, s.getPrice()));
        } else {
            h.avgCost = (h.avgCost * h.quantity + cost) / (h.quantity + qty);
            h.quantity += qty;
        }
        transactions.add(new Transaction(day, Transaction.Type.BUY, s.getSymbol(), qty, s.getPrice()));
        return null;
    }

    /** @return null on success, otherwise an error message. */
    public String sell(Stock s, int qty, int day) {
        if (qty <= 0) return "Quantity must be positive.";
        Holding h = holdings.get(s.getSymbol());
        if (h == null || h.quantity < qty) {
            return "You do not own enough shares of " + s.getSymbol() + ".";
        }
        cash += qty * s.getPrice();
        h.quantity -= qty;
        if (h.quantity == 0) holdings.remove(s.getSymbol());
        transactions.add(new Transaction(day, Transaction.Type.SELL, s.getSymbol(), qty, s.getPrice()));
        return null;
    }

    public double holdingsValue(Market m) {
        double v = 0;
        for (Map.Entry<String, Holding> e : holdings.entrySet()) {
            v += e.getValue().quantity * m.get(e.getKey()).getPrice();
        }
        return v;
    }

    public double totalValue(Market m) { return cash + holdingsValue(m); }

    /** Record a snapshot of total value for the given day (replaces same-day snapshot). */
    public void snapshot(Market m) {
        int day = m.getDay();
        if (!history.isEmpty() && (int) history.get(history.size() - 1)[0] == day) {
            history.remove(history.size() - 1);
        }
        history.add(new double[]{day, totalValue(m)});
    }

    public void display(Market m) {
        System.out.println("\n=============== PORTFOLIO: " + owner + " ===============");
        System.out.printf("%-7s %5s %10s %10s %12s %12s%n",
                "Symbol", "Qty", "AvgCost", "Price", "Value", "P/L");
        System.out.println("------------------------------------------------------------------");
        if (holdings.isEmpty()) System.out.println("(no holdings)");
        for (Map.Entry<String, Holding> e : holdings.entrySet()) {
            Holding h = e.getValue();
            double price = m.get(e.getKey()).getPrice();
            double value = h.quantity * price;
            double pl = value - h.quantity * h.avgCost;
            System.out.printf("%-7s %5d %10.2f %10.2f %12.2f %+12.2f%n",
                    e.getKey(), h.quantity, h.avgCost, price, value, pl);
        }
        System.out.println("------------------------------------------------------------------");
        double total = totalValue(m);
        System.out.printf("Cash           : %12.2f%n", cash);
        System.out.printf("Holdings value : %12.2f%n", holdingsValue(m));
        System.out.printf("Total value    : %12.2f%n", total);
        System.out.printf("Overall P/L    : %+12.2f (%+.2f%%)%n",
                total - startingCash, (total - startingCash) / startingCash * 100.0);
    }
}
