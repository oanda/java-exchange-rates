package com.oanda.exchangerates.api.test;

import java.io.IOException;

import com.oanda.exchangerates.api.ExchangeRatesClient;
import com.oanda.exchangerates.api.ExchangeRatesClient.CurrenciesResponse;
import com.oanda.exchangerates.api.ExchangeRatesClient.RemainingQuotesResponse;

public class Example {
    private ExchangeRatesClient client = null;

    private void GetCurrencies() {
        try {
            CurrenciesResponse response = client.GetCurrencies();
            if (response.isSuccessful) {
                if (response.currencies != null) {
                    for (int i = 0 ; i < response.currencies.length ; i++) {
                        System.out.println(response.currencies[i].code + ":" + response.currencies[i].description);
                    }
                }
            }
            else {
                System.out.println(response.errorMessage);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void GetRemainingQuotes() {
        try {
            RemainingQuotesResponse response = client.GetRemainingQuotes();
            if (response.isSuccessful) {
                if (response.remaining_quotes != null) {
                    System.out.println("Remaining Quotes: " + response.remaining_quotes);
                }
            }
            else {
                System.out.println(response.errorMessage);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void init(String api_key) {
        client = new ExchangeRatesClient(api_key);
        GetCurrencies();
        GetRemainingQuotes();
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            String api_key = args[0];
            Example self = new Example();
            self.init(api_key);
        }
        else {
            System.out.println("\nUsage: java " + Example.class.getName() + " <api_key>\n");
            System.exit(1);
        }
    }
}
