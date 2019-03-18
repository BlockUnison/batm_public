package com.generalbytes.batm.common.factories

import cats.implicits._
import com.generalbytes.batm.common.adapters.WalletAdapter
import com.generalbytes.batm.common.domain.{Attempt, Task}
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.wallets.DummyWallet
import com.generalbytes.batm.server.extensions.IWallet

trait DummyWalletFactory extends WalletFactory {
  def createWallet(loginInfo: String): Attempt[IWallet] = new WalletAdapter[Task](new DummyWallet()).asRight[Throwable]
}
