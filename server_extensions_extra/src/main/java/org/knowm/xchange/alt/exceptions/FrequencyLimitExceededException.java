package org.knowm.xchange.alt.exceptions;

/** An exception indicating the request rate limit has been exceeded */
public class FrequencyLimitExceededException extends ExchangeException {

  public FrequencyLimitExceededException(String message) {
    super(message);
  }

  public FrequencyLimitExceededException() {
    super("Too many attempts made in a given time window.");
  }
}
