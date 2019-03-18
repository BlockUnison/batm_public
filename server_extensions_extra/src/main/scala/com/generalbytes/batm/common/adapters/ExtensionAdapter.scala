package com.generalbytes.batm.common.adapters

import java.util

import cats.Applicative
import com.generalbytes.batm.common.domain.{Extension, Interpreter}
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.utils.LoggingSupport
import com.generalbytes.batm.common.utils.Util._
import com.generalbytes.batm.server.extensions._
import com.generalbytes.batm.server.extensions.watchlist.IWatchList

class ExtensionAdapter(ext: Extension)
  extends IExtension with LoggingSupport {

  override def getName: String = ext.name

  override def getSupportedCryptoCurrencies: util.Set[String] = ext.supportedCryptoCurrencies.map(_.name).toJavaSet

  override def createExchange(loginInfo: String): IExchange = attempt2Null(ext.createExchange(loginInfo)) |> (x => log(x, "Create exchange"))

  override def createPaymentProcessor(s: String): IPaymentProcessor = null

  override def createRateSource(loginInfo: String): IRateSource = attempt2Null(ext.createRateSource(loginInfo)) |> (x => log(x, "Create rate source"))

  override def createWallet(loginInfo: String): IWallet = attempt2Null(ext.createWallet(loginInfo))

  override def createAddressValidator(cryptoCurrency: String): ICryptoAddressValidator = attempt2Null(ext.createAddressValidator) |> (x => log(x, "Create validator"))

  override def createPaperWalletGenerator(s: String): IPaperWalletGenerator = null

  override def getSupportedWatchListsNames: util.Set[String] = Set.empty[String].toJavaSet

  override def getWatchList(s: String): IWatchList = null

  override def init(iExtensionContext: IExtensionContext): Unit = ()

  override def getCryptoCurrencyDefinitions: util.Set[ICryptoCurrencyDefinition] = Set.empty[ICryptoCurrencyDefinition].toJavaSet

  override def getRestServices: util.Set[IRestService] = Set.empty[IRestService].toJavaSet

  override def getChatCommands: util.Set[Class[_]] = Set.empty[Class[_]].toJavaSet
}
