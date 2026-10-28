import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/** Saves and loads portfolio + market state in a simple CSV-like text file. */
public class DataStore {
    private final Path file;

    public DataStore(String fileName) { this.file = Paths.get(fileName); }

    public boolean exists() { return Files.exists(file); }

    public void save(Portfolio p, Market m) throws IOException {
        try (BufferedWriter w = Files.newBufferedWriter(file)) {
            w.write("OWNER," + p.getOwner() + "," + p.getStartingCash() + "," + p.getCash());
            w.newLine();
            w.write("DAY," + m.getDay());
            w.newLine();
            for (Stock s : m.all()) {
                w.write("PRICE," + s.getSymbol() + "," + s.getPrice() + "," + s.getPreviousPrice());
                w.newLine();
            }
            for (Map.Entry<String, Portfolio.Holding> e : p.getHoldings().entrySet()) {
                w.write("HOLD," + e.getKey() + "," + e.getValue().quantity + "," + e.getValue().avgCost);
                w.newLine();
            }
            for (Transaction t : p.getTransactions()) {
                w.write("TXN," + t.getDay() + "," + t.getType() + "," + t.getSymbol() + ","
                        + t.getQuantity() + "," + t.getPrice());
                w.newLine();
            }
            for (double[] h : p.getHistory()) {
                w.write("SNAP," + (int) h[0] + "," + h[1]);
                w.newLine();
            }
        }
    }

    /** Loads state into the given market and returns the restored portfolio. */
    public Portfolio load(Market m) throws IOException {
        Portfolio p = null;
        java.util.List<String> lines = Files.readAllLines(file);
        // First pass: create the portfolio
        for (String line : lines) {
            String[] f = line.split(",");
            if (f[0].equals("OWNER")) {
                p = new Portfolio(f[1], Double.parseDouble(f[2]));
                // restore cash by replaying: set via reflection-free approach below
                restoreCash(p, Double.parseDouble(f[3]));
            }
        }
        if (p == null) throw new IOException("Corrupt save file.");
        for (String line : lines) {
            String[] f = line.split(",");
            switch (f[0]) {
                case "DAY":
                    m.setDay(Integer.parseInt(f[1]));
                    break;
                case "PRICE": {
                    Stock s = m.get(f[1]);
                    if (s != null) {
                        s.setPrice(Double.parseDouble(f[3]));   // previous price
                        s.setPrice(Double.parseDouble(f[2]));   // current price
                    }
                    break;
                }
                case "HOLD":
                    p.getHoldings().put(f[1], newHolding(Integer.parseInt(f[2]), Double.parseDouble(f[3])));
                    break;
                case "TXN":
                    p.getTransactions().add(new Transaction(Integer.parseInt(f[1]),
                            Transaction.Type.valueOf(f[2]), f[3],
                            Integer.parseInt(f[4]), Double.parseDouble(f[5])));
                    break;
                case "SNAP":
                    p.getHistory().add(new double[]{Integer.parseInt(f[1]), Double.parseDouble(f[2])});
                    break;
                default:
                    break;
            }
        }
        return p;
    }

    private static Portfolio.Holding newHolding(int q, double c) {
        // Holding's constructor is package-private; DataStore is in the same package.
        return new Portfolio.Holding(q, c);
    }

    private static void restoreCash(Portfolio p, double cash) {
        p.restoreCash(cash);
    }
}
