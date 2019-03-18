package com.generalbytes.batm.common.wallets

import com.generalbytes.batm.common.domain._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.utils.Util._

class DummyWallet[F[_]: ErrorApplicative] extends Wallet[F] {
  private def raiseNotImpl[A]: F[A] = raise[F](err"Not implemented")

  override val cryptoCurrency: CryptoCurrency = null

  override def getAddress: F[Address] = raiseNotImpl

  override def issuePayment(recipientAddress: Address, amount: Amount, description: String): F[Identifier] = raiseNotImpl

  override def getBalance: F[Amount] = raiseNotImpl
}
