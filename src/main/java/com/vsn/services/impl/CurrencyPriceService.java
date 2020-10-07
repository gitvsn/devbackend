package com.vsn.services.impl;


import com.vsn.entities.wallets.Currency;
import com.vsn.utils.requests.RequestHelper;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class CurrencyPriceService {
    private String getSymbolId(String symbol) {
        symbol = symbol.toLowerCase();
        final String path = "https://api.coingecko.com/api/v3/coins/list";

        try {
            JSONArray resp = new JSONArray(RequestHelper.GET_Request(path));
            for (int i = 0; i < resp.length(); i++) {
                JSONObject jsonObject = resp.getJSONObject(i);
                if (jsonObject.getString("symbol").equals(symbol))
                    return jsonObject.getString("id");
            }
        } catch (JSONException jsonException) {
            log.error("Not search symbol a CoinGecko service " + symbol);
        }
        return  "";
    }

    public double getCurrencyPrice(Currency from, Currency to){
        if(from.equals(to)){
            return  1;
        }

        String fromSymbol = from.toString();

        final String symbolId = getSymbolId(fromSymbol);
        String path = "https://api.coingecko.com/api/v3/simple/price?ids="+symbolId+"&vs_currencies="+to.toString().toLowerCase();

        try {
            JSONObject resp = new JSONObject(RequestHelper.GET_Request(path));
            Double returnPrice = resp.getJSONObject(symbolId).getDouble(to.toString().toLowerCase());
            return  returnPrice;
        } catch (JSONException jsonException) {
            log.error("Not search price {} -> {} a CoinGecko service",from.toString(),to.toString());
            return 0;
        }
    }


    public double getBtcPrice(Currency currency){
       return  getPriceBTCsymbol(currency.toString());
    }
    public double getUsdPrice(Currency currency){
        return  getPriceUSDsymbol(currency.toString());
    }
    public double getUsdMarketCap(Currency currency){
        return  getMarketCapUSD(currency.toString());
    }

    private double getPriceBTCsymbol(String symbol){
        symbol = symbol.toLowerCase();
        final String symbolId = getSymbolId(symbol);
        String path = "https://api.coingecko.com/api/v3/simple/price?ids="+symbolId+"&vs_currencies=btc";

        try {
            JSONObject resp = new JSONObject(RequestHelper.GET_Request(path));
            Double returnPrice = resp.getJSONObject(symbolId).getDouble("btc");
            return  returnPrice;
        } catch (JSONException jsonException) {
            log.error(("Not search price  BTC "+symbol+" a CoinGecko service"));
            return 0;
        }
    }

    private double getPriceUSDsymbol(String symbol){
        symbol = symbol.toLowerCase();


        final String ids = getSymbolId(symbol);
        final String path = "https://api.coingecko.com/api/v3/simple/price?ids="+ids+"&vs_currencies=usd";

        try {
            JSONObject resp = new JSONObject(RequestHelper.GET_Request(path));
            return  resp.getJSONObject(ids).getDouble("usd");
        } catch (JSONException jsonException) {
            log.error("Not search price USD "+symbol+" a CoinGecko service");
            return 0;
        }
    }

    private double getMarketCapUSD(String symbol){
        symbol = symbol.toLowerCase();
        final String ids = getSymbolId(symbol);
        final String path = "https://api.coingecko.com/api/v3/simple/price?ids="+ids+"&vs_currencies=usd&include_market_cap=true";

        try {
            JSONObject resp = new JSONObject(RequestHelper.GET_Request(path));
            return  resp.getJSONObject(ids).getDouble("usd_market_cap");
        } catch (JSONException jsonException) {
            log.error("Not search market cap USD "+symbol+" a CoinGecko service");
            return 0;
        }
    }

    public double getPricePercentChange(@NotNull Currency from){
            Currency to = Currency.USDT;



            String fromSymbol = from.toString();
            final String symbolId = getSymbolId(fromSymbol);
            String path = "https://api.coingecko.com/api/v3/simple/price?ids="+symbolId+"&vs_currencies="+to.toString().toLowerCase()+"&include_24hr_change=true";

            try {
                JSONObject resp = new JSONObject(RequestHelper.GET_Request(path));
                Double returnPrice = resp.getJSONObject(symbolId).getDouble("usd_24h_change");
                return  returnPrice;
            } catch (JSONException jsonException) {
                log.error("Not search 24 h price {} -> {} a CoinGecko service",from.toString(),to.toString());
                return 0;
            }
    }


}
