import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/** Holds all stocks and simulates market movement day by day. */
public class Market {
    private final Map<String, Stock> stocks = new LinkedHashMap<>();
    private final Random rnd = new Random();
    private int day = 1;

    public Market() {
        add(new Stock("TCS", "Tata Consultancy Services", 3800.00));
        add(new Stock("INFY", "Infosys Ltd", 1550.00));
        add(new Stock("RELI", "Reliance Industries", 2900.00));
        add(new Stock("HDFC", "HDFC Bank", 1650.00));
        add(new Stock("WIPRO", "Wipro Ltd", 480.00));
        add(new Stock("ITC", "ITC Ltd", 430.00));
    }

    private void add(Stock s) { stocks.put(s.getSymbol(), s); }

    public Stock get(String symbol) { return stocks.get(symbol.toUpperCase()); }
    public Iterable<Stock> all() { return stocks.values(); }
    public int getDay() { return day; }
    public void setDay(int d) { this.day = d; }

    public void nextDay() {
        day++;
        for (Stock s : stocks.values()) s.simulateMove(rnd);
    }

    public void display() {
        System.out.println("\n=============== MARKET - Day " + day + " ===============");
        System.out.printf("%-7s %-28s %10s %8s%n", "Symbol", "Company", "Price", "Change");
        System.out.println("--------------------------------------------------------");
        for (Stock s : stocks.values()) {
            System.out.printf("%-7s %-28s %10.2f %+7.2f%%%n",
                    s.getSymbol(), s.getName(), s.getPrice(), s.changePercent());
        }
    }
}
