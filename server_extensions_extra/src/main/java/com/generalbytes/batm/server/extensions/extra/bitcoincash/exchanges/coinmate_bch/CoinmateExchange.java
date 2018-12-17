package com.generalbytes.batm.server.extensions.extra.bitcoincash.exchanges.coinmate_bch;

import com.generalbytes.batm.server.extensions.Currencies;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange;
import org.knowm.xchange.ExchangeSpecification;

import java.util.HashSet;
import java.util.Set;

public class CoinmateExchange extends XChangeExchange {

    private static ExchangeSpecification getDefaultSpecification() {
        return new org.knowm.xchange.coinmate.CoinmateExchange().getDefaultExchangeSpecification();
    }

    public CoinmateExchange(String clientId, String publicKey, String privateKey, String preferredFiatCurrency) {
        super(getSpecification(clientId, publicKey, privateKey), preferredFiatCurrency);
    }

    private static ExchangeSpecification getSpecification(String clientId, String publicKey, String privateKey) {
        ExchangeSpecification spec = getDefaultSpecification();
        spec.setUserName(clientId);
        spec.setApiKey(publicKey);
        spec.setSecretKey(privateKey);
        return spec;
    }

    @Override
    protected boolean isWithdrawSuccessful(String result) {
        return true;
    }

    @Override
    protected double getAllowedCallsPerSecond() {
        return 10;
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> cryptoCurrencies = new HashSet<>();
        cryptoCurrencies.add(Currencies.BCH);
        cryptoCurrencies.add(Currencies.BTC);
        return cryptoCurrencies;
    }

    @Override
    public Set<String> getFiatCurrencies() {
        Set<String> fiatCurrencies = new HashSet<>();
        fiatCurrencies.add(Currencies.EUR);
        return fiatCurrencies;
    }
}
