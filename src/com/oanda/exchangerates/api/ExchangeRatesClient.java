package com.oanda.exchangerates.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;



public class ExchangeRatesClient {

    public class Currency {
        public String code;
        public String description;
    }

    public class Quote {
        public String ask;
        public String bid;
        public String date;
        public String high_ask;
        public String high_bid;
        public String low_ask;
        public String low_bid;
        public String midpoint;
    }

    public class EffectiveParams {
        public int decimal_places;
        public String[] fields;
        public String[] quote_currencies;
        public String date;
    }

    public class Meta {
        public EffectiveParams effective_params;
        public String[] skipped_currencies;
        public String request_time;
    }

    public class CurrenciesResponse extends ApiResponse {
        public Currency[] currencies;
    }

    public class RemainingQuotesResponse extends ApiResponse {
        public String remaining_quotes;
    }

    public class RatesResponse extends ApiResponse {
        public String base_currency;
        public Meta meta;
        public Map<String, Quote> quotes;
    }

    public enum RateFields {
        all("all"),
        averages("averages"),
        midpoint("midpoint"),
        highs("highs"),
        lows("lows");

        private final String text;

        private RateFields(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    public class ApiResponse {
        public boolean isSuccessful = false;
        public String errorMessage = null;
        public String rawJsonResponse = null;

        public void Copy(ApiResponse copyFrom) {
            isSuccessful = copyFrom.isSuccessful;
            errorMessage = copyFrom.errorMessage;
            rawJsonResponse = copyFrom.rawJsonResponse;
        }
    }

    private String base_url = "https://www.oanda.com/rates/api/v1/";
    private String api_key = null;
    private String proxy_url = null;
    private int proxy_port = 0;

    public ExchangeRatesClient(String apiKey) {
        this(apiKey, null, null, 0);
    }

    public ExchangeRatesClient(String apiKey, String baseUrl) {
        this(apiKey, baseUrl, null, 0);
    }

    public ExchangeRatesClient(String apiKey, String proxyUrl, int proxyPort) {
        this(apiKey, null, proxyUrl, proxyPort);
    }

    public ExchangeRatesClient(String apiKey, String baseUrl, String proxyUrl, int proxyPort) {
        api_key = apiKey;
        if (baseUrl != null) {
            base_url = baseUrl;
        }

        if (proxyUrl != null) {
            proxy_url = proxyUrl;
            proxy_port = proxyPort;
        }
    }

    private void AppendToQueryString(StringBuilder sb, String key, String value) {
        if (sb.length() == 0) {
            sb.append("?");
        }
        else {
            sb.append("&");
        }
        sb.append(key + "=" + value);
    }

    private String GetRatesParameterQueryString(String[] quotes, RateFields[] fields, String decimalPlaces, String date, String start, String end) {
        StringBuilder queryStringBuilder = new StringBuilder();

        if (quotes != null) {
            for (int i = 0 ; i < quotes.length ; i++) {
                String quote = quotes[i];
                if ((quote != null) && (quote.trim().length() != 0)) {
                    AppendToQueryString(queryStringBuilder, "quote", quote);
                }
            }
        }

        if (fields != null) {
            for (int i = 0 ; i < fields.length ; i++) {
                String fieldValue = fields[i].toString();
                AppendToQueryString(queryStringBuilder, "fields", fieldValue);
            }
        }

        if ((date != null) && (date.trim().length() != 0)) {
            AppendToQueryString(queryStringBuilder, "date", date);
        }

        if ((start != null) && (start.trim().length() != 0)) {
            AppendToQueryString(queryStringBuilder, "start", start);
        }

        if ((end != null) && (end.trim().length() != 0)) {
            AppendToQueryString(queryStringBuilder, "end", end);
        }

        return queryStringBuilder.toString();
    }

    private ApiResponse SendRequest(String requestName) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = String.format("%s%s", base_url, requestName);
        String authHeader = String.format("Bearer %s", api_key);
        System.out.println(url);
        ApiResponse response = new ApiResponse();

        try {
            HttpUriRequest httpGet = new HttpGet(url);
            httpGet.setHeader(new BasicHeader("Authorization", authHeader));
            httpGet.setHeader("User-Agent", "OANDAExchangeRates.Java/0.01");

            //still needs work & testing for proxy.
//            if (proxy_url != null) {
//                HttpHost proxy = new HttpHost(proxy_url, proxy_port);
//                httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
//            }
            
            System.out.println("Executing request: " + httpGet.getRequestLine());

            HttpResponse resp = httpClient.execute(httpGet);
            HttpEntity entity = resp.getEntity();

            if (resp.getStatusLine().getStatusCode() == 200 && entity != null) {
                InputStream stream = entity.getContent();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                StringBuilder jsonBuilder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    jsonBuilder.append(line);
                }
                response.isSuccessful = true;
                response.rawJsonResponse = jsonBuilder.toString();
            } else {
                // print error message
                String responseString = EntityUtils.toString(entity, "UTF-8");
                response.errorMessage = responseString;
            }
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return response;
    }

    public CurrenciesResponse GetCurrencies() throws IOException {
        ApiResponse response = SendRequest("currencies.json");
        CurrenciesResponse curResponse;
        if (response.isSuccessful) {
            Gson gson = new Gson();
            curResponse = gson.fromJson(response.rawJsonResponse, CurrenciesResponse.class);
        }
        else {
            curResponse = new CurrenciesResponse();
        }

        curResponse.Copy(response);

        return curResponse;
    }

    public RatesResponse GetRates(String baseCurrency, String[] quotes, RateFields[] fields, String decimalPlaces, String date, String start, String end) throws IOException {
        String queryStr = GetRatesParameterQueryString(quotes, fields, decimalPlaces, date, start, end);
        String requestStr = String.format("rates/%s.json%s", baseCurrency, queryStr);

        ApiResponse response = SendRequest(requestStr);
        RatesResponse ratesResponse;
        if (response.isSuccessful) {
            Gson gson = new Gson();
            ratesResponse = gson.fromJson(response.rawJsonResponse, RatesResponse.class);
        }
        else {
            ratesResponse = new RatesResponse();
        }

        ratesResponse.Copy(response);

        return ratesResponse;
    }

    public RemainingQuotesResponse GetRemainingQuotes() throws IOException {
        ApiResponse response = SendRequest("remaining_quotes.json");
        RemainingQuotesResponse remainingQuotesResponse;
        if (response.isSuccessful) {
            Gson gson = new Gson();
            remainingQuotesResponse = gson.fromJson(response.rawJsonResponse, RemainingQuotesResponse.class);
        }
        else {
            remainingQuotesResponse = new RemainingQuotesResponse();
        }

        remainingQuotesResponse.Copy(response);

        return remainingQuotesResponse;
    }
}
