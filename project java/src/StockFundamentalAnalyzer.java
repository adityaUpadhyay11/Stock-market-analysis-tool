import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class StockFundamentalAnalyzer {

    // Your Alpha Vantage API key
    private static final String API_KEY = "MI44R8V18YH3JBTV";

    // Fetch stock data from Alpha Vantage API
    public static String fetchStockData(String symbol) {
        String urlString = "https://www.alphavantage.co/query?function=OVERVIEW&symbol=" + symbol + "&apikey=" + API_KEY;
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();

            // Print the full API response for debugging
            System.out.println("API Response: " + content.toString());

            return content.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Analyze stock fundamentals and determine if the stock is "Good" or "Poor"
    public static void analyzeStockFundamentals(String jsonData) {
        if (jsonData == null || jsonData.isEmpty()) {
            System.out.println("No data found. Please check the stock symbol and try again.");
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Check and extract financial data only if the keys exist
            double earningsPerShare = jsonObject.has("EPS") ? jsonObject.getDouble("EPS") : 0.0;
            double price = jsonObject.has("52WeekHigh") ? jsonObject.getDouble("52WeekHigh") : 0.0;
            double roe = jsonObject.has("ReturnOnEquityTTM") ? jsonObject.getDouble("ReturnOnEquityTTM") : 0.0;
            double debtToEquity = jsonObject.has("DebtToEquityRatio") ? jsonObject.getDouble("DebtToEquityRatio") : 0.0;

            // Calculate P/E Ratio, ensure no division by zero
            double peRatio = (earningsPerShare > 0) ? (price / earningsPerShare) : 0.0;

            // Print out the fundamental metrics
            System.out.println("P/E Ratio: " + peRatio);
            System.out.println("ROE: " + roe);
            System.out.println("Debt-to-Equity Ratio: " + debtToEquity);

            // Simple decision-making for stock classification
            if (peRatio < 20 && roe > 15 && debtToEquity < 1) {
                System.out.println("The stock has good fundamentals.");
            } else {
                System.out.println("The stock may not have strong fundamentals.");
            }

        } catch (Exception e) {
            System.out.println("Error parsing JSON data.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the stock symbol: ");
        String stockSymbol = scanner.nextLine();  // Example: AAPL, MSFT, NTPC

        // Fetch and analyze stock data
        String stockData = fetchStockData(stockSymbol);
        if (stockData != null) {
            analyzeStockFundamentals(stockData);
        } else {
            System.out.println("Could not fetch stock data. Please check your API key or stock symbol.");
        }
    }
}

