package com.generalbytes.batm.server.extensions.extra.decent.factories

import cats.implicits._
import com.generalbytes.batm.common.adapters.WalletAdapter
import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.factories.WalletFactory
import com.generalbytes.batm.server.extensions.IWallet
import com.generalbytes.batm.server.extensions.extra.decent.wallets.dctd.DecentWalletRestApi
import org.http4s.Uri

trait DecentHotWalletFactory extends WalletFactory {
  private val walletLoginData = """dctd:(https?):([A-Za-z0-9]+):([A-Za-z0-9\.]+):([A-Za-z0-9.]+):([0-9]+)""".r

  override def createWallet(loginInfo: String): Attempt[IWallet] = loginInfo match {
    case walletLoginData(protocol, user, password, hostname, port) =>
      Uri.fromString(s"$protocol://$hostname:$port")
        .map { uri =>
          new WalletAdapter(
            new DecentWalletRestApi(uri, DecentWalletRestApi.DecentWalletCredentials(user, password))
          )
        }
    case _ => err"Login info ($loginInfo) did not match the expected format".asLeft
  }
}
