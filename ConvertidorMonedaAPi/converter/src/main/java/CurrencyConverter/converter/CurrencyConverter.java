package CurrencyConverter.converter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import org.json.JSONObject;
	
	public class CurrencyConverter {
	

		private static final String API_URL = "https://v6.exchangerate-api.com/v6/bd7ffee01d88e1314b123969/latest/";


	    private static final Map<String, String> currencies = new HashMap<>();
	    static {
	        currencies.put("USD", "Dolar Americano");
	        currencies.put("EUR", "Euro");
	        currencies.put("GBP", "Liras");
	        currencies.put("JPY", "Yenes");
	        currencies.put("CLP", "Pesos Chilenos");
	        currencies.put("ARS", "Pesos Argentinos");
	    }

	    public static void main(String[] args) {
	        Scanner scanner = new Scanner(System.in);
	        boolean continuar = true;

	        while (continuar) {
	            try {
	                System.out.println("Seleccione la moneda de origen:");
	                printCurrencyMenu();

	                String fromCurrency = selectCurrency(scanner);

	                System.out.print("Ingrese la cantidad a convertir: ");
	                double amount = scanner.nextDouble();
	                scanner.nextLine();  // Consume newline

	                System.out.println("Seleccione la moneda de destino:");
	                printCurrencyMenu();

	                String toCurrency = selectCurrency(scanner);

	                convertCurrency(fromCurrency, amount, toCurrency);

	                System.out.println("\n1. Convertir otra vez");
	                System.out.println("2. Salir");
	                System.out.print("Elija una opción: ");
	                int opcion = scanner.nextInt();

	                if (opcion == 2) {
	                    continuar = false;
	                }

	            } catch (InputMismatchException e) {
	                System.out.println("Entrada no válida. Asegúrese de ingresar el tipo de dato correcto.");
	                scanner.nextLine();
	            } catch (IOException | InterruptedException e) {
	                System.out.println("Hubo un problema al obtener los datos de la API: " + e.getMessage());
	            }
	        }

	        System.out.println("Programa finalizado.");
			scanner.close();
		}

	    private static void printCurrencyMenu() {
	        for (Map.Entry<String, String> entry : currencies.entrySet()) {
	            System.out.printf("%s - %s%n", entry.getKey(), entry.getValue());
	        }
	    }

	    private static String selectCurrency(Scanner scanner) {
	        String currency;
	        do {
	            System.out.print("Ingrese el código de la moneda: ");
	            currency = scanner.next().toUpperCase();
	        } while (!currencies.containsKey(currency));
	        return currency;
	    }

	    private static void convertCurrency(String fromCurrency, double amount, String toCurrency) throws IOException, InterruptedException {
	        HttpClient client = HttpClient.newHttpClient();
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(API_URL + fromCurrency))
	                .build();

	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

	        if (response.statusCode() == 200) {
	            JSONObject ratesObject = new JSONObject(response.body()).getJSONObject("conversion_rates");

	            if (ratesObject.has(toCurrency)) {
	                double exchangeRate = ratesObject.getDouble(toCurrency);
	                double convertedAmount = amount * exchangeRate;
	                String fromCurrencyName = currencies.get(fromCurrency);
	                String toCurrencyName = currencies.get(toCurrency);
	                System.out.printf("%.2f %s (%s) equivale a %.2f %s (%s)%n", amount, fromCurrency, fromCurrencyName, convertedAmount, toCurrency, toCurrencyName);
	            } else {
	                System.out.println("La moneda de destino ingresada no es válida.");
	            }
	        } else {
	            System.out.println("Hubo un problema al obtener los datos de la API.");
	        }
	    }
	}

	