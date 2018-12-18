package com.generalbytes.batm.server.extensions.bitcoincash;

import com.generalbytes.batm.server.extensions.IExchange;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.BitcoinCashExtension;
import org.junit.Ignore;
import org.junit.Test;
import org.knowm.xchange.utils.Assert;

import java.math.BigDecimal;

public class BitcoinCashExtensionTest {

    @Test
    @Ignore
    public void TestCoinmateSell() {
        String xchgLoginInfo = "coinmate_bch:34813:k0w_LMZzhZVLdsvww5ej5PQCoZmxDNRY1mFOvRFMQNM:ufjJ1vmCQBrp5HxymX76XbvuMCsVIm0n_MZcmM0qm1M";

        IExchange exchange = new BitcoinCashExtension().createExchange(xchgLoginInfo);
        String txId = exchange.sellCoins(BigDecimal.valueOf(0.13), "BCH", "EUR", "");

        Assert.notNull(txId, "TxId is null");
    }
}
