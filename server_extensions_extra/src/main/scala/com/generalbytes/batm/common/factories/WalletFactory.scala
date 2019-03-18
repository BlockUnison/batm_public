package com.generalbytes.batm.common.factories

import com.generalbytes.batm.common.domain.Attempt
import com.generalbytes.batm.server.extensions.IWallet

trait WalletFactory {
  def createWallet(loginInfo: String): Attempt[IWallet]
}
