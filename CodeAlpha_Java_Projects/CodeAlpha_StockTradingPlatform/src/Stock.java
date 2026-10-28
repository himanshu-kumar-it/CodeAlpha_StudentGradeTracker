import java.util.Random;

/** A tradable stock with a current price that can fluctuate. */
public class Stock {
    private final String symbol;
    private final String name;
    private double price;
    private double previousPrice;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
        this.previousPrice = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public double getPreviousPrice() { return previousPrice; }

    public void setPrice(double p) {
        this.previousPrice = this.price;
        this.price = p;
    }

    public double changePercent() {
        return previousPrice == 0 ? 0 : (price - previousPrice) / previousPrice * 100.0;
    }

    /** Random daily move between roughly -5% and +5%. */
    public void simulateMove(Random rnd) {
        double pct = (rnd.nextDouble() * 10.0 - 5.0) / 100.0;
        double newPrice = Math.max(1.0, price * (1 + pct));
        setPrice(Math.round(newPrice * 100.0) / 100.0);
    }
}
