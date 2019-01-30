/*************************************************************************************
 * Copyright (C) 2014-2018 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.bitcoincash;

import com.generalbytes.batm.server.extensions.*;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.exchanges.bch_coinmate.CoinmateXChangeExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.sources.bch_coinmate.CoinmateXChangeRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.wallets.BATMBitcoinCashdRPCWallet;
import com.generalbytes.batm.server.extensions.extra.coinmate.CoinmateLoginInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

public class BitcoinCashExtension extends AbstractExtension {
//    private static final CryptoCurrencyDefinition DEFINITION = new BitcoinCashDefinition();
    public static final String CURRENCY = Currencies.BCH;
    private static final Logger log = LoggerFactory.getLogger("BitcoinCashExtension");

    @Override
    public String getName() {
        return "BATM Bitcoin Cash extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken();
            if ("bitcoincashd".equalsIgnoreCase(walletType)) {
                //"bitcoind:protocol:user:password:ip:port:accountname"

                String protocol = st.nextToken();
                String username = st.nextToken();
                String password = st.nextToken();
                String hostname = st.nextToken();
                String port = st.nextToken();
                String accountName = "";
                if (st.hasMoreTokens()) {
                    accountName = st.nextToken();
                }


                if (protocol != null && username != null && password != null && hostname != null && port != null && accountName != null) {
                    String rpcURL = protocol + "://" + username + ":" + password + "@" + hostname + ":" + port;
                    return new BATMBitcoinCashdRPCWallet(rpcURL, accountName, Currencies.BCH);
                }
            }
        }
        return null;
    }

    @Override
    public IRateSource createRateSource(String paramString) {
        CoinmateLoginInfo loginInfo = parseLoginInfo(paramString);
        if (loginInfo != null) {
            return new CoinmateXChangeRateSource(loginInfo);
        } else return null;
    }

    @Override
    public IExchange createExchange(String paramString) {
        CoinmateLoginInfo loginInfo = parseLoginInfo(paramString);
        if (loginInfo != null) {
            return new CoinmateXChangeExchange(loginInfo);
        } else return null;
    }

    private CoinmateLoginInfo parseLoginInfo(String paramString) {
        if ((paramString != null) && (!paramString.trim().isEmpty()))
        {
            StringTokenizer paramTokenizer = new StringTokenizer(paramString, ":");
            String prefix = paramTokenizer.nextToken();
            log.debug("paramString: " + paramString);

            if ("coinmate_bch".equalsIgnoreCase(prefix)) {
                String clientId = paramTokenizer.nextToken();
                String publicKey = paramTokenizer.nextToken();
                String privateKey = paramTokenizer.nextToken();
                String preferredFiatCurrency = Currencies.EUR;

                return new CoinmateLoginInfo(clientId, publicKey, privateKey);
            }
        }
        return null;
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(CURRENCY);
        return result;
    }

    //Disable for now
//    @Override
//    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
//        Set<ICryptoCurrencyDefinition> result = new HashSet<>();
//        result.add(DEFINITION);
//        return result;
//    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CURRENCY.equalsIgnoreCase(cryptoCurrency)) {
            return new BitcoinCashAddressValidator();
        }
        return null;
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        return new BitcoinCashWalletGenerator("qqqq", ctx);
    }
}
