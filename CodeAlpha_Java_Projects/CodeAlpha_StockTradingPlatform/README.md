# CodeAlpha_StockTradingPlatform

Java internship **Task 2** at CodeAlpha: a console Stock Trading simulation.

## Features
- Market data display for 6 stocks with price and % change
- Buy / sell with balance and holdings checks (starting cash: 100000)
- Portfolio view: quantity, average cost, current value and profit/loss per stock
- "Advance to next day" simulates random price moves (about -5% to +5%)
- Portfolio performance history (total value per day) and full transaction history
- File I/O persistence: save and reload portfolio, prices, transactions and history (`portfolio_data.txt`)

## OOP design
| Class | Role |
|---|---|
| `Stock` | symbol, name, price, random move |
| `Market` | all stocks, day counter, display |
| `Transaction` | one BUY/SELL record |
| `Portfolio` | cash, holdings, transactions, value history |
| `DataStore` | save / load to text file |
| `StockTradingPlatform` | console menu (main) |

## Run
```bash
cd src
javac *.java
java StockTradingPlatform
```
Requires JDK 8 or newer. All data is simulated; no real market data or money.
