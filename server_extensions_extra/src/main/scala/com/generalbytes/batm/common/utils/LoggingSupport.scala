package com.generalbytes.batm.common.utils

import org.slf4j.{Logger, LoggerFactory}

trait LoggingSupport { self =>
  implicit lazy val logger: Logger = LoggerFactory.getLogger(self.getClass)
}
