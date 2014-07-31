package com.oanda.exchangerates.api.test;

import java.io.IOException;
import java.util.Iterator;

import com.oanda.exchangerates.api.ExchangeRatesClient;
import com.oanda.exchangerates.api.ExchangeRatesClient.CurrenciesResponse;
import com.oanda.exchangerates.api.ExchangeRatesClient.Quote;
import com.oanda.exchangerates.api.ExchangeRatesClient.RateFields;
import com.oanda.exchangerates.api.ExchangeRatesClient.RatesResponse;
import com.oanda.exchangerates.api.ExchangeRatesClient.RemainingQuotesResponse;

public class Example {
    private ExchangeRatesClient client = null;

    private void GetCurrencies(String dataSet) {
        try {
            CurrenciesResponse response = client.GetCurrencies(dataSet);
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

    private void GetRates(String baseCurrency, String[] quotes, RateFields[] fields, String decimalPlaces, String date, String start, String end, String dataSet) {
        try {
            RatesResponse response = client.GetRates(baseCurrency, quotes, fields, decimalPlaces, date, start, end, dataSet);
            if (response.isSuccessful) {
                System.out.println("GetRates: base currency=" + response.base_currency + " date=" + response.meta.effective_params.date
                        + " decimal places=" + response.meta.effective_params.decimal_places + " data_set=" + response.meta.effective_params.data_set);
                for (int i = 0 ; i < response.meta.effective_params.fields.length ; i++) {
                    System.out.println("fields[" + i + "]=" + response.meta.effective_params.fields[i]);
                }
                for (int i = 0 ; i < response.meta.effective_params.quote_currencies.length ; i++) {
                    System.out.println("quote_currencies[" + i + "]=" + response.meta.effective_params.quote_currencies[i]);
                }
                System.out.println("meta.request_time=" + response.meta.request_time);
                for (int i = 0 ; i < response.meta.skipped_currencies.length ; i++) {
                    System.out.println("skipped_currencies[" + i + "]=" + response.meta.skipped_currencies[i]);
                }

                Iterator<String> iterator = response.quotes.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Quote quote = response.quotes.get(key);
                    System.out.println(key + " : ask=" + quote.ask + " bid=" + quote.bid + " spot=" + quote.spot + " date=" + quote.date);
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
        GetCurrencies(null);
        GetRemainingQuotes();
        GetRates("USD", new String[] {"AUD", "CHF"}, null, null, null, null, null, null);
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
