/** A single buy or sell record. */
public class Transaction {
    public enum Type { BUY, SELL }

    private final int day;
    private final Type type;
    private final String symbol;
    private final int quantity;
    private final double price;

    public Transaction(int day, Type type, String symbol, int quantity, double price) {
        this.day = day;
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
    }

    public int getDay() { return day; }
    public Type getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return quantity * price; }

    @Override
    public String toString() {
        return String.format("Day %-3d %-4s %-6s qty=%-4d @ %9.2f  total=%11.2f",
                day, type, symbol, quantity, price, getTotal());
    }
}
