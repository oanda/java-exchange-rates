package com.oanda.exchangerates.api.test;

import java.io.IOException;

import com.oanda.exchangerates.api.ExchangeRatesClient;
import com.oanda.exchangerates.api.ExchangeRatesClient.CurrenciesResponse;

public class Example {
    private ExchangeRatesClient client = null;

    private void GetCurrencies() {
        try {
            CurrenciesResponse response = client.GetCurrencies();
            if (response.IsSuccessful) {
                if (response.currencies != null) {
                    for (int i = 0 ; i < response.currencies.length ; i++) {
                        System.out.println(response.currencies[i].code + ":" + response.currencies[i].description);
                    }
                }
            }
            else {
                System.out.println(response.ErrorMessage);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void init(String api_key) {
        client = new ExchangeRatesClient(api_key);
        GetCurrencies();
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
