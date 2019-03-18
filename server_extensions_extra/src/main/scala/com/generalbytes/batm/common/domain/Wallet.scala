package com.generalbytes.batm.common.domain

trait Wallet[F[_]] {
  val cryptoCurrency: CryptoCurrency
  def getAddress: F[Address]
  def issuePayment(recipientAddress: Address, amount: Amount, description: String = ""): F[Identifier]
  def getBalance: F[Amount]
}
