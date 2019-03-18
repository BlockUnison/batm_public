package com.generalbytes.batm.common.adapters

import java.util

import cats._
import cats.implicits._
import com.generalbytes.batm.common.Adapters.{Address, Amount, TransactionId}
import com.generalbytes.batm.common.domain.Interpreter
import com.generalbytes.batm.common.Adapters
import com.generalbytes.batm.common.domain.Wallet
import com.generalbytes.batm.server.extensions.IWallet

import scala.collection.JavaConverters._
import scala.collection.mutable

class WalletAdapter[F[_] : Applicative : Interpreter](wallet: Wallet[F]) extends IWallet {
  private val interpret = implicitly[Interpreter[F]]

  override def sendCoins(address: Adapters.Address, amount: Adapters.Amount, currency: Adapters.CryptoCurrency, desc: String): TransactionId =
    interpret(wallet.issuePayment(address, BigDecimal(amount), desc))

  override def getCryptoAddress(cryptoCurrency: Adapters.CryptoCurrency): Adapters.Address =
    interpret(wallet.getAddress)

  override def getCryptoBalance(cryptoCurrency: Adapters.CryptoCurrency): Adapters.Amount=
    interpret(wallet.getBalance.map(_.bigDecimal))

  override def getCryptoCurrencies: util.Set[Adapters.CryptoCurrency] = mutable.Set(wallet.cryptoCurrency.name).asJava

  override def getPreferredCryptoCurrency: Adapters.CryptoCurrency = wallet.cryptoCurrency.name
}
