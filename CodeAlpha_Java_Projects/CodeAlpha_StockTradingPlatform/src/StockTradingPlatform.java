import java.io.IOException;
import java.util.Scanner;

/**
 * CodeAlpha Java Internship - Task 2: Stock Trading Platform (console simulation).
 * Features: market data display, buy/sell, portfolio tracking over time,
 * OOP design (Stock, Market, Portfolio, Transaction) and file persistence.
 */
public class StockTradingPlatform {
    private static final String SAVE_FILE = "portfolio_data.txt";
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Market market = new Market();
        DataStore store = new DataStore(SAVE_FILE);
        Portfolio portfolio = null;

        System.out.println("=== Stock Trading Platform (CodeAlpha) ===");
        if (store.exists()) {
            String ans = ask("Saved portfolio found. Load it? (y/n): ");
            if (ans.equalsIgnoreCase("y")) {
                try {
                    portfolio = store.load(market);
                    System.out.println("Welcome back, " + portfolio.getOwner() + "!");
                } catch (IOException | RuntimeException e) {
                    System.out.println("Could not load save file: " + e.getMessage());
                }
            }
        }
        if (portfolio == null) {
            String name = ask("Enter your name: ");
            if (name.isEmpty()) name = "Trader";
            portfolio = new Portfolio(name, 100000.00);
            System.out.println("Account created with starting balance of 100000.00");
        }
        portfolio.snapshot(market);

        boolean running = true;
        while (running) {
            menu();
            int c = readInt("Choice: ");
            switch (c) {
                case 1: market.display(); break;
                case 2: trade(market, portfolio, true); break;
                case 3: trade(market, portfolio, false); break;
                case 4: portfolio.display(market); break;
                case 5:
                    market.nextDay();
                    portfolio.snapshot(market);
                    System.out.println("Market moved to day " + market.getDay() + ".");
                    market.display();
                    break;
                case 6: showHistory(portfolio); break;
                case 7: showTransactions(portfolio); break;
                case 8: save(store, portfolio, market); break;
                case 9:
                    save(store, portfolio, market);
                    running = false;
                    break;
                default: System.out.println("Invalid choice.");
            }
        }
        System.out.println("Thanks for trading. Goodbye!");
    }

    private static void menu() {
        System.out.println("\n------------- MENU -------------");
        System.out.println("1. View market data");
        System.out.println("2. Buy stock");
        System.out.println("3. Sell stock");
        System.out.println("4. View portfolio");
        System.out.println("5. Advance to next day (simulate market)");
        System.out.println("6. Portfolio performance history");
        System.out.println("7. Transaction history");
        System.out.println("8. Save");
        System.out.println("9. Save and exit");
    }

    private static void trade(Market m, Portfolio p, boolean buy) {
        m.display();
        String sym = ask("Symbol: ");
        Stock s = m.get(sym);
        if (s == null) { System.out.println("Unknown symbol."); return; }
        int qty = readInt("Quantity: ");
        String err = buy ? p.buy(s, qty, m.getDay()) : p.sell(s, qty, m.getDay());
        if (err != null) {
            System.out.println("Failed: " + err);
        } else {
            System.out.printf("%s %d x %s @ %.2f done. Cash left: %.2f%n",
                    buy ? "Bought" : "Sold", qty, s.getSymbol(), s.getPrice(), p.getCash());
            p.snapshot(m);
        }
    }

    private static void showHistory(Portfolio p) {
        System.out.println("\n--- Portfolio value over time ---");
        System.out.printf("%-6s %14s %12s%n", "Day", "Total value", "Change");
        double prev = -1;
        for (double[] h : p.getHistory()) {
            String chg = prev < 0 ? "-" : String.format("%+.2f", h[1] - prev);
            System.out.printf("%-6d %14.2f %12s%n", (int) h[0], h[1], chg);
            prev = h[1];
        }
    }

    private static void showTransactions(Portfolio p) {
        System.out.println("\n--- Transactions ---");
        if (p.getTransactions().isEmpty()) System.out.println("(none yet)");
        for (Transaction t : p.getTransactions()) System.out.println(t);
    }

    private static void save(DataStore store, Portfolio p, Market m) {
        try {
            store.save(p, m);
            System.out.println("Saved to " + SAVE_FILE);
        } catch (IOException e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    private static String ask(String prompt) {
        System.out.print(prompt);
        return sc.hasNextLine() ? sc.nextLine().trim() : "";
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!sc.hasNextLine()) return 9; // EOF -> behave like exit on menu
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }
}
