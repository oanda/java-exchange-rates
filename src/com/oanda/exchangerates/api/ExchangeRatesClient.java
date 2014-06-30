package com.oanda.exchangerates.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public class CurrenciesResponse extends ApiResponse {
        public Currency[] currencies;
    }

    public class RemainingQuotesResponse extends ApiResponse {
        public String remaining_quotes;
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

    public ApiResponse SendRequest(String requestName) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = String.format("%s%s", base_url, requestName);
        String authHeader = String.format("Bearer %s", api_key);
        System.out.println(url);
        ApiResponse response = new ApiResponse();

        try {
            HttpUriRequest httpGet = new HttpGet(url);
            httpGet.setHeader(new BasicHeader("Authorization", authHeader));

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

    public void GetRates() {
        
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
