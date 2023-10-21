import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class CurrencyConverter {
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your ExchangeRatesAPI key
    private static final String API_BASE_URL = "https://api.apilayer.com/exchangerates_data/latest";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Currency Converter");
        System.out.print("Enter the amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter the base currency (e.g., USD, EUR): ");
        String baseCurrency = scanner.nextLine().toUpperCase();

        System.out.print("Enter the target currency (e.g., USD, EUR): ");
        String targetCurrency = scanner.nextLine().toUpperCase();

        try {
            double conversionRate = getExchangeRate(baseCurrency, targetCurrency);
            if (conversionRate >= 0) {
                double convertedAmount = amount * conversionRate;
                System.out.println(amount + " " + baseCurrency + " is equivalent to " + convertedAmount + " " + targetCurrency);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to fetch exchange rates.");
        }

        scanner.close();
    }

    private static double getExchangeRate(String baseCurrency, String targetCurrency) throws IOException {
        String urlStr = API_BASE_URL + "?base=" + baseCurrency + "&symbols=" + targetCurrency;
        URL url = new URL(urlStr);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Api-Key", API_KEY);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            if (jsonResponse.has("rates")) {
                JSONObject rates = jsonResponse.getJSONObject("rates");
                return rates.getDouble(targetCurrency);
            } else {
                System.out.println("No exchange rate data available for the provided currencies.");
                return -1.0;
            }
        } else {
            System.out.println("Failed to fetch exchange rates. Response code: " + responseCode);
            return -1.0;
        }
    }
}
