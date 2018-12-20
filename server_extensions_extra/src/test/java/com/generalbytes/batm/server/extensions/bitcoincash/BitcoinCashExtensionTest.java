package com.generalbytes.batm.server.extensions.bitcoincash;

import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.BitcoinCashExtension;
import org.junit.Ignore;
import org.junit.Test;
import org.knowm.xchange.alt.utils.Assert;

import java.math.BigDecimal;

public class BitcoinCashExtensionTest {

    @Test
    @Ignore
    public void TestCoinmateSell() {
        String xchgLoginInfo = "coinmate_bch:34813:k0w_LMZzhZVLdsvww5ej5PQCoZmxDNRY1mFOvRFMQNM:ufjJ1vmCQBrp5HxymX76XbvuMCsVIm0n_MZcmM0qm1M";

        IRateSource rateSource = new BitcoinCashExtension().createRateSource(xchgLoginInfo);
        IExchange exchange = new BitcoinCashExtension().createExchange(xchgLoginInfo);
        String txId1 = exchange.purchaseCoins(BigDecimal.valueOf(0.13), "BCH", "EUR", "");
        String txId2 = exchange.sellCoins(BigDecimal.valueOf(0.13), "BCH", "EUR", "");

        Assert.notNull(rateSource, "RS is null");
        Assert.notNull(txId1, "TxId is null");
        Assert.notNull(txId2, "TxId is null");
    }
}
