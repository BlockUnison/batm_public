package com.generalbytes.batm.server.extensions.extra.decent.wallets.dctd

import cats.Applicative
import com.generalbytes.batm.common.domain.Interpreter
import com.generalbytes.batm.common.adapters.WalletAdapter
import com.generalbytes.batm.common.domain.Wallet

class DecentWalletAdapter[F[_] : Interpreter : Applicative](client: Wallet[F]) extends WalletAdapter[F](client)
